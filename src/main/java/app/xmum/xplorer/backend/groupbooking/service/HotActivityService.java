package app.xmum.xplorer.backend.groupbooking.service;

import app.xmum.xplorer.backend.groupbooking.config.RedisConstant;
import app.xmum.xplorer.backend.groupbooking.mapper.HotActivityMapper;
import app.xmum.xplorer.backend.groupbooking.pojo.po.ActivityPO;
import app.xmum.xplorer.backend.groupbooking.pojo.po.HotActivityPO;
import app.xmum.xplorer.backend.groupbooking.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class HotActivityService {

    @Autowired
    private HotActivityMapper hotActivityMapper;
    @Autowired
    private RedisUtil redisUtil;

    /**
     * 插入活动数据
     */
    public void insertHotActivity(HotActivityPO hotActivity) {
        hotActivityMapper.insert(hotActivity);
    }

    public HotActivityPO initHotActivity(String activityUuid, double activityHeat, String versionId) {
        HotActivityPO hotActivity = new HotActivityPO();
        hotActivity.setActivityUuid(activityUuid);
        hotActivity.setActivityHeat(activityHeat);
        hotActivity.setIsActive(true);
        hotActivity.setVersionId(versionId);
        hotActivity.setIsCurrent(true);
        return hotActivity;
    }

    /**
     * 用于主动更新活动热度和状态,由 activityService.updateHeatAndStatus 调用
     */
    public void updateHotActivityByActivityId(String activityUuid, double heat) {
       HotActivityPO hotActivity = hotActivityMapper.selectByActivityUuid(activityUuid);
        if (Objects.isNull(hotActivity)) {
            log.error("活动不存在，无法更新热度表");
            return;
        }
        if (!hotActivity.getIsActive()) {
            log.error("活动已被取消或删除，无法更新热度表");
            return;
        }
        hotActivity.setActivityHeat(heat);
        hotActivity.setUpdatedAt(LocalDateTime.now());
        hotActivityMapper.update(hotActivity);
        redisUtil.zAdd(RedisConstant.HOT_ACTIVITY_LIST_KEY + hotActivity.getVersionId(),RedisConstant.HOT_ACTIVITY_LIST_VALUE + activityUuid, heat);
        hotActivityMapper.updateStatusByUuid(activityUuid, true);
    }

    /**
     * 根据ID删除活动数据
     */
    public void deleteHotActivityById(Long hotActivityId) {
        hotActivityMapper.deleteById(hotActivityId);
    }

    /**
     * 根据ID查询活动数据
     */
    public HotActivityPO getHotActivityById(Long hotActivityId) {
        return hotActivityMapper.selectById(hotActivityId);
    }

    /**
     * 查询所有活动数据
     */
    public List<HotActivityPO> getAllHotActivities() {
        return hotActivityMapper.selectAll();
    }

    /**
     * 基于 activity_uuid 更新活动状态
     */
    public void updateStatusByUuid(String activityUuid, Boolean isActive) {
        hotActivityMapper.updateStatusByUuid(activityUuid, isActive);
    }

    /**
     * 将 version_id 相同的某个批次的 is_current 都改为 false
     */
    public void batchUpdateIsCurrentToFalse(String versionId) {
        hotActivityMapper.batchUpdateIsCurrentToFalse(versionId);
    }
    /**
     * 事务 2：更新热度表和 Redis 缓存
     *
     * @param updatedActivities 更新后的活动列表
     */
    @Transactional
    public void updateHotActivitiesAndCache(List<ActivityPO> updatedActivities) {
        log.info("开始执行热度表与缓存更新任务, 时间: {}", LocalDateTime.now());
        // 获取当前最大版本号
        String maxVersionId = hotActivityMapper.getMaxVersionId();
        if (redisUtil.hasKey(RedisConstant.HOT_ACTIVITY_LIST_KEY + maxVersionId)) {
            log.warn("缓存和热度表当前版本号不一致，可能存在并发问题");
        }

        // 生成新的版本号
        String newVersionId = String.valueOf(Integer.parseInt(maxVersionId) + 1);
        // 对活动列表按热度排序
        List<ActivityPO> sortedByHeatList = updatedActivities.stream()
                .sorted(Comparator.comparing(ActivityPO::getActivityHeat).reversed())
                .toList();

        log.info("写入热度表时查询到的活动数量为：{}, 时间：{}, 新版本：{}", sortedByHeatList.size(), LocalDateTime.now(), newVersionId);

        for (ActivityPO activity : sortedByHeatList) {
            HotActivityPO hotActivity = initHotActivity(activity.getActivityUuid(), activity.getActivityHeat(), newVersionId);
            insertHotActivity(hotActivity); // 插入新热度表
            redisUtil.zAdd(RedisConstant.HOT_ACTIVITY_LIST_KEY + newVersionId, activity.getActivityUuid(), activity.getActivityHeat()); // 更新 Redis 缓存
        }

        // 更新热度表中的 is_current 字段
        batchUpdateIsCurrentToFalse(maxVersionId);
        // 更新当前版本号缓存
        redisUtil.set(RedisConstant.HOT_ACTIVITY_LIST_CURRENT_VERSION_KEY, newVersionId);
        // 移除旧版本的热度缓存
        redisUtil.zRemove(RedisConstant.HOT_ACTIVITY_LIST_KEY + maxVersionId);

        log.info("热度表与缓存更新任务执行完成, 时间: {}", LocalDateTime.now());
    }

}