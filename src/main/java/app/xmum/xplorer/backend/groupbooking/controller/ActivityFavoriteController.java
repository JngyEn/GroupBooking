package app.xmum.xplorer.backend.groupbooking.controller;

import app.xmum.xplorer.backend.groupbooking.enums.ErrorCode;
import app.xmum.xplorer.backend.groupbooking.pojo.ActivityFavoritePO;
import app.xmum.xplorer.backend.groupbooking.response.ApiResponse;
import app.xmum.xplorer.backend.groupbooking.service.ActivityFavoriteService;
import app.xmum.xplorer.backend.groupbooking.service.UserService;
import app.xmum.xplorer.backend.groupbooking.service.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/favorites")
public class ActivityFavoriteController {

    @Autowired
    private ActivityFavoriteService favoriteService;
    @Autowired
    private UserService userService;
    @Autowired
    private ValidationService validationService;

    // 取消收藏
    @PostMapping("/unfavorite")
    public ApiResponse<?> unfavoriteActivity(@RequestParam String userUuid, @RequestParam String activityUuid) {
        if (userService.getUserByUuid(userUuid).getCode() == ErrorCode.NOT_FOUND.getCode()) {
            return ApiResponse.fail(ErrorCode.NOT_FOUND, "用户不存在");
        }
        return ApiResponse.success(favoriteService.unfavoriteActivity(userUuid, activityUuid));
    }

    // 获取用户的所有收藏
    @GetMapping("/user/{userUuid}")
    public ApiResponse<?> getUserFavorites(@PathVariable String userUuid) {
        ApiResponse<?> checkResult = validationService.validationCheckUser(userUuid);
        if (checkResult.getCode() != 200) {
            return checkResult;
        }
        return favoriteService.getUserFavorites(userUuid);
    }
}