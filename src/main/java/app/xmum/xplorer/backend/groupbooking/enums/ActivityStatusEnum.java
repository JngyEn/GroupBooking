package app.xmum.xplorer.backend.groupbooking.enums;

public enum ActivityStatusEnum {
    NOT_STARTED(0, "未开始报名"),
    REGISTERING(1, "报名中，活动未开始"),
    REGISTERING_ONGOING(2, "报名中，活动进行中"),
    FULL_NOT_STARTED(3, "人数已满，活动未开始"),
    FULL_ONGOING(4, "人数已满，活动进行中"),
    REGISTRATION_ENDED_NOT_STARTED(5, "报名结束，活动未开始"),
    REGISTRATION_ENDED_ONGOING(6, "报名结束，活动进行中"),
    ENDED(7, "活动已结束"),
    CANCELLED(8, "活动已取消");

    private final int code;
    private final String description;

    ActivityStatusEnum(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
    // 根据 code 获取枚举实例
    public static ActivityStatusEnum getByCode(int code) {
        for (ActivityStatusEnum status : ActivityStatusEnum.values()) {
            if (status.getCode() == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid code: " + code);
    }
}