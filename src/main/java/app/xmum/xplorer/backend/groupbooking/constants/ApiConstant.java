package app.xmum.xplorer.backend.groupbooking.constants;

public class ApiConstant {

    // 定义静态变量
    public static final String API_KEY;

    // 使用静态代码块初始化变量
    static {
        // 从系统环境变量中读取 API_KEY
        API_KEY = System.getenv("API_KEY");

        // 检查是否成功获取，如果未设置环境变量，可以抛出异常或设置默认值
        if (API_KEY == null || API_KEY.isEmpty()) {
            throw new IllegalStateException("API_KEY environment variable is not set!");
        }
    }
}