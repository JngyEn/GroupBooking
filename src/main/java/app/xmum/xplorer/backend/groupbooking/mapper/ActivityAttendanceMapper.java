package app.xmum.xplorer.backend.groupbooking.mapper;

import app.xmum.xplorer.backend.groupbooking.enums.ActivityAttendanceEnum;
import app.xmum.xplorer.backend.groupbooking.pojo.po.ActivityAttendancePO;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface ActivityAttendanceMapper {

    @Insert("INSERT INTO fact_activity_attendance (attendance_uuid, activity_uuid, user_uuid, attendance_status, created_at, updated_at) " +
            "VALUES (#{attendanceUuid}, #{activityUuid}, #{userUuid}, #{attendanceStatus,typeHandler=app.xmum.xplorer.backend.groupbooking.handler.ActivityAttendanceEnumTypeHandler}, #{createdAt}, #{updatedAt})")
    void insert(ActivityAttendancePO attendance);

    @Delete(("UPDATE fact_activity_attendance SET attendance_status = #{attendanceStatus,typeHandler=app.xmum.xplorer.backend.groupbooking.handler.ActivityAttendanceEnumTypeHandler}, updated_at = #{updatedAt} WHERE attendance_uuid = #{attendanceUuid}"))
    void deleteByUuid(String attendanceUuid, ActivityAttendanceEnum attendanceStatus);

    @Update("UPDATE fact_activity_attendance SET attendance_status = #{attendanceStatus,typeHandler=app.xmum.xplorer.backend.groupbooking.handler.ActivityAttendanceEnumTypeHandler}, updated_at = #{updatedAt} WHERE attendance_uuid = #{attendanceUuid}")
    void update(ActivityAttendancePO attendance);

    @Select("SELECT * FROM fact_activity_attendance WHERE attendance_uuid = #{attendanceUuid}")
    ActivityAttendancePO findByUuid(String attendanceUuid);

    @Select("SELECT * FROM fact_activity_attendance WHERE user_uuid = #{userUuid}")
    List<ActivityAttendancePO> findByUserUuid(String userUuid);

    @Select("SELECT * FROM fact_activity_attendance WHERE user_uuid = #{userUuid} AND activity_uuid = #{activityUuid}")
    ActivityAttendancePO findByUserAndActivityUuid(String userUuid, String activityUuid);

    @Select("SELECT * FROM fact_activity_attendance WHERE user_uuid = #{userUuid} AND created_at BETWEEN #{startTime} AND #{endTime}")
    List<ActivityAttendancePO> findByUserUuidAndTimeRange(String userUuid, LocalDateTime startTime, LocalDateTime endTime);
}