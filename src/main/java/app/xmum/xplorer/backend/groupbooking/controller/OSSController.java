package app.xmum.xplorer.backend.groupbooking.controller;

import app.xmum.xplorer.backend.groupbooking.response.ApiResponse;
import app.xmum.xplorer.backend.groupbooking.enums.ErrorCode;
import app.xmum.xplorer.backend.groupbooking.utils.OSSUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class OSSController {

    @Autowired
    private OSSUtil ossUtil;

    @GetMapping("/generate-presigned-url")
    public ApiResponse<Map<String, String>> generatePresignedUrl(@RequestParam String fileName) {
        try {
            Map<String, String> ossParams = ossUtil.generatePresignedUrl(fileName);
            return ApiResponse.success(ossParams);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ApiResponse.fail(ErrorCode.SERVICE_UNAVAILABLE, e.getMessage());
        }
    }
}