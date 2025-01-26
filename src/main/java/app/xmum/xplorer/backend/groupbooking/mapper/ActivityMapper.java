package app.xmum.xplorer.backend.groupbooking.mapper;

import app.xmum.xplorer.backend.groupbooking.pojo.ActivityPO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ActivityMapper {

    // 根据活动 ID 查询活动
    @Select("SELECT * FROM dim_activity WHERE activity_uuid = #{uuid}")
    ActivityPO findByUid(@Param("uuid") String uuid);

    // 插入活动
    @Insert("INSERT INTO dim_activity (activity_uuid, activity_name, activity_desc_text, activity_location, " +
            "activity_begin_time, activity_end_time, activity_register_start_time, activity_register_end_time, " +
            "activity_person_min, activity_person_max, activity_status, activity_person_now, activity_visit_num, " +
            "activity_collect_num, activity_heat, category_uuid, user_uuid, comment_count) " +
            "VALUES (#{activityUuid}, #{activityName}, #{activityDescText}, #{activityLocation}, " +
            "#{activityBeginTime}, #{activityEndTime}, #{activityRegisterStartTime}, #{activityRegisterEndTime}, " +
            "#{activityPersonMin}, #{activityPersonMax}, #{activityStatus, typeHandler=app.xmum.xplorer.backend.groupbooking.handler.ActivityStatusEnumTypeHandler}, #{activityPersonNow}, #{activityVisitNum}, " +
            "#{activityCollectNum}, #{activityHeat}, #{categoryUuid}, #{userUuid}, #{commentCount})")
    @Options(useGeneratedKeys = true, keyProperty = "activityId")
    void insert(ActivityPO activityPO);

    // 参加活动
    @Update("UPDATE dim_activity SET activity_person_now = activity_person_now + 1 WHERE activity_uuid = #{activityUuid} AND activity_person_now < activity_person_max")
    void joinActivity(@Param("activityUuid") String activityUuid);

    // 退出活动
    @Update("UPDATE dim_activity SET activity_person_now = activity_person_now - 1 WHERE activity_uuid = #{activityUuid} AND activity_person_now > 0")
    void cancelJoinActivity(@Param("activityUuid") String activityUuid);
    // 更新活动信息
    @Update("UPDATE dim_activity SET activity_name = #{activityName}, activity_desc_text = #{activityDescText}, " +
            "activity_location = #{activityLocation}, activity_begin_time = #{activityBeginTime}, " +
            "activity_end_time = #{activityEndTime}, activity_register_start_time = #{activityRegisterStartTime}, " +
            "activity_register_end_time = #{activityRegisterEndTime}, activity_person_min = #{activityPersonMin}, " +
            "activity_person_max = #{activityPersonMax}, activity_status = #{activityStatus, typeHandler=app.xmum.xplorer.backend.groupbooking.handler.ActivityStatusEnumTypeHandler}, " +
            "activity_person_now = #{activityPersonNow}, activity_visit_num = #{activityVisitNum}, " +
            "activity_collect_num = #{activityCollectNum}, activity_heat = #{activityHeat}, " +
            "category_uuid = #{categoryUuid}, user_uuid = #{userUuid}, comment_count = #{commentCount} " +
            "WHERE activity_id = #{activityId}")
    void update(ActivityPO activityPO);

    // 根据活动 ID 删除活动
    @Delete("DELETE FROM dim_activity WHERE activity_id = #{id}")
    void deleteById(@Param("id") Long id);

    // 查询所有活动
    @Select("SELECT * FROM dim_activity")
    List<ActivityPO> findAll();
    @Select("SELECT * FROM dim_activity WHERE activity_status NOT IN (7, 8)")
    List<ActivityPO> findAllActive();
    // 更新评论数量
    @Update("UPDATE dim_activity SET comment_count = #{commentCount} WHERE activity_id = #{activityId}")
    void updateCommentCount(@Param("activityId") Long activityId, @Param("commentCount") Integer commentCount);

    // 按照热度排序查询活动，排除已结束和已取消的活动
    @Select("SELECT * FROM dim_activity WHERE activity_status NOT IN (7, 8) ORDER BY activity_heat DESC")
    List<ActivityPO> findAllOrderByHeat();

    // 按照收藏数排序查询活动，排除已结束和已取消的活动
    @Select("SELECT * FROM dim_activity WHERE activity_status NOT IN (7, 8) ORDER BY activity_collect_num DESC")
    List<ActivityPO> findAllOrderByCollect();

    // 按照活动开始时间排序查询活动，排除已结束和已取消的活动
    @Select("SELECT * FROM dim_activity WHERE activity_status NOT IN (7, 8) ORDER BY activity_begin_time ASC")
    List<ActivityPO> findAllOrderByBeginTime();

    // 按照报名截止时间排序查询活动，排除已结束和已取消的活动
    @Select("SELECT * FROM dim_activity WHERE activity_status NOT IN (7, 8) ORDER BY activity_register_end_time ASC")
    List<ActivityPO> findAllOrderByRegisterEndTime();
}