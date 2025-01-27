package app.xmum.xplorer.backend.groupbooking.service;

import app.xmum.xplorer.backend.groupbooking.config.RedisConstant;
import app.xmum.xplorer.backend.groupbooking.enums.ActivityAttendanceEnum;
import app.xmum.xplorer.backend.groupbooking.enums.ActivityStatusEnum;
import app.xmum.xplorer.backend.groupbooking.exception.ActivityException;
import app.xmum.xplorer.backend.groupbooking.mapper.ActivityMapper;
import app.xmum.xplorer.backend.groupbooking.pojo.ActivityPO;
import app.xmum.xplorer.backend.groupbooking.pojo.HotActivityPO;
import app.xmum.xplorer.backend.groupbooking.response.ApiResponse;
import app.xmum.xplorer.backend.groupbooking.enums.ErrorCode;
import app.xmum.xplorer.backend.groupbooking.utils.RedisUtil;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class ActivityService {

    @Autowired
    private ActivityMapper activityMapper;
    @Autowired
    private HotActivityService hotActivityService;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private VisitLogService visitLogService;
    @Autowired
    private ActivityAttendanceService activityAttendanceService;

    public ApiResponse<ActivityPO> findByAId(ActivityPO activityPO,String  uid) {
        try {
            if (redisUtil.hasKey(RedisConstant.ACTIVITY_VISIT_KEY + activityPO.getActivityUuid())) {
                redisUtil.increase(RedisConstant.ACTIVITY_VISIT_KEY + activityPO.getActivityUuid());
            } else {
                redisUtil.set(RedisConstant.ACTIVITY_VISIT_KEY + activityPO.getActivityUuid(), "0");
            }
        } catch (Exception e) {
            log.error("redis访问失败", e);
        }
        try {
            visitLogService.createVisitLog(activityPO, uid);
        } catch (Exception e) {
           return ApiResponse.fail(ErrorCode.BAD_REQUEST, "浏览记录插入失败");
        }

        return ApiResponse.success(activityMapper.findByUid(activityPO.getActivityUuid()));

    }

    public ApiResponse<?> insert(ActivityPO activityPO) {
        if (redisUtil.hasKey(RedisConstant.ACTIVITY_CREATE_KEY + activityPO.getActivityName())) {
            return ApiResponse.fail(ErrorCode.BAD_REQUEST, "活动已创建");
        }
        //HACK: 后续插入检查，以及其他数据拼接
        activityPO.setActivityUuid(UUID.randomUUID().toString().replace("-", ""));
        // 活动发起者自己也需要参加活动才能计入
        activityPO.setActivityPersonNow(0);
        activityPO.setActivityVisitNum(0L);
        activityPO.setActivityCollectNum(0);
        activityPO.setCommentCount(0);
        activityPO.setActivityHeat(heatCalculate(activityPO));
        if(activityPO.getActivityRegisterStartTime().isAfter(LocalDateTime.now())){
            activityPO.setActivityStatus(ActivityStatusEnum.NOT_STARTED);
        } else if (activityPO.getActivityRegisterEndTime().isAfter(LocalDateTime.now())) {
            activityPO.setActivityStatus(ActivityStatusEnum.REGISTERING);
        }
        activityMapper.insert(activityPO);
        redisUtil.set(RedisConstant.ACTIVITY_CREATE_KEY + activityPO.getActivityName(), RedisConstant.ACTIVITY_STATUS_ACTIVATE, RedisConstant.ACTIVITY_CREATED_TTL,TimeUnit.MILLISECONDS);
        return ApiResponse.success(activityPO);
    }

    //参加活动
    public ApiResponse<?> joinActivity(ActivityPO activityPO, String uid) {
        if (activityPO.getActivityPersonNow() > activityPO.getActivityPersonMax()) {
            log.error("活动人数超过最大限制");
            return ApiResponse.fail(ErrorCode.BAD_REQUEST, "活动人数已满");
        }
        try {
            activityMapper.joinActivity(activityPO.getActivityUuid());
            activityAttendanceService.addAttendance(activityPO.getActivityUuid(), uid);
        } catch (Exception e) {
            log.error("参加活动时，人数更新失败", e);
            return ApiResponse.fail(ErrorCode.BAD_REQUEST, "参加活动失败，请重试："+e.getMessage());
        }
        updateHeatAndStatus(activityPO);

        return ApiResponse.success(null);
    }

    // 取消参加活动
    public ApiResponse<?> cancelJoinActivity(ActivityPO activityPO, String uid) {
        if (activityPO.getActivityPersonNow() < 0) {
            log.error("活动人数小于0");
            return ApiResponse.fail(ErrorCode.BAD_REQUEST, "活动人数小于0");
        }
        try {
            activityMapper.cancelJoinActivity(activityPO.getActivityUuid());
        } catch (Exception e) {
            log.error("取消参加活动时，人数更新失败", e);
            return ApiResponse.fail(ErrorCode.BAD_REQUEST, "取消参加活动时，活动人数更新失败："+e.getMessage());
        }
        updateHeatAndStatus(activityPO);

        return ApiResponse.success(null);
    }

    public void update(ActivityPO activityPO) {
        activityMapper.update(activityPO);
    }

    // 用于主动更新活动状态和热度，有activityAttendanceService 进行状态判断并调用
    public ApiResponse<?> updateHeatAndStatus(ActivityPO activity) {
        int retryCount = 2;
        long retryInterval = 100;

        for (int i = 0; i <= retryCount; i++) {
            if (redisUtil.tryLock(
                    RedisConstant.ACTIVITY_UPDATE_STATUS_LOCK_KEY + activity.getActivityUuid(),
                    String.valueOf(Thread.currentThread().getId()),
                    RedisConstant.ACTIVITY_UPDATE_STATUS_LOCK_TTL,
                    TimeUnit.MILLISECONDS
            )) {
                try {
                    ActivityPO activityPO = activityMapper.findByUid(activity.getActivityUuid());
                    updateStatusByTime(activityPO);
                    activityPO.setActivityHeat(heatCalculate(activityPO));
                    activityMapper.update(activityPO);
                    hotActivityService.updateHotActivityByActivityId(activityPO.getActivityUuid(), activityPO.getActivityHeat());

                    return ApiResponse.success(null);
                } catch (Exception e) {
                    log.error("更新活动状态和热度失败", e);
                    // 业务逻辑执行失败，返回失败响应
                    return ApiResponse.fail(ErrorCode.INTERNAL_ERROR, "活动更新失败，内部错误");
                } finally {
                    // 释放锁
                    redisUtil.unlock(
                            RedisConstant.ACTIVITY_UPDATE_STATUS_LOCK_KEY + activity.getActivityUuid(),
                            String.valueOf(Thread.currentThread().getId())
                    );
                }
            } else {
                // 获取锁失败
                if (i < retryCount) {
                    log.warn("活动:{} 更新状态和热度失败，锁被占用，正在重试... (重试次数: {})", activity.getActivityUuid(), i + 1);
                    try {
                        Thread.sleep(retryInterval);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        log.error("重试过程中线程被中断", e);
                        return ApiResponse.fail(ErrorCode.INTERNAL_ERROR, "活动更新失败，线程被中断");
                    }
                } else {
                    log.warn("活动:{} 更新状态和热度失败，锁被占用，重试次数用尽", activity.getActivityUuid());
                    return ApiResponse.fail(ErrorCode.INTERNAL_ERROR, "活动更新失败，锁被占用，请稍后重试");
                }
            }
        }
        return ApiResponse.fail(ErrorCode.INTERNAL_ERROR, "活动更新失败，未知错误");
    }

    public void deleteById(Long id) {
        activityMapper.deleteById(id);
    }

    public List<ActivityPO> findAll() {
        return activityMapper.findAll();
    }

    public List<ActivityPO> findAllActive() {
        return activityMapper.findAllActive();
    }

    public ApiResponse<ActivityPO> findByAid(String uuid) {
        ActivityPO activityPO = activityMapper.findByUid(uuid);
        if (activityPO == null) {
            return ApiResponse.fail(ErrorCode.BAD_REQUEST, "活动不存在");
        }
        return ApiResponse.success(activityPO);
    }

    public ApiResponse<?> cancelActivity(ActivityPO activityPO, String userUuid) {
        // HACK: 后续可以添加通知给被取消活动的参与用户
        activityPO.setActivityStatus(ActivityStatusEnum.CANCELLED);
        activityMapper.update(activityPO);
        activityAttendanceService.cancelAttendance(activityPO.getActivityUuid(), ActivityAttendanceEnum.CANCEL);
        redisUtil.delete(RedisConstant.ACTIVITY_CREATE_KEY + activityPO.getActivityName());
        log.info("用户:{} 取消活动:{}", userUuid, activityPO.getActivityName());
        return ApiResponse.success(null);
    }

    // 按照热度排序查询活动
    public List<ActivityPO> getActivitiesOrderByHeat() {
        return activityMapper.findAllOrderByHeat();
    }

    // 按照收藏数排序查询活动
    public List<ActivityPO> getActivitiesOrderByCollect() {
        return activityMapper.findAllOrderByCollect();
    }

    // 按照活动开始时间排序查询活动
    public List<ActivityPO> getActivitiesOrderByBeginTime() {
        return activityMapper.findAllOrderByBeginTime();
    }

    // 按照报名截止时间排序查询活动
    public List<ActivityPO> getActivitiesOrderByRegisterEndTime() {
        return activityMapper.findAllOrderByRegisterEndTime();
    }

    public double heatCalculate(ActivityPO activityPO) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime beginTime = activityPO.getActivityBeginTime();
        LocalDateTime endTime = activityPO.getActivityEndTime();
        long totalDuration = ChronoUnit.DAYS.between(beginTime, endTime);
        long remainingDuration = ChronoUnit.DAYS.between(now, endTime);
        if (totalDuration == 0) {
            remainingDuration =ChronoUnit.HOURS.between(now, endTime);
        }
        double progress = 100 * (1 - (double) remainingDuration / totalDuration);
        if (progress < 0) {
            progress = 0;
        } else if (progress > 100) {
            progress = 100;
        }
        double timeFunc = 60 - 50 * Math.exp(-0.02 * Math.abs(progress - 60));
        double argue = activityPO.getActivityPersonNow() +
                2L *activityPO.getActivityCollectNum() +
                activityPO.getActivityVisitNum() +
                activityPO.getCommentCount();
        double userFunc = Math.log(1 + argue) / Math.log(1.2);
        // 获取当前时间的时分秒毫秒
        int hour = now.getHour();
        int minute = now.getMinute();
        int second = now.getSecond();
        int millis = now.getNano() / 1_000_000; // 纳秒转毫秒

        // 将时分秒毫秒转换为一个 9 位数（HHmmssSSS）
        double timeValue = hour * 10_000_000L + minute * 100_000L + second * 1_000L + millis;
        return timeFunc + userFunc + timeValue*0.000000001;
    }

    public void updateStatusByTime(ActivityPO activityPO){
        LocalDateTime now = LocalDateTime.now();
        boolean isInteraction = activityPO.getActivityRegisterEndTime().isAfter(activityPO.getActivityBeginTime());
        boolean isBeforeRegisterBegin = activityPO.getActivityRegisterStartTime().isAfter(now);
        boolean isBeforeActivityBegin = activityPO.getActivityBeginTime().isAfter(now);
        boolean isBeforeRegisterEnd = activityPO.getActivityRegisterEndTime().isAfter(now);
        boolean isBeforeActivityEnd = activityPO.getActivityEndTime().isAfter(now);
        boolean isEnoughRegister = activityPO.getActivityPersonNow().equals(activityPO.getActivityPersonMax());

        if (!isBeforeActivityEnd){
            activityPO.setActivityStatus(ActivityStatusEnum.ENDED);
            log.info("活动:{},于结束时间:{},活动结束",activityPO.getActivityName(),activityPO.getActivityEndTime());
            return;
        }
        // 终结状态
        if(activityPO.getActivityStatus() == ActivityStatusEnum.ENDED || activityPO.getActivityStatus() == ActivityStatusEnum.CANCELLED){
            log.warn("活动重复传入终结状态，检查代码错误");
            return;
        }
        // 判断报名截止时人数是否足够，是否需要取消
        if (!isBeforeRegisterEnd){
            if(activityPO.getActivityPersonNow() < activityPO.getActivityPersonMin()) {
                activityPO.setActivityStatus(ActivityStatusEnum.CANCELLED);
                log.info("活动:{},于截止日期:{},报名人数：{},少于最低要求人数：{},活动取消",activityPO.getActivityName(),
                        activityPO.getActivityRegisterEndTime(),activityPO.getActivityPersonNow(),activityPO.getActivityPersonMin());
                //hack： 后续可以添加通知给被取消活动的参与用户
                activityMapper.update(activityPO);
                return;
            }
        }
        // 先判断人数是否已满
        if (isEnoughRegister) {
            if(isBeforeActivityBegin){
                activityPO.setActivityStatus(ActivityStatusEnum.FULL_NOT_STARTED);
            } else {
                activityPO.setActivityStatus(ActivityStatusEnum.FULL_ONGOING);
            }
        } else {
            // 人数未满，判断时间
            if(isBeforeRegisterBegin){
                activityPO.setActivityStatus(ActivityStatusEnum.NOT_STARTED);
            }
            // 判断活动开始后是否可以报名
            if (isInteraction) {
                // 可以报名，活动开始时间早于报名截止时间
                if(isBeforeActivityBegin){
                    activityPO.setActivityStatus(ActivityStatusEnum.REGISTERING);
                } else {
                    if (isBeforeRegisterEnd) {
                        activityPO.setActivityStatus(ActivityStatusEnum.REGISTERING_ONGOING);
                    } else {
                        activityPO.setActivityStatus(ActivityStatusEnum.REGISTRATION_ENDED_ONGOING);
                    }
                }
            } else {
                // 不可报名，活动开始时间晚于报名截止时间
                if(isBeforeRegisterEnd){
                    activityPO.setActivityStatus(ActivityStatusEnum.REGISTERING);
                } else if (isBeforeActivityBegin) {
                    activityPO.setActivityStatus(ActivityStatusEnum.REGISTRATION_ENDED_NOT_STARTED);
                } else {
                    activityPO.setActivityStatus(ActivityStatusEnum.REGISTRATION_ENDED_ONGOING);
                }
            }
        }
    }

    /**
     * 事务 1：更新活动状态、访问量、热度
     * @return 更新后的活动列表
     */
    @Transactional
    public List<ActivityPO> updateActivitiesStatusAndHeat() {
        log.info("开始执行活动状态、访问量、热度更新任务, 时间: {}", LocalDateTime.now());

        // 查询所有活动
        List<ActivityPO> activityPOList = findAllActive();

        // 遍历活动列表，更新状态和热度
        for (ActivityPO activityPO : activityPOList) {
            updateStatusByTime(activityPO); // 更新状态
            int visitNum = visitLogService.countByActivityUuid(activityPO.getActivityUuid()); // 更新访问量

            // 检查缓存中的访问量是否与数据库一致
            long visitNumCached = Long.parseLong(redisUtil.get(RedisConstant.ACTIVITY_VISIT_KEY + activityPO.getActivityUuid()));
            if (visitNum > visitNumCached) {
                log.warn("活动: {} 访问量异常，数据库访问量：{}，缓存访问量：{}", activityPO.getActivityName(), visitNum, visitNumCached);
                redisUtil.set(RedisConstant.ACTIVITY_VISIT_KEY + activityPO.getActivityUuid(), String.valueOf(visitNum));
            }

            activityPO.setActivityVisitNum((long) visitNum);
            activityPO.setActivityHeat(heatCalculate(activityPO)); // 计算热度
            activityMapper.update(activityPO); // 更新数据库
        }

        log.info("活动状态、访问量、热度更新任务执行完成, 时间: {}", LocalDateTime.now());

        // 返回更新后的活动列表
        return activityPOList;
    }

    public ApiResponse<?> paramCheck(ActivityPO activityPO) {
        if (activityPO.getActivityName() == null || activityPO.getActivityName().isEmpty()) {
            return ApiResponse.fail(ErrorCode.BAD_REQUEST, "活动名称不能为空");
        }
        if (activityPO.getActivityPersonMax() == null || activityPO.getActivityPersonMax() <= 0) {
            return ApiResponse.fail(ErrorCode.BAD_REQUEST, "活动最大人数必须大于0");
        }
        if (activityPO.getActivityPersonMin() == null || activityPO.getActivityPersonMin() <= 0) {
            return ApiResponse.fail(ErrorCode.BAD_REQUEST, "活动最小人数必须大于0");
        }
        if (activityPO.getActivityPersonMax() < activityPO.getActivityPersonMin()) {
            return ApiResponse.fail(ErrorCode.BAD_REQUEST, "活动最大人数必须大于等于最小人数");
        }
        if (activityPO.getActivityBeginTime() == null) {
            return ApiResponse.fail(ErrorCode.BAD_REQUEST, "活动开始时间不能为空");
        }
        if (activityPO.getActivityEndTime() == null) {
            return ApiResponse.fail(ErrorCode.BAD_REQUEST, "活动结束时间不能为空");
        }
        if (activityPO.getActivityRegisterStartTime() == null) {
            return ApiResponse.fail(ErrorCode.BAD_REQUEST, "活动报名开始时间不能为空");
        }
        if (activityPO.getActivityRegisterEndTime() == null) {
            return ApiResponse.fail(ErrorCode.BAD_REQUEST, "活动报名结束时间不能为空");
        }
        if (activityPO.getActivityBeginTime().isAfter(activityPO.getActivityEndTime())) {
            return ApiResponse.fail(ErrorCode.BAD_REQUEST, "活动开始时间不能晚于结束时间");
        }
        if (activityPO.getActivityRegisterStartTime().isAfter(activityPO.getActivityRegisterEndTime())) {
            return ApiResponse.fail(ErrorCode.BAD_REQUEST, "活动报名开始时间不能晚于结束时间");
        }
        return ApiResponse.success(null);
    }
}