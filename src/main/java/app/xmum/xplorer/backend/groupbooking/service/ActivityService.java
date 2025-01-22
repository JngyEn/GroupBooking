package app.xmum.xplorer.backend.groupbooking.service;

import app.xmum.xplorer.backend.groupbooking.config.RedisConstant;
import app.xmum.xplorer.backend.groupbooking.enums.ActivityStatusEnum;
import app.xmum.xplorer.backend.groupbooking.mapper.ActivityMapper;
import app.xmum.xplorer.backend.groupbooking.pojo.ActivityPO;
import app.xmum.xplorer.backend.groupbooking.response.ApiResponse;
import app.xmum.xplorer.backend.groupbooking.enums.ErrorCode;
import app.xmum.xplorer.backend.groupbooking.utils.RedisUtil;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class ActivityService {

    @Autowired
    private ActivityMapper activityMapper;
    @Autowired
    private RedisUtil redisUtil;

    public ActivityPO findByAId(String Aid) {
        return activityMapper.findByUid(Aid);
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

    public void update(ActivityPO activityPO) {
        activityMapper.update(activityPO);
    }

    public void deleteById(Long id) {
        activityMapper.deleteById(id);
    }

    public List<ActivityPO> findAll() {
        return activityMapper.findAll();
    }

    public ApiResponse<ActivityPO> findByAid(String uuid) {
        ActivityPO activityPO = activityMapper.findByUid(uuid);
        if (activityPO == null) {
            return ApiResponse.fail(ErrorCode.BAD_REQUEST, "活动不存在");
        }
        return ApiResponse.success(activityPO);
    }

    public ApiResponse<?> cancelActivity(ActivityPO activityPO, String userUuid) {
        activityPO.setActivityStatus(ActivityStatusEnum.CANCELLED);
        activityMapper.update(activityPO);
        redisUtil.delete(RedisConstant.ACTIVITY_CREATE_KEY + activityPO.getActivityName());
        return ApiResponse.success(null);
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
                2*activityPO.getActivityCollectNum() +
                activityPO.getActivityVisitNum() +
                activityPO.getCommentCount();
        double userFunc = Math.log(1 + argue) / Math.log(1.2);
        return timeFunc + userFunc;
    }

    public void updateStatusByTime(ActivityPO activityPO){
        LocalDateTime now = LocalDateTime.now();
        Boolean isInteraction = activityPO.getActivityRegisterEndTime().isAfter(activityPO.getActivityBeginTime());
        Boolean isBeforeRegisterBegin = activityPO.getActivityRegisterStartTime().isAfter(now);
        Boolean isBeforeActivityBegin = activityPO.getActivityBeginTime().isAfter(now);
        Boolean isBeforeRegisterEnd = activityPO.getActivityRegisterEndTime().isAfter(now);
        Boolean isBeforeActivityEnd = activityPO.getActivityEndTime().isAfter(now);
        Boolean isEnoughRegister = activityPO.getActivityPersonNow().equals(activityPO.getActivityPersonMax());

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

    @XxlJob("activityDailyUpdate")
    public void dailyUpdatesHandler() throws Exception {
        log.info("开始执行活动状态更新任务,时间:{}", LocalDateTime.now());

        try {
            // 查询所有活动
            List<ActivityPO> activityPOList = activityMapper.findAll();

            // 遍历活动列表，更新状态和热度
            for (ActivityPO activityPO : activityPOList) {
                updateStatusByTime(activityPO); // 更新状态
                activityPO.setActivityHeat(heatCalculate(activityPO)); // 计算热度
                activityMapper.update(activityPO); // 更新数据库
            }

            log.info("活动状态更新任务执行完成,时间:{}", LocalDateTime.now());
        } catch (Exception e) {
            log.error("活动状态更新任务执行失败", e);
            throw e; // 抛出异常，让 XXL-JOB 捕获并记录失败
        }
    }
}