package app.xmum.xplorer.backend.groupbooking.mapper;

import app.xmum.xplorer.backend.groupbooking.pojo.UserPO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper {

    @Insert("INSERT INTO dim_user (user_uuid, student_id, user_contact_info, student_name, user_avatar_url, created_at, updated_at) " +
            "VALUES (#{userUuid}, #{studentId}, #{userContactInfo}, #{studentName}, #{userAvatarUrl}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "userId")
    void insert(UserPO user);

    @Select("SELECT * FROM dim_user WHERE user_id = #{userId}")
    UserPO findById(Long userId);

    @Select("SELECT * FROM dim_user WHERE user_uuid = #{userUuid}")
    UserPO findByUserUuid(String userUuid);

    @Select("SELECT * FROM dim_user WHERE student_id = #{studentId}")
    UserPO findByStudentId(String studentId);

    @Update("UPDATE dim_user SET user_contact_info = #{userContactInfo}, student_name = #{studentName}, user_avatar_url = #{userAvatarUrl}, updated_at = #{updatedAt} WHERE user_id = #{userId}")
    void update(UserPO user);

    @Delete("DELETE FROM dim_user WHERE user_id = #{userId}")
    void delete(Long userId);
}