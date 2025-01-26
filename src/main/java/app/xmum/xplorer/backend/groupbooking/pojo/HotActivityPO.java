package app.xmum.xplorer.backend.groupbooking.pojo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class HotActivityPO {
    private Long hotActivityId;       // 自增主键
    private String activityUuid;      // 活动UUID
    private Double activityHeat;     // 活动热度
    private Boolean isActive;         // 活动状态，false为不活跃，true为活跃
    private String versionId;         // 活动版本号
    private Boolean isCurrent;        // 活动状态，false为往期版本，true为当前版本
    private LocalDateTime createdAt;           // 创建时间
    private LocalDateTime updatedAt;           // 修改时间
}
