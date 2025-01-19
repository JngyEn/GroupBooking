package app.xmum.xplorer.backend.groupbooking.mapper;

import app.xmum.xplorer.backend.groupbooking.pojo.ActivityAttendancePO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ActivityAttendanceMapper {

    @Insert("INSERT INTO fact_activity_attendance (attendance_uuid, activity_uuid, user_uuid, attendance_status, created_at, updated_at) " +
            "VALUES (#{attendanceUuid}, #{activityUuid}, #{userUuid}, #{attendanceStatus}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "attendanceId")
    void insert(ActivityAttendancePO attendance);

    @Select("SELECT * FROM fact_activity_attendance WHERE attendance_id = #{attendanceId}")
    ActivityAttendancePO findById(Long attendanceId);

    @Select("SELECT * FROM fact_activity_attendance")
    List<ActivityAttendancePO> findAll();

    @Update("UPDATE fact_activity_attendance SET attendance_status = #{attendanceStatus}, updated_at = #{updatedAt} WHERE attendance_id = #{attendanceId}")
    void update(ActivityAttendancePO attendance);

    @Delete("DELETE FROM fact_activity_attendance WHERE attendance_id = #{attendanceId}")
    void delete(Long attendanceId);
}