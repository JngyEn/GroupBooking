package app.xmum.xplorer.backend.groupbooking.pojo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ImagePO {
    private Long imageId;        // 图片的自增主键
    private String imageUuid;    // 图片UUID
    private String activityUuid; // 活动UUID，关联活动表，-1 则为用户头像图片
    private String userUuid;     // 用户UUID，关联用户表，-1 则为活动图片
    private String imageUrl;     // 图片的URL
    private Integer imageWidth;  // 图片的宽度
    private Integer imageHeight; // 图片的高度
    private LocalDateTime createdAt; // 创建时间
    private LocalDateTime updatedAt; // 更新时间
}