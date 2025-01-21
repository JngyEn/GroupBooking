package app.xmum.xplorer.backend.groupbooking.controller;

import app.xmum.xplorer.backend.groupbooking.enums.ActivityStatusEnum;
import app.xmum.xplorer.backend.groupbooking.pojo.ActivityPO;
import app.xmum.xplorer.backend.groupbooking.response.ApiResponse;
import app.xmum.xplorer.backend.groupbooking.service.ActivityService;
import app.xmum.xplorer.backend.groupbooking.enums.ErrorCode;
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

    @GetMapping("/{Aid}")
    public ActivityPO findByAId(@PathVariable String Aid) {
        //hack: 判断时间先后顺序
        return activityService.findByAId(Aid);
    }

    @PostMapping
    public ApiResponse<?> insert(@RequestBody ActivityPO activityPO) {
        // 活动开始后人数未满可以继续报名
        if(activityPO.getActivityBeginTime().isBefore(LocalDateTime.now()) ||
            activityPO.getActivityRegisterEndTime().isBefore(LocalDateTime.now()) ||
            activityPO.getActivityBeginTime().isAfter(activityPO.getActivityEndTime()) ||
            activityPO.getActivityRegisterStartTime().isAfter(activityPO.getActivityRegisterEndTime()) ||
            activityPO.getActivityRegisterEndTime().isAfter(activityPO.getActivityEndTime())) {
            return ApiResponse.fail(ErrorCode.BAD_REQUEST,"报名或活动时间设置错误");
        }
        //hack：校验用户和类型的UUID

        return activityService.insert(activityPO);
    }

    @PutMapping
    public void update(@RequestBody ActivityPO activityPO) {
        activityService.update(activityPO);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        activityService.deleteById(id);
    }

    @GetMapping
    public List<ActivityPO> findAll() {
        return activityService.findAll();
    }

    @PostMapping("/{uid}/cancel")
    public ApiResponse<?> cancelActivity(@RequestBody ActivityPO activityPO,@PathVariable String uid) {
        if(activityService.findByAid(activityPO.getActivityUuid()).getCode() == 400) {
            return ApiResponse.fail(ErrorCode.BAD_REQUEST,"活动不存在");
        }
        if (!Objects.equals(activityPO.getUserUuid(), uid)) {
            return ApiResponse.fail(ErrorCode.BAD_REQUEST,"只能取消自己的活动");
        }
        if(activityPO.getActivityStatus() == ActivityStatusEnum.CANCELLED){
            return ApiResponse.fail(ErrorCode.BAD_REQUEST, "活动已取消");
        }
        return activityService.cancelActivity(activityPO, uid);
    }

    // TODO: 参与活动
    // TODO：更新热度、限制人数

    //Todo：收藏与取消收藏
    //TODO：浏览量
}