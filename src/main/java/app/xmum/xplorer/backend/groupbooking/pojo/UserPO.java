package app.xmum.xplorer.backend.groupbooking.pojo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserPO {
    private Long userId;            // 用户的自增主键
    private String userUuid;        // 用户UUID
    private String studentId;       // 学生ID
    private String userContactInfo; // 联系方式
    private String studentName;     // 学生名称
    private String userAvatarUrl;   // 学生头像URL
    private LocalDateTime createdAt; // 创建时间
    private LocalDateTime updatedAt; // 修改时间
}