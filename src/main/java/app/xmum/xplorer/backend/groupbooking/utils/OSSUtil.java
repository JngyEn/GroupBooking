package app.xmum.xplorer.backend.groupbooking.utils;

import app.xmum.xplorer.backend.groupbooking.config.OSSConfig;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class OSSUtil {

    private final OSSConfig ossConfig;
    private static final Logger logger = LoggerFactory.getLogger(OSSUtil.class);

    @Autowired
    public OSSUtil(OSSConfig ossConfig) {
        this.ossConfig = ossConfig;
    }

    /**
     * 生成 OSS 上传参数
     *
     * @param fileName 文件名
     * @return 包含上传 URL 和参数的 Map
     */
    public Map<String, String> generatePresignedUrl(String fileName) {
        OSS ossClient = new OSSClientBuilder()
                .build(ossConfig.getEndpoint(), ossConfig.getAccessKeyId(), ossConfig.getAccessKeySecret());

        try {
            Date expiration = new Date(System.currentTimeMillis() + 15 * 60 * 1000); // 15 分钟后过期

            GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(ossConfig.getBucketName(), fileName);
            request.setExpiration(expiration);
            request.setMethod(com.aliyun.oss.HttpMethod.PUT); // 允许前端 PUT 上传文件
            URL signedUrl = ossClient.generatePresignedUrl(request);

            Map<String, String> result = new HashMap<>();
            result.put("url", signedUrl.toString());
            result.put("bucket", ossConfig.getBucketName());
            result.put("region", extractRegionFromEndpoint(ossConfig.getEndpoint()));

            // 输出生成的签名URL和参数
            logger.info("Generated signed URL: {}", signedUrl);
            logger.info("Bucket: {}", ossConfig.getBucketName());
            logger.info("Region: {}", extractRegionFromEndpoint(ossConfig.getEndpoint()));
            logger.info("Full result map: {}", result);

            return result;
        } catch (Exception e) {
            logger.error("Failed to generate OSS upload parameters", e);
            throw new RuntimeException("Failed to generate OSS upload parameters", e);
        } finally {
            ossClient.shutdown();
        }
    }

    /**
     * 从 endpoint 中提取区域
     *
     * @param endpoint OSS endpoint
     * @return 区域（如 oss-cn-beijing）
     */
    private String extractRegionFromEndpoint(String endpoint) {
        try {
            // 去掉协议部分（如 https://）
            String withoutProtocol = endpoint.split("//")[1];
            // 提取区域部分
            return withoutProtocol.split("\\.")[0];
        } catch (ArrayIndexOutOfBoundsException e) {
            logger.error("Invalid OSS endpoint format: {}", endpoint);
            throw new IllegalArgumentException("Invalid OSS endpoint format: " + endpoint);
        }
    }
}