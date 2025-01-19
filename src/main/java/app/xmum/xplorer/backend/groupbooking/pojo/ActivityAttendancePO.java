package app.xmum.xplorer.backend.groupbooking.pojo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ActivityAttendancePO {
    private Long attendanceId;        // 活动报名记录的自增主键
    private String attendanceUuid;    // 报名记录的唯一标识
    private String activityUuid;      // 活动的UUID，外键关联活动表
    private String userUuid;          // 报名活动的用户UUID
    private Integer attendanceStatus; // 报名状态的枚举
    private LocalDateTime createdAt;  // 创建时间
    private LocalDateTime updatedAt;  // 更新时间
}