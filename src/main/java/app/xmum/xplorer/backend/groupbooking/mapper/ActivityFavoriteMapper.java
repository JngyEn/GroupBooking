package app.xmum.xplorer.backend.groupbooking.mapper;

import app.xmum.xplorer.backend.groupbooking.pojo.ActivityFavoritePO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ActivityFavoriteMapper {

    @Insert("INSERT INTO fact_activity_favorite (favorite_uuid, activity_uuid, user_uuid, favorite_status, created_at, updated_at) " +
            "VALUES (#{favoriteUuid}, #{activityUuid}, #{userUuid}, #{favoriteStatus}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "favoriteId")
    void insert(ActivityFavoritePO favorite);

    @Select("SELECT * FROM fact_activity_favorite WHERE favorite_id = #{favoriteId}")
    ActivityFavoritePO findById(Long favoriteId);

    @Select("SELECT * FROM fact_activity_favorite WHERE user_uuid = #{userUuid}")
    List<ActivityFavoritePO> findByUserUuid(String userUuid);

    @Select("SELECT * FROM fact_activity_favorite WHERE activity_uuid = #{activityUuid}")
    List<ActivityFavoritePO> findByActivityUuid(String activityUuid);

    @Update("UPDATE fact_activity_favorite SET favorite_status = #{favoriteStatus}, updated_at = #{updatedAt} WHERE favorite_id = #{favoriteId}")
    void update(ActivityFavoritePO favorite);

    @Delete("DELETE FROM fact_activity_favorite WHERE favorite_id = #{favoriteId}")
    void delete(Long favoriteId);
}