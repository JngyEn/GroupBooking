package app.xmum.xplorer.backend.groupbooking.service;

import app.xmum.xplorer.backend.groupbooking.enums.ErrorCode;
import app.xmum.xplorer.backend.groupbooking.mapper.ActivityFavoriteMapper;
import app.xmum.xplorer.backend.groupbooking.pojo.po.ActivityFavoritePO;
import app.xmum.xplorer.backend.groupbooking.response.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ActivityFavoriteService {

    @Autowired
    private ActivityFavoriteMapper favoriteMapper;

    // 收藏活动
    @Transactional
    public ApiResponse<?> favoriteActivity(String userUuid, String activityUuid) {
            ActivityFavoritePO favorite = new ActivityFavoritePO();
            favorite.setFavoriteUuid(UUID.randomUUID().toString().replace("-", ""));
            favorite.setActivityUuid(activityUuid);
            favorite.setUserUuid(userUuid);
            favorite.setFavoriteStatus(0);
            favorite.setCreatedAt(LocalDateTime.now());
            favorite.setUpdatedAt(LocalDateTime.now());
            favoriteMapper.insertFavorite(favorite);
            return ApiResponse.success();
    }

    public ApiResponse<ActivityFavoritePO> findFavoriteByUserAndActivity(String userUuid, String activityUuid) {
        ActivityFavoritePO existingFavorite = favoriteMapper.selectFavoriteByUserAndActivity(userUuid, activityUuid);
        if (existingFavorite != null && existingFavorite.getFavoriteStatus() == 0) {
            return ApiResponse.fail(ErrorCode.BAD_REQUEST, "活动已被收藏");
        }
        return ApiResponse.success(existingFavorite);
    }
    // 取消收藏
    @Transactional
    public ApiResponse<?> unfavoriteActivity(String userUuid, String activityUuid) {
        ActivityFavoritePO favorite = favoriteMapper.selectFavoriteByUserAndActivity(userUuid, activityUuid);
        if (favorite != null && favorite.getFavoriteStatus() == 0) {
            favorite.setFavoriteStatus(1); // 1 表示取消收藏
            favorite.setUpdatedAt(LocalDateTime.now());
            favoriteMapper.updateFavoriteStatus(favorite);
            return ApiResponse.success("用户：" + userUuid + " 取消收藏活动：" + activityUuid + " 成功");
        } else {
            return ApiResponse.fail(ErrorCode.BAD_REQUEST, "用户：" + userUuid + " 未取消收藏活动：" + activityUuid + "，无法取消收藏");
        }
    }

    // 再次收藏
    @Transactional
    public void refavoriteActivity(String userUuid, String activityUuid) {
        ActivityFavoritePO favorite = favoriteMapper.selectFavoriteByUserAndActivity(userUuid, activityUuid);
        if (favorite != null) {
            favorite.setFavoriteStatus(0); // 0 表示再次收藏
            favorite.setUpdatedAt(LocalDateTime.now());
            favoriteMapper.updateFavoriteStatus(favorite);
        }
    }

    // 获取用户的所有收藏
    public ApiResponse<List<ActivityFavoritePO>> getUserFavorites(String userUuid) {
        return ApiResponse.success(favoriteMapper.selectFavoritesByUserUuid(userUuid));
    }
}