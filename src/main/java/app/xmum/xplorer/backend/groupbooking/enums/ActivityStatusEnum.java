package app.xmum.xplorer.backend.groupbooking.enums;

public enum ActivityStatusEnum {
    NOT_STARTED(0, "未开始报名"),
    REGISTERING(1, "报名中"),
    REGISTRATION_ENDED_NOT_STARTED(2, "报名结束，活动未开始"),
    REGISTRATION_ENDED_ONGOING(3, "报名结束，活动进行中"),
    ENDED(4, "活动已结束"),
    CANCELLED(5, "活动已取消");

    private final int value; // 数据库存储的值
    private final String description; // 状态描述

    // 构造函数
    ActivityStatusEnum(int value, String description) {
        this.value = value;
        this.description = description;
    }

    // 获取枚举值对应的整数值
    public int getValue() {
        return value;
    }

    // 获取枚举值的描述
    public String getDescription() {
        return description;
    }

    // 根据整数值获取对应的枚举常量
    public static ActivityStatusEnum fromValue(int value) {
        for (ActivityStatusEnum status : ActivityStatusEnum.values()) {
            if (status.getValue() == value) {
                return status;
            }
        }
        throw new IllegalArgumentException("无效的活动状态值: " + value);
    }
}