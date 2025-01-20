package app.xmum.xplorer.backend.groupbooking;
import app.xmum.xplorer.backend.groupbooking.config.OSSConfig;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.ObjectListing;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class OssTest {

    @Autowired
    private OSSConfig ossConfig; // 注入配置类

    @Test
    public void testOSSConnection() {
        // 初始化 OSS 客户端
        OSS ossClient = new OSSClientBuilder()
                .build(ossConfig.getEndpoint(), ossConfig.getAccessKeyId(), ossConfig.getAccessKeySecret());

        try {
            // 执行一个简单的操作：列出 Bucket 中的文件（最多 10 个）
            ObjectListing objectListing = ossClient.listObjects(ossConfig.getBucketName());

            // 检查返回结果是否为空
            assertNotNull(objectListing, "OSS 连接失败，返回结果为空");

            // 打印 Bucket 中的文件列表
            System.out.println("OSS 连接成功！Bucket 中的文件列表：");
            objectListing.getObjectSummaries().forEach(object -> System.out.println(object.getKey()));

        } catch (Exception e) {
            // 捕获异常并打印错误信息
            System.err.println("OSS 连接失败：" + e.getMessage());
            throw e; // 抛出异常以使测试失败
        } finally {
            // 关闭 OSS 客户端
            ossClient.shutdown();
        }
    }
}