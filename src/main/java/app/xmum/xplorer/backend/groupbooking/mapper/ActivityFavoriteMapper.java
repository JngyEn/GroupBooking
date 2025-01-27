package app.xmum.xplorer.backend.groupbooking.mapper;

import app.xmum.xplorer.backend.groupbooking.pojo.ActivityFavoritePO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ActivityFavoriteMapper {

    // 插入收藏记录
    @Insert("INSERT INTO fact_activity_favorite (favorite_uuid, activity_uuid, user_uuid, favorite_status, created_at, updated_at) " +
            "VALUES (#{favoriteUuid}, #{activityUuid}, #{userUuid}, #{favoriteStatus}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "favoriteId")
    int insertFavorite(ActivityFavoritePO favorite);

    // 根据用户UUID查询收藏记录
    @Select("SELECT * FROM fact_activity_favorite WHERE user_uuid = #{userUuid}")
    List<ActivityFavoritePO> selectFavoritesByUserUuid(String userUuid);

    // 根据用户UUID和活动UUID查询收藏记录
    @Select("SELECT * FROM fact_activity_favorite WHERE user_uuid = #{userUuid} AND activity_uuid = #{activityUuid}")
    ActivityFavoritePO selectFavoriteByUserAndActivity(String userUuid, String activityUuid);

    // 更新收藏状态
    @Update("UPDATE fact_activity_favorite SET favorite_status = #{favoriteStatus}, updated_at = #{updatedAt} " +
            "WHERE favorite_uuid = #{favoriteUuid}")
    int updateFavoriteStatus(ActivityFavoritePO favorite);
}