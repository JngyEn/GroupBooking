package app.xmum.xplorer.backend.groupbooking.pojo;

import app.xmum.xplorer.backend.groupbooking.enums.ActivityAttendanceEnum;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ActivityAttendancePO {
    private Long attendanceId;        // 活动报名记录的自增主键
    private String attendanceUuid;    // 报名记录的唯一标识,查询具体报名记录时使用
    private String activityUuid;      // 活动的UUID，外键关联活动表
    private String userUuid;          // 报名活动的用户UUID
    private ActivityAttendanceEnum attendanceStatus; // 报名状态的枚举，0表示参与，1表示用户取消报名，2表示活动取消
    private LocalDateTime createdAt;  // 创建时间
    private LocalDateTime updatedAt;  // 更新时间
}