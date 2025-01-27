package app.xmum.xplorer.backend.groupbooking.pojo.po;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ActivityFavoritePO {
    private Long favoriteId;        // 收藏记录的自增主键
    private String favoriteUuid;    // 收藏记录的UUID
    private String activityUuid;    // 活动UUID，关联活动表
    private String userUuid;        // 用户UUID，关联用户表
    private Integer favoriteStatus; // 收藏状态：0-已收藏，1-取消收藏
    private LocalDateTime createdAt; // 创建时间
    private LocalDateTime updatedAt; // 修改时间
}