package app.xmum.xplorer.backend.groupbooking.service;

import app.xmum.xplorer.backend.groupbooking.mapper.UserVisitLogMapper;
import app.xmum.xplorer.backend.groupbooking.pojo.ActivityPO;
import app.xmum.xplorer.backend.groupbooking.pojo.UserVisitLogPO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class VisitLogService {

    @Autowired
    private UserVisitLogMapper visitLogMapper;


    public void createVisitLog(ActivityPO activityPO, String  uid) {
        UserVisitLogPO visitLog = new UserVisitLogPO();
        visitLog.setActivityUuid(activityPO.getActivityUuid());
        visitLog.setUserUuid(uid);
        visitLog.setCategoryUuid(activityPO.getCategoryUuid());
        visitLog.setCreatedAt(LocalDateTime.now());
        visitLog.setUpdatedAt(LocalDateTime.now());
        visitLogMapper.insert(visitLog);
    }

    public int countByActivityUuid(String activityUuid) {
        return visitLogMapper.countByActivityUuid(activityUuid);
    }

    /**
     * 根据ID查询访问记录
     *
     * @param visitLogId 访问记录ID
     * @return 访问记录对象
     */
    public UserVisitLogPO getVisitLogById(Long visitLogId) {
        return visitLogMapper.findById(visitLogId);
    }

    /**
     * 根据用户UUID查询访问记录
     *
     * @param userUuid 用户UUID
     * @return 访问记录列表
     */
    public List<UserVisitLogPO> getVisitLogsByUserUuid(String userUuid) {
        return visitLogMapper.findByUserUuid(userUuid);
    }

    /**
     * 根据活动UUID查询访问记录
     *
     * @param activityUuid 活动UUID
     * @return 访问记录列表
     */
    public List<UserVisitLogPO> getVisitLogsByActivityUuid(String activityUuid) {
        return visitLogMapper.findByActivityUuid(activityUuid);
    }

    /**
     * 根据活动种类UUID查询访问记录
     *
     * @param categoryUuid 活动种类UUID
     * @return 访问记录列表
     */
    public List<UserVisitLogPO> getVisitLogsByCategoryUuid(String categoryUuid) {
        return visitLogMapper.findByCategoryUuid(categoryUuid);
    }

    /**
     * 更新访问记录
     *
     * @param visitLog 访问记录对象
     */
    public void updateVisitLog(UserVisitLogPO visitLog) {
        visitLog.setUpdatedAt(LocalDateTime.now());
        visitLogMapper.update(visitLog);
    }

    /**
     * 删除访问记录
     *
     * @param visitLogId 访问记录ID
     */
    public void deleteVisitLog(Long visitLogId) {
        visitLogMapper.delete(visitLogId);
    }
}