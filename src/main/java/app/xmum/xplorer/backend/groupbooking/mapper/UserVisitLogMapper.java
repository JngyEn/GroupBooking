package app.xmum.xplorer.backend.groupbooking.mapper;

import app.xmum.xplorer.backend.groupbooking.pojo.po.UserVisitLogPO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserVisitLogMapper {

    @Insert("INSERT INTO fact_user_visit_log (visit_uuid, category_uuid, activity_uuid, user_uuid, created_at, updated_at) " +
            "VALUES (#{visitUuid}, #{categoryUuid}, #{activityUuid}, #{userUuid}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "visitLogId")
    void insert(UserVisitLogPO visitLog);

    @Select("SELECT * FROM fact_user_visit_log WHERE visit_log_id = #{visitLogId}")
    UserVisitLogPO findById(Long visitLogId);

    @Select("SELECT * FROM fact_user_visit_log WHERE user_uuid = #{userUuid}")
    List<UserVisitLogPO> findByUserUuid(String userUuid);

    @Select("SELECT * FROM fact_user_visit_log WHERE activity_uuid = #{activityUuid}")
    List<UserVisitLogPO> findByActivityUuid(String activityUuid);

    @Select("SELECT * FROM fact_user_visit_log WHERE category_uuid = #{categoryUuid}")
    List<UserVisitLogPO> findByCategoryUuid(String categoryUuid);

    @Update("UPDATE fact_user_visit_log SET updated_at = #{updatedAt} WHERE visit_log_id = #{visitLogId}")
    void update(UserVisitLogPO visitLog);

    @Delete("DELETE FROM fact_user_visit_log WHERE visit_log_id = #{visitLogId}")
    void delete(Long visitLogId);

    @Select("SELECT COUNT_(*) FROM fact_user_visit_log WHERE activity_uuid = #{activityUuid}")
    int countByActivityUuid(String activityUuid);
}