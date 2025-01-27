package app.xmum.xplorer.backend.groupbooking.service;

import app.xmum.xplorer.backend.groupbooking.enums.ErrorCode;
import app.xmum.xplorer.backend.groupbooking.pojo.po.ActivityPO;
import app.xmum.xplorer.backend.groupbooking.response.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ValidationService {
    @Autowired
    private ActivityService activityService;
    @Autowired
    private UserService userService;

    public ApiResponse<?> validationCheck( String  activityUuid,  String uid) {
        if (userService.getUserByUuid(uid).getCode() != 200) {
            return ApiResponse.fail(ErrorCode.BAD_REQUEST, "用户不存在");
        }
        if (activityService.findByAid(activityUuid).getCode() != 200) {
            return ApiResponse.fail(ErrorCode.BAD_REQUEST, "活动不存在");
        }
        return ApiResponse.success();
    }
    public ApiResponse<?> validationCheckUser(String uid){
        if (userService.getUserByUuid(uid).getCode() != 200) {
            return ApiResponse.fail(ErrorCode.BAD_REQUEST, "用户不存在");
        }
        return ApiResponse.success();
    }
    public ApiResponse<?> validationCheckActivity(String activityUuid){
        if (activityService.findByAid(activityUuid).getCode() != 200) {
            return ApiResponse.fail(ErrorCode.BAD_REQUEST, "活动不存在");
        }
        return ApiResponse.success();
    }
    public ApiResponse<?> activityParamCheck(ActivityPO activityPO) {
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
