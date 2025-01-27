package app.xmum.xplorer.backend.groupbooking.enums;

public enum ActivityAttendanceEnum {
    ATTEND(0, "正常参与"),
    QUIT(2, "用户退出活动"),
    CANCEL(3, "活动被取消");
    private final int code;
    private final String description;

    ActivityAttendanceEnum(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static ActivityAttendanceEnum getByCode(int code) {
        for (ActivityAttendanceEnum status : ActivityAttendanceEnum.values()) {
            if (status.getCode() == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid code: " + code);
    }
}
