package app.xmum.xplorer.backend.groupbooking.controller;

import app.xmum.xplorer.backend.groupbooking.enums.ActivityAttendanceEnum;
import app.xmum.xplorer.backend.groupbooking.enums.ActivityStatusEnum;
import app.xmum.xplorer.backend.groupbooking.pojo.ActivityAttendancePO;
import app.xmum.xplorer.backend.groupbooking.pojo.ActivityPO;
import app.xmum.xplorer.backend.groupbooking.response.ApiResponse;
import app.xmum.xplorer.backend.groupbooking.service.ActivityAttendanceService;
import app.xmum.xplorer.backend.groupbooking.service.ActivityService;
import app.xmum.xplorer.backend.groupbooking.enums.ErrorCode;
import app.xmum.xplorer.backend.groupbooking.service.UserService;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/activities")
public class ActivityController {

    @Autowired
    private ActivityService activityService;
    @Autowired
    private UserService userService;
    @Autowired
    private ActivityAttendanceService activityAttendanceService;
    @PostMapping("/{uid}")
    public ApiResponse<?> findByAId(@RequestBody ActivityPO activityPO,@PathVariable String uid) {
        if(userService.getUserByUuid(uid).getCode() != 200) {
            return ApiResponse.fail(ErrorCode.BAD_REQUEST,"用户不存在");
        }
        if(activityService.findByAid(activityPO.getActivityUuid()).getCode() != 200) {
            return ApiResponse.fail(ErrorCode.BAD_REQUEST,"活动不存在");
        }
        return activityService.findByAId(activityPO, uid);
    }

    @PostMapping
    public ApiResponse<?> insert(@RequestBody ActivityPO activityPO) {
        // 活动开始后人数未满可以继续报名
        ApiResponse<?> result = activityService.paramCheck(activityPO);
        if (result.getCode() != 200) {
            return result;
        }
        //hack：校验用户和类型的UUID

        return activityService.insert(activityPO);
    }

    // 参加活动
    @PostMapping("/{uid}/join")
    public ApiResponse<?> joinActivity(@RequestBody ActivityPO activityPO,@PathVariable String uid) {
        ActivityPO activity = activityService.findByAid(activityPO.getActivityUuid()).getData();
        if (activity == null) {
            return ApiResponse.fail(ErrorCode.BAD_REQUEST, "活动不存在");
        }
        if (userService.getUserByUuid(uid).getCode() != 200) {
            return ApiResponse.fail(ErrorCode.BAD_REQUEST, "用户不存在");
        }
        ActivityAttendancePO attendance = activityAttendanceService.getAttendanceByUserAndActivityUuid(uid, activityPO.getActivityUuid());
        if (attendance != null) {
            {
                if (attendance.getAttendanceStatus() == ActivityAttendanceEnum.ATTEND) {
                    return ApiResponse.fail(ErrorCode.BAD_REQUEST, "已经参与过该活动");
                }
            }
        }
        return activityService.joinActivity(activity, uid);
    }

    @PostMapping("/{uid}/cancelJoin")
    public ApiResponse<?> cancelJoinActivity(@RequestBody ActivityPO activityPO,@PathVariable String uid) {
        ActivityPO activity = activityService.findByAid(activityPO.getActivityUuid()).getData();
        if(activity == null) {
            return ApiResponse.fail(ErrorCode.BAD_REQUEST,"活动不存在");
        }
        if(userService.getUserByUuid(uid).getCode() != 200) {
            return ApiResponse.fail(ErrorCode.BAD_REQUEST, "用户不存在");
        }
        return activityService.cancelJoinActivity(activity, uid);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        activityService.deleteById(id);
    }

    @GetMapping
    public List<ActivityPO> findAll() {
        return activityService.findAll();
    }

    @PostMapping("/{uid}/cancelActivity")
    public ApiResponse<?> cancelActivity(@RequestBody ActivityPO activityPO,@PathVariable String uid) {
        ActivityPO activity = activityService.findByAid(activityPO.getActivityUuid()).getData();
        if(activity == null) {
            return ApiResponse.fail(ErrorCode.BAD_REQUEST,"活动不存在");
        }
        if (!Objects.equals(activityPO.getUserUuid(), uid)) {
            return ApiResponse.fail(ErrorCode.BAD_REQUEST,"只能取消自己的活动");
        }
        if(activityPO.getActivityStatus() == ActivityStatusEnum.CANCELLED || activityPO.getActivityStatus() == ActivityStatusEnum.ENDED) {
            return ApiResponse.fail(ErrorCode.BAD_REQUEST, "活动已取消或已结束");
        }
        return activityService.cancelActivity(activityPO, uid);
    }


    @GetMapping("/order-by-heat")
    public ApiResponse<List<ActivityPO>> getActivitiesOrderByHeat() {
        List<ActivityPO> activities = activityService.getActivitiesOrderByHeat();
        if (activities.isEmpty()) {
            return  ApiResponse.fail(ErrorCode.BAD_REQUEST,"没有符合条件的活动");
        }
        return ApiResponse.success(activities);
    }

    // 按照收藏数排序查询活动，排除已结束和已取消的活动
    @GetMapping("/order-by-collect")
    public ApiResponse<List<ActivityPO>> getActivitiesOrderByCollect() {
        List<ActivityPO> activities = activityService.getActivitiesOrderByCollect();
        if (activities.isEmpty()) {
            return ApiResponse.fail(ErrorCode.BAD_REQUEST,"没有符合条件的活动");
        }
        return ApiResponse.success(activities);
    }

    // 按照活动开始时间排序查询活动，排除已结束和已取消的活动
    @GetMapping("/order-by-begin-time")
    public ApiResponse<List<ActivityPO>> getActivitiesOrderByBeginTime() {
        List<ActivityPO> activities = activityService.getActivitiesOrderByBeginTime();
        if (activities.isEmpty()) {
            return ApiResponse.fail(ErrorCode.BAD_REQUEST,"没有符合条件的活动");
        }
        return ApiResponse.success(activities);
    }

    // 按照报名截止时间排序查询活动，排除已结束和已取消的活动
    @GetMapping("/order-by-register-end-time")
    public ApiResponse<List<ActivityPO>> getActivitiesOrderByRegisterEndTime() {
        List<ActivityPO> activities = activityService.getActivitiesOrderByRegisterEndTime();
        if (activities.isEmpty()) {
            return ApiResponse.fail(ErrorCode.BAD_REQUEST,"没有符合条件的活动");
        }
        return ApiResponse.success(activities);
    }
    // TODO: 参与活动
    // TODO：更新热度、限制人数
    //Todo：收藏与取消收藏
    //TODO：浏览量
}