package app.xmum.xplorer.backend.groupbooking.pojo;
import app.xmum.xplorer.backend.groupbooking.enums.ActivityStatusEnum;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ActivityPO {

    private Long activityId; // 自增主键
    private String activityUuid; // 活动唯一标识 UUID
    private String activityName; // 活动名字
    private String activityDescText; // 活动内容描述
    private String activityLocation; // 活动地点
    private LocalDateTime activityBeginTime; // 活动开始时间
    private LocalDateTime activityEndTime; // 活动结束时间
    private LocalDateTime activityRegisterStartTime; // 报名开始时间
    private LocalDateTime activityRegisterEndTime; // 报名结束时间
    private Integer activityPersonMin; // 活动最少参加人数
    private Integer activityPersonMax; // 活动最多参加人数
    private ActivityStatusEnum activityStatus; // 活动状态的枚举
    private Integer activityPersonNow; // 活动当前报名人数
    private Long activityVisitNum; // 活动访问次数
    private Integer activityCollectNum; // 活动收藏人数
    private Integer activityHeat; // 活动热度
    private String categoryUuid; // 活动分类 UUID
    private String userUuid; // 活动发起人 UUID
    private LocalDateTime createdAt; // 创建时间
    private LocalDateTime upLocalDateTimedAt; // 更新时间
}