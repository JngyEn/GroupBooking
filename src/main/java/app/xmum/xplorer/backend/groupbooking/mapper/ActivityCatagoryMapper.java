package app.xmum.xplorer.backend.groupbooking.mapper;
import app.xmum.xplorer.backend.groupbooking.pojo.DimActivityCategoryPO;
import org.apache.ibatis.annotations.*;

import java.util.List;
public interface ActivityCatagoryMapper {
    @Mapper
    public interface DimActivityCategoryMapper {

        @Insert("INSERT INTO dim_activity_category (category_uuid, category_name, category_visit_num, category_activity_num, created_at, updated_at) " +
                "VALUES (#{categoryUuid}, #{categoryName}, #{categoryVisitNum}, #{categoryActivityNum}, #{createdAt}, #{updatedAt})")
        @Options(useGeneratedKeys = true, keyProperty = "categoryId")
        void insert(DimActivityCategoryPO category);

        @Select("SELECT * FROM dim_activity_category WHERE category_id = #{categoryId}")
        DimActivityCategoryPO findById(Long categoryId);

        @Select("SELECT * FROM dim_activity_category")
        List<DimActivityCategoryPO> findAll();

        @Update("UPDATE dim_activity_category SET category_name = #{categoryName}, category_visit_num = #{categoryVisitNum}, " +
                "category_activity_num = #{categoryActivityNum}, updated_at = #{updatedAt} WHERE category_id = #{categoryId}")
        void update(DimActivityCategoryPO category);

        @Delete("DELETE FROM dim_activity_category WHERE category_id = #{categoryId}")
        void delete(Long categoryId);
    }
}
