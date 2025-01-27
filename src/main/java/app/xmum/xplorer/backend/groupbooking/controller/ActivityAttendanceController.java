package app.xmum.xplorer.backend.groupbooking.controller;

import app.xmum.xplorer.backend.groupbooking.pojo.po.ActivityAttendancePO;
import app.xmum.xplorer.backend.groupbooking.service.ActivityAttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/attendance")
public class ActivityAttendanceController {

    @Autowired
    private ActivityAttendanceService attendanceService;
    @GetMapping("/{attendanceUuid}")
    public ActivityAttendancePO getAttendanceByUuid(@PathVariable String attendanceUuid) {
        return attendanceService.getAttendanceByUuid(attendanceUuid);
    }

    @GetMapping("/user/{userUuid}")
    public List<ActivityAttendancePO> getAttendancesByUserUuid(@PathVariable String userUuid) {
        return attendanceService.getAttendancesByUserUuid(userUuid);
    }

    @GetMapping("/user/{userUuid}/activity/{activityUuid}")
    public ActivityAttendancePO getAttendanceByUserAndActivityUuid(@PathVariable String userUuid, @PathVariable String activityUuid) {
        return attendanceService.getAttendanceByUserAndActivityUuid(userUuid, activityUuid);
    }

    @GetMapping("/user/{userUuid}/time-range")
    public List<ActivityAttendancePO> getAttendancesByUserUuidAndTimeRange(@PathVariable String userUuid, @RequestParam LocalDateTime startTime, @RequestParam LocalDateTime endTime) {
        return attendanceService.getAttendancesByUserUuidAndTimeRange(userUuid, startTime, endTime);
    }
}