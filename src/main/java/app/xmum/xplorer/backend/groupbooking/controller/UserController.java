package app.xmum.xplorer.backend.groupbooking.controller;

import app.xmum.xplorer.backend.groupbooking.pojo.po.UserPO;
import app.xmum.xplorer.backend.groupbooking.response.ApiResponse;
import app.xmum.xplorer.backend.groupbooking.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    // 创建用户
    @PostMapping
    public ApiResponse<UserPO> createUser(@RequestBody UserPO user) {
        return userService.createUser(user);
    }

    // 根据用户UUID查询用户
    @GetMapping("/{userUuid}")
    public ApiResponse<UserPO> getUserByUuid(@PathVariable String userUuid) {
        return userService.getUserByUuid(userUuid);
    }

    // 查询所有用户
    @GetMapping
    public ApiResponse<List<UserPO>> getAllUsers() {
        return userService.getAllUsers();
    }

    // 更新用户头像
    @PatchMapping("/{userUuid}/avatar")
    public ApiResponse<UserPO> updateUserAvatar(
            @PathVariable String userUuid,
            @RequestParam String avatarUrl
    ) {
        return userService.updateUserAvatar(userUuid, avatarUrl);
    }

    // 更新用户名称
    @PatchMapping("/{userUuid}/name")
    public ApiResponse<UserPO> updateUserName(
            @PathVariable String userUuid,
            @RequestParam String name
    ) {
        return userService.updateUserName(userUuid, name);
    }

    // 删除用户
    @DeleteMapping("/{userUuid}")
    public ApiResponse<?> deleteUser(@PathVariable String userUuid) {
        return userService.deleteUser(userUuid);
    }
}