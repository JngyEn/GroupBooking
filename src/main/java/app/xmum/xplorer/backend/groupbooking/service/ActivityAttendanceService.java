package app.xmum.xplorer.backend.groupbooking.service;

import app.xmum.xplorer.backend.groupbooking.enums.ActivityAttendanceEnum;
import app.xmum.xplorer.backend.groupbooking.mapper.ActivityAttendanceMapper;
import app.xmum.xplorer.backend.groupbooking.pojo.po.ActivityAttendancePO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ActivityAttendanceService {

    @Autowired
    private ActivityAttendanceMapper attendanceMapper;

    public void addAttendance(String activityUuid, String userUuid) {
        ActivityAttendancePO attendance = new ActivityAttendancePO();
        attendance.setAttendanceUuid(UUID.randomUUID().toString().replace("-", ""));
        attendance.setActivityUuid(activityUuid);
        attendance.setUserUuid(userUuid);
        attendance.setAttendanceStatus(ActivityAttendanceEnum.ATTEND);
        attendance.setCreatedAt(LocalDateTime.now());
        attendance.setUpdatedAt(LocalDateTime.now());
        attendanceMapper.insert(attendance);
    }

    public void cancelAttendance(String attendanceUuid, ActivityAttendanceEnum status) {
        attendanceMapper.deleteByUuid(attendanceUuid, status);
    }

    public void updateAttendance(ActivityAttendancePO attendance) {
        attendanceMapper.update(attendance);
    }

    public ActivityAttendancePO getAttendanceByUuid(String attendanceUuid) {
        return attendanceMapper.findByUuid(attendanceUuid);
    }

    public List<ActivityAttendancePO> getAttendancesByUserUuid(String userUuid) {
        return attendanceMapper.findByUserUuid(userUuid);
    }

    public ActivityAttendancePO getAttendanceByUserAndActivityUuid(String userUuid, String activityUuid) {
        return attendanceMapper.findByUserAndActivityUuid(userUuid, activityUuid);
    }

    public List<ActivityAttendancePO> getAttendancesByUserUuidAndTimeRange(String userUuid, LocalDateTime startTime, LocalDateTime endTime) {
        return attendanceMapper.findByUserUuidAndTimeRange(userUuid, startTime, endTime);
    }
}