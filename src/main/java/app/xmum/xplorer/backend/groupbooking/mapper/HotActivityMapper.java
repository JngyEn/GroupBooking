package app.xmum.xplorer.backend.groupbooking.mapper;

import app.xmum.xplorer.backend.groupbooking.pojo.po.HotActivityPO;
import org.apache.ibatis.annotations.*;
import java.util.List;
@Mapper
public interface HotActivityMapper {
    @Insert("INSERT INTO fact_hot_activity (activity_uuid, activity_heat, is_active, version_id, is_current, created_at, updated_at) " +
            "VALUES (#{activityUuid}, #{activityHeat}, #{isActive}, #{versionId}, #{isCurrent}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "hotActivityId")
    void insert(HotActivityPO factHotActivity);

    @Update("UPDATE fact_hot_activity SET activity_uuid = #{activityUuid}, activity_heat = #{activityHeat}, is_active = #{isActive}, " +
            "version_id = #{versionId}, is_current = #{isCurrent}, updated_at = #{updatedAt} WHERE hot_activity_id = #{hotActivityId}")
    void update(HotActivityPO factHotActivity);

    @Delete("DELETE FROM fact_hot_activity WHERE hot_activity_id = #{hotActivityId}")
    void deleteById(Long hotActivityId);

    @Select("SELECT * FROM fact_hot_activity WHERE hot_activity_id = #{hotActivityId}")
    HotActivityPO selectById(Long hotActivityId);

    @Select("SELECT * FROM fact_hot_activity WHERE activity_uuid = #{activityUuid} AND is_current = true")
    HotActivityPO selectByActivityUuid(String activityUuid);

    @Select("SELECT * FROM fact_hot_activity")
    List<HotActivityPO> selectAll();

    // 基于 activity_uuid 更新活动状态
    @Update("UPDATE fact_hot_activity SET is_active = #{isActive}, updated_at = NOW() WHERE activity_uuid = #{activityUuid} AND is_current = true")
    void updateStatusByUuid(@Param("activityUuid") String activityUuid, @Param("isActive") Boolean isActive);

    // 将 version_id 相同的某个批次的 is_current 都改为 false
    @Update("UPDATE fact_hot_activity SET is_current = false, updated_at = NOW() WHERE version_id = #{versionId}")
    void batchUpdateIsCurrentToFalse(@Param("versionId") String versionId);

    // 获取当前最大版本号
    @Select("SELECT MAX(version_id) FROM fact_hot_activity")
    String getMaxVersionId();
}
