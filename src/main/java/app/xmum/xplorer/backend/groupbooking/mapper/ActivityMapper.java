package app.xmum.xplorer.backend.groupbooking.mapper;

import app.xmum.xplorer.backend.groupbooking.pojo.ActivityPO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ActivityMapper {

    @Select("SELECT * FROM dim_activity WHERE activity_id = #{id}")
    ActivityPO findById(@Param("id") Long id);

    @Insert("INSERT INTO dim_activity (activity_uuid, activity_name, activity_desc_text, activity_location, activity_begin_time, activity_end_time, activity_register_start_time, activity_register_end_time, activity_person_min, activity_person_max, activity_status, activity_person_now, activity_visit_num, activity_collect_num, activity_heat, category_uuid, user_uuid) " +
            "VALUES (#{activityUuid}, #{activityName}, #{activityDescText}, #{activityLocation}, #{activityBeginTime}, #{activityEndTime}, #{activityRegisterStartTime}, #{activityRegisterEndTime}, #{activityPersonMin}, #{activityPersonMax}, #{activityStatus}, #{activityPersonNow}, #{activityVisitNum}, #{activityCollectNum}, #{activityHeat}, #{categoryUuid}, #{userUuid})")
    @Options(useGeneratedKeys = true, keyProperty = "activityId")
    void insert(ActivityPO activityPO);

    @Update("UPDATE dim_activity SET activity_name = #{activityName}, activity_desc_text = #{activityDescText}, activity_location = #{activityLocation}, activity_begin_time = #{activityBeginTime}, activity_end_time = #{activityEndTime}, activity_register_start_time = #{activityRegisterStartTime}, activity_register_end_time = #{activityRegisterEndTime}, activity_person_min = #{activityPersonMin}, activity_person_max = #{activityPersonMax}, activity_status = #{activityStatus}, activity_person_now = #{activityPersonNow}, activity_visit_num = #{activityVisitNum}, activity_collect_num = #{activityCollectNum}, activity_heat = #{activityHeat}, category_uuid = #{categoryUuid}, user_uuid = #{userUuid} WHERE activity_id = #{activityId}")
    void update(ActivityPO activityPO);

    @Delete("DELETE FROM dim_activity WHERE activity_id = #{id}")
    void deleteById(@Param("id") Long id);

    @Select("SELECT * FROM dim_activity")
    List<ActivityPO> findAll();
}