package app.xmum.xplorer.backend.groupbooking.mapper;

import app.xmum.xplorer.backend.groupbooking.pojo.ContactPO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ContactMapper {

    @Insert("INSERT INTO dim_user_contact (contact_uuid, activity_uuid, user_uuid, contact_type, contact_value, target_type, created_at, updated_at) " +
            "VALUES (#{contactUuid}, #{activityUuid}, #{userUuid}, #{contactType}, #{contactValue}, #{targetType}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "contactId")
    void insert(ContactPO contact);

    @Select("SELECT * FROM dim_user_contact WHERE contact_id = #{contactId}")
    ContactPO findById(Long contactId);

    @Select("SELECT * FROM dim_user_contact WHERE user_uuid = #{userUuid}")
    List<ContactPO> findByUserUuid(String userUuid);

    @Select("SELECT * FROM dim_user_contact WHERE activity_uuid = #{activityUuid}")
    List<ContactPO> findByActivityUuid(String activityUuid);

    @Select("SELECT * FROM dim_user_contact WHERE contact_uuid = #{contactUuid}")
    ContactPO findByContactUuid(String contactUuid);

    @Update("UPDATE dim_user_contact SET contact_type = #{contactType}, contact_value = #{contactValue}, target_type = #{targetType}, updated_at = #{updatedAt} WHERE contact_id = #{contactId}")
    void update(ContactPO contact);

    @Delete("DELETE FROM dim_user_contact WHERE contact_id = #{contactId}")
    void delete(Long contactId);
}