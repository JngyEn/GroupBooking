package app.xmum.xplorer.backend.groupbooking.pojo.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ActivityQueryDTO {
    private LocalDateTime registerStartTime; // 报名开始时间（查找之后的）
    private LocalDateTime registerEndTime;   // 报名结束时间（查找之前的）
    private LocalDateTime activityBeginTime; // 活动开始时间（查找之后的）
    private String categoryUuid;             // 活动种类 UUID
    private String location;                 // 活动地点
    private Integer vacantSpots;             // 空余人数
}
