package app.xmum.xplorer.backend.groupbooking.scheduler;

import app.xmum.xplorer.backend.groupbooking.config.RedisConstant;
import app.xmum.xplorer.backend.groupbooking.exception.ActivityException;
import app.xmum.xplorer.backend.groupbooking.mapper.HotActivityMapper;
import app.xmum.xplorer.backend.groupbooking.pojo.ActivityPO;
import app.xmum.xplorer.backend.groupbooking.service.ActivityService;
import app.xmum.xplorer.backend.groupbooking.service.HotActivityService;
import app.xmum.xplorer.backend.groupbooking.utils.RedisUtil;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
public class ActivityScheduler {

    @Autowired
    private ActivityService activityService;

    @Autowired
    private HotActivityService hotActivityService;


    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private HotActivityMapper hotActivityMapper;

    /**
     * 定时任务：每日更新活动状态、访问量、热度，并更新热度表和 Redis 缓存
     */
    @XxlJob("activityDailyUpdate")
    public void dailyUpdatesHandler() throws Exception {
        log.info("开始活动定时更新任务, 时间: {}", LocalDateTime.now());

        try {

            // 事务 1：更新活动状态、访问量、热度，并返回更新后的活动列表
            List<ActivityPO> updatedActivities = activityService.updateActivitiesStatusAndHeat();

            // 事务 2：使用更新后的活动列表更新热度表和 Redis 缓存
            hotActivityService.updateHotActivitiesAndCache(updatedActivities);

            log.info("活动定时更新任务执行完成, 时间: {}", LocalDateTime.now());
        } catch (NumberFormatException error) {
            log.error("活动状态更新任务时，Redis 记录数据格式错误", error);
            throw new ActivityException(error.getMessage()); // 抛出异常，让 XXL-JOB 捕获并记录失败
        } catch (Exception e) {
            log.error("活动状态更新任务执行失败", e);
            throw e; // 抛出异常，让 XXL-JOB 捕获并记录失败
        }
    }

}