package app.xmum.xplorer.backend.groupbooking.config;

public class RedisConstant {
    // 创建活动
    public static final String ACTIVITY_CREATE_KEY = "Activity:Creat:";
    public static final long ACTIVITY_CREATED_TTL = 10*60*1000;
    public static final String ACTIVITY_STATUS_ACTIVATE = "activate";

    // 浏览量
    public static final String ACTIVITY_VISIT_KEY = "Activity:Visit:";

    // 热度表
    public static final String HOT_ACTIVITY_LIST_KEY = "HotActivityList:";//+versionId
    public static final String HOT_ACTIVITY_LIST_CURRENT_VERSION_KEY = "HotActivityList:CurrentVersion";
    public static final String HOT_ACTIVITY_LIST_VALUE = "HotActivityList:ActivityUuid:";

    // 活动更新分布式锁
    public static final String ACTIVITY_UPDATE_STATUS_LOCK_KEY = "Activity:UpdateStatus:Lock:";
    public static final long ACTIVITY_UPDATE_STATUS_LOCK_TTL = 2*60*1000;
}
