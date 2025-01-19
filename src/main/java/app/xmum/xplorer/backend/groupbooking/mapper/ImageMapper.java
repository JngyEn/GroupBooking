package app.xmum.xplorer.backend.groupbooking.mapper;

import app.xmum.xplorer.backend.groupbooking.pojo.ImagePO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ImageMapper {

    @Insert("INSERT INTO dim_activity_image (image_uuid, activity_uuid, user_uuid, image_url, image_width, image_height, created_at, updated_at) " +
            "VALUES (#{imageUuid}, #{activityUuid}, #{userUuid}, #{imageUrl}, #{imageWidth}, #{imageHeight}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "imageId")
    void insert(ImagePO image);

    @Select("SELECT * FROM dim_activity_image WHERE image_id = #{imageId}")
    ImagePO findById(Long imageId);

    @Select("SELECT * FROM dim_activity_image WHERE activity_uuid = #{activityUuid}")
    List<ImagePO> findByActivityUuid(String activityUuid);

    @Select("SELECT * FROM dim_activity_image WHERE user_uuid = #{userUuid}")
    List<ImagePO> findByUserUuid(String userUuid);

    @Update("UPDATE dim_activity_image SET image_url = #{imageUrl}, image_width = #{imageWidth}, image_height = #{imageHeight}, updated_at = #{updatedAt} WHERE image_id = #{imageId}")
    void update(ImagePO image);

    @Delete("DELETE FROM dim_activity_image WHERE image_id = #{imageId}")
    void delete(Long imageId);
}