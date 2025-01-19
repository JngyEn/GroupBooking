package app.xmum.xplorer.backend.groupbooking.pojo;

import lombok.Data;

import java.time.LocalDateTime;
@Data
public class ContactPO {
    private Long contactId;        // 联系方式的唯一标识
    private String contactUuid;    // 联系方式UUID
    private String activityUuid;   // 活动UUID，关联活动表，-1 则为用户联系方式
    private String userUuid;       // 用户UUID，关联用户表，-1 则为活动联系方式
    private String contactType;    // 联系方式类型（如邮箱、电话等）
    private String contactValue;   // 联系方式的值（如邮箱地址、电话号码等）
    private Integer targetType;    // 对象的类型：1-活动，2-用户
    private LocalDateTime createdAt; // 创建时间
    private LocalDateTime updatedAt; // 修改时间
}
