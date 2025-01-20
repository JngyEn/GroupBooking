package app.xmum.xplorer.backend.groupbooking.service;

import app.xmum.xplorer.backend.groupbooking.config.RedisConstant;
import app.xmum.xplorer.backend.groupbooking.enums.ActivityStatusEnum;
import app.xmum.xplorer.backend.groupbooking.mapper.ActivityMapper;
import app.xmum.xplorer.backend.groupbooking.pojo.ActivityPO;
import app.xmum.xplorer.backend.groupbooking.response.ApiResponse;
import app.xmum.xplorer.backend.groupbooking.utils.ErrorCode;
import app.xmum.xplorer.backend.groupbooking.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class ActivityService {

    @Autowired
    private ActivityMapper activityMapper;
    @Autowired
    private RedisUtil redisUtil;

    public ActivityPO findById(Long id) {
        return activityMapper.findById(id);
    }

    public ApiResponse<?> insert(ActivityPO activityPO) {
        if (redisUtil.hasKey(RedisConstant.ACTIVITY_CREATE_KEY + activityPO.getActivityName())) {
            if (redisUtil.get(RedisConstant.ACTIVITY_CREATE_KEY + activityPO.getActivityName()).equals(RedisConstant.ACTIVITY_STATUS_ACTIVATE)) {
                return ApiResponse.fail(ErrorCode.BAD_REQUEST, "活动已创建");
            } else {
                //HACK: 后续插入检查，以及其他数据拼接
                activityPO.setActivityUuid(UUID.randomUUID().toString());
                if(activityPO.getActivityRegisterStartTime().isBefore(LocalDateTime.now())){
                    activityPO.setActivityStatus(ActivityStatusEnum.NOT_STARTED);
                } else if (activityPO.getActivityRegisterEndTime().isAfter(LocalDateTime.now())) {
                    activityPO.setActivityStatus(ActivityStatusEnum.REGISTERING);
                } else {
                    return ApiResponse.fail(ErrorCode.BAD_REQUEST,"报名结束时间早于创建活动时间");
                }
                //hack: 插入热度初始值
                //hack：校验用户和类型的UUID
                activityMapper.insert(activityPO);
                redisUtil.set(RedisConstant.ACTIVITY_CREATE_KEY + activityPO.getActivityName(), RedisConstant.ACTIVITY_STATUS_ACTIVATE);
                return ApiResponse.success();
            }
        }
        redisUtil.set(RedisConstant.ACTIVITY_CREATE_KEY + activityPO.getActivityName(), RedisConstant.ACTIVITY_STATUS_UNACTIVATED, RedisConstant.ACTIVITY_CREATED_TTL, TimeUnit.MILLISECONDS);
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
}