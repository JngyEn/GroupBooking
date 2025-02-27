package app.xmum.xplorer.backend.groupbooking.mapper;

import app.xmum.xplorer.backend.groupbooking.pojo.po.ActivityPO;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

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

    // 收藏活动
    @Update("UPDATE dim_activity SET activity_collect_num = activity_collect_num + 1 WHERE activity_uuid = #{activityUuid}")
    void collectActivity(@Param("activityUuid") String activityUuid);

    // 取消收藏
    @Update("UPDATE dim_activity SET activity_collect_num = activity_collect_num - 1 WHERE activity_uuid = #{activityUuid} AND activity_collect_num > 0")
    void cancelCollectActivity(@Param("activityUuid") String activityUuid);

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
    // 热度排序游标分页，热度从高到低
    @Select("SELECT * FROM dim_activity WHERE activity_status NOT IN (7, 8) AND activity_heat < #{cursor}  ORDER BY activity_heat DESC LIMIT #{size}")
    List<ActivityPO> findAllOrderByHeatWithLimit(@Param("cursor") Double heat, @Param("size") Integer size);
    // 按照收藏数排序查询活动，排除已结束和已取消的活动
    @Select("SELECT * FROM dim_activity WHERE activity_status NOT IN (7, 8) ORDER BY activity_collect_num DESC")
    List<ActivityPO> findAllOrderByCollect();
    // 收藏数排序游标分页，收藏数从高到低
    @Select("SELECT * FROM dim_activity WHERE activity_status NOT IN (7, 8) AND activity_collect_num < #{cursor}  ORDER BY activity_collect_num DESC LIMIT #{size}")
    List<ActivityPO> findAllOrderByCollectWithLimit(@Param("cursor") Integer collect, @Param("size") Integer size);
    // 按照活动开始时间排序查询活动，排除已结束和已取消的活动
    @Select("SELECT * FROM dim_activity WHERE activity_status NOT IN (7, 8) ORDER BY activity_begin_time ")
    List<ActivityPO> findAllOrderByBeginTime();
    // 活动开始时间排序游标分页，活动开始时间从早到晚
    @Select("SELECT * FROM dim_activity WHERE activity_status NOT IN (7, 8) AND activity_begin_time < #{cursor}  ORDER BY activity_begin_time LIMIT #{size}")
    List<ActivityPO> findAllOrderByBeginTimeWithLimit(@Param("cursor") Long beginTime, @Param("size") Integer size);
    // 按照报名截止时间排序查询活动，排除已结束和已取消的活动
    @Select("SELECT * FROM dim_activity WHERE activity_status NOT IN (7, 8) ORDER BY activity_register_end_time ASC")
    List<ActivityPO> findAllOrderByRegisterEndTime();
    // 报名截止时间排序游标分页，报名截止时间从早到晚
    @Select("SELECT * FROM dim_activity WHERE activity_status NOT IN (7, 8) AND activity_register_end_time < #{cursor}  ORDER BY activity_register_end_time LIMIT #{size}")
    List<ActivityPO> findAllOrderByRegisterEndTimeWithLimit(@Param("cursor") Long registerEndTime, @Param("size") Integer size);

    //多条件查询
    List<ActivityPO> findActivitiesByCriteria(Map<String, Object> criteria);

}