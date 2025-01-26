package app.xmum.xplorer.backend.groupbooking.pojo;
import lombok.Data;

import java.time.LocalDateTime;
@Data
public class DimActivityCategoryPO {
    private Long categoryId;          // 活动种类的自增主键
    private String categoryUuid;      // 活动种类UUID
    private String categoryName;      // 种类名字
    private Long categoryVisitNum;    // 分类的访问次数
    private Long categoryActivityNum; // 分类内的活动数量
    private LocalDateTime createdAt;  // 创建时间
    private LocalDateTime updatedAt;  // 更新时间
}



