package app.xmum.xplorer.backend.groupbooking.mapper;

import app.xmum.xplorer.backend.groupbooking.pojo.po.UserPO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper {

    // 插入用户
    @Insert("INSERT INTO dim_user (user_uuid, student_id, student_name, user_avatar_url, created_at, updated_at) " +
            "VALUES (#{userUuid}, #{studentId}, #{studentName}, #{userAvatarUrl}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "userId")
    int insertUser(UserPO user);

    // 根据 userUuid 查询用户
    @Select("SELECT * FROM dim_user WHERE user_uuid = #{userUuid}")
    UserPO selectUserByUuid(String userUuid);

    // 查询所有用户
    @Select("SELECT * FROM dim_user")
    List<UserPO> selectAllUsers();

    // 更新用户信息
    @Update("UPDATE dim_user SET student_name = #{studentName}, user_avatar_url = #{userAvatarUrl}, updated_at = #{updatedAt} WHERE user_uuid = #{userUuid}")
    int updateUser(UserPO user);

    // 根据 userUuid 删除用户
    @Delete("DELETE FROM dim_user WHERE user_uuid = #{userUuid}")
    int deleteUserByUuid(String userUuid);
}