package app.xmum.xplorer.backend.groupbooking.pojo.po;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserVisitLogPO {
    private Long visitLogId;      // 访问记录的自增主键
    private String visitUuid;     // 访问记录的UUID
    private String categoryUuid;  // 被访问的活动种类UUID
    private String activityUuid;  // 被访问的活动UUID
    private String userUuid;      // 用户的UUID
    private LocalDateTime createdAt; // 访问时间
    private LocalDateTime updatedAt; // 更新时间
}