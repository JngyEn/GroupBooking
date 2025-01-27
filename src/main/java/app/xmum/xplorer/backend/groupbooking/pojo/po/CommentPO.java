package app.xmum.xplorer.backend.groupbooking.pojo.po;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CommentPO {
    private Long commentId;         // 评论的自增主键
    private String commentUuid;     // 评论的UUID
    private Integer commentLevel;   // 评论层级：1-评论，2-回复
    private String activityUuid;    // 活动UUID，回复则此项为-1
    private String authorUuid;      // 作者UUID，关联用户表
    private String replyUuid;       // 回复对象UUID，评论则此项为-1
    private String commentContent;  // 评论内容
    private LocalDateTime createdAt; // 创建时间
    private LocalDateTime updatedAt; // 修改时间
}