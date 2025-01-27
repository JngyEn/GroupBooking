package app.xmum.xplorer.backend.groupbooking.mapper;

import app.xmum.xplorer.backend.groupbooking.pojo.po.CommentPO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CommentMapper {

    @Insert("INSERT INTO fact_comment (comment_uuid, comment_level, activity_uuid, author_uuid, reply_uuid, comment_content, created_at, updated_at) " +
            "VALUES (#{commentUuid}, #{commentLevel}, #{activityUuid}, #{authorUuid}, #{replyUuid}, #{commentContent}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "commentId")
    void insert(CommentPO comment);

    @Select("SELECT * FROM fact_comment WHERE comment_id = #{commentId}")
    CommentPO findById(Long commentId);

    @Select("SELECT * FROM fact_comment WHERE activity_uuid = #{activityUuid}")
    List<CommentPO> findByActivityUuid(String activityUuid);

    @Select("SELECT * FROM fact_comment WHERE author_uuid = #{authorUuid}")
    List<CommentPO> findByAuthorUuid(String authorUuid);

    @Select("SELECT * FROM fact_comment WHERE reply_uuid = #{replyUuid}")
    List<CommentPO> findByReplyUuid(String replyUuid);

    @Update("UPDATE fact_comment SET comment_content = #{commentContent}, updated_at = #{updatedAt} WHERE comment_id = #{commentId}")
    void update(CommentPO comment);

    @Delete("DELETE FROM fact_comment WHERE comment_id = #{commentId}")
    void delete(Long commentId);
}