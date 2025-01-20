package app.xmum.xplorer.backend.groupbooking.service;

import app.xmum.xplorer.backend.groupbooking.mapper.UserMapper;
import app.xmum.xplorer.backend.groupbooking.pojo.UserPO;
import app.xmum.xplorer.backend.groupbooking.response.ApiResponse;
import app.xmum.xplorer.backend.groupbooking.utils.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static app.xmum.xplorer.backend.groupbooking.response.ApiResponse.fail;
@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;


    // 创建用户
    public ApiResponse<UserPO> createUser(UserPO user) {
        try {
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());
            user.setUserUuid(UUID.randomUUID().toString().replace("-", ""));
            int result = userMapper.insertUser(user);
            if (result > 0) {
                return ApiResponse.success(user);
            }
            return fail(ErrorCode.INTERNAL_ERROR);
        } catch (Exception e) {
            return fail(ErrorCode.INTERNAL_ERROR, e.getMessage());
        }
    }

    // 根据用户UUID查询用户
    public ApiResponse<UserPO> getUserByUuid(String userUuid) {
        try {
            UserPO user = userMapper.selectUserByUuid(userUuid);
            if (user != null) {
                return ApiResponse.success(user);
            }
            return fail(ErrorCode.NOT_FOUND, "User not found");
        } catch (Exception e) {
            return fail(ErrorCode.INTERNAL_ERROR, e.getMessage());
        }
    }

    // 查询所有用户
    public ApiResponse<List<UserPO>> getAllUsers() {
        try {
            List<UserPO> users = userMapper.selectAllUsers();
            return ApiResponse.success(users);
        } catch (Exception e) {
            return fail(ErrorCode.INTERNAL_ERROR, e.getMessage());
        }
    }

    // 更新用户头像
    public ApiResponse<UserPO> updateUserAvatar(String userUuid, String avatarUrl) {
        try {
            UserPO user = userMapper.selectUserByUuid(userUuid);
            if (user != null) {
                user.setUserAvatarUrl(avatarUrl);
                user.setUpdatedAt(LocalDateTime.now());
                int result = userMapper.updateUser(user);
                if (result > 0) {
                    return ApiResponse.success(user);
                }
                return fail(ErrorCode.INTERNAL_ERROR, "Failed to update user avatar");
            }
            return fail(ErrorCode.NOT_FOUND, "User not found");
        } catch (Exception e) {
            return fail(ErrorCode.INTERNAL_ERROR, e.getMessage());
        }
    }

    // 更新用户名称
    public ApiResponse<UserPO> updateUserName(String userUuid, String name) {
        try {
            UserPO user = userMapper.selectUserByUuid(userUuid);
            if (user != null) {
                user.setStudentName(name);
                user.setUpdatedAt(LocalDateTime.now());
                int result = userMapper.updateUser(user);
                if (result > 0) {
                    return ApiResponse.success(user);
                }
                return fail(ErrorCode.INTERNAL_ERROR, "Failed to update user name");
            }
            return fail(ErrorCode.NOT_FOUND, "User not found");
        } catch (Exception e) {
            return fail(ErrorCode.INTERNAL_ERROR, e.getMessage());
        }
    }

    // 删除用户
    public ApiResponse<?> deleteUser(String userUuid) {
        try {
            int result = userMapper.deleteUserByUuid(userUuid);
            if (result > 0) {
                return ApiResponse.success();
            }
            return fail(ErrorCode.NOT_FOUND, "User not found");
        } catch (Exception e) {
            return fail(ErrorCode.INTERNAL_ERROR, e.getMessage());
        }
    }
}