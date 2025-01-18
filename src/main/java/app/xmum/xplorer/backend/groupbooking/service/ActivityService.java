package app.xmum.xplorer.backend.groupbooking.service;

import app.xmum.xplorer.backend.groupbooking.mapper.ActivityMapper;
import app.xmum.xplorer.backend.groupbooking.pojo.ActivityPO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActivityService {

    @Autowired
    private ActivityMapper activityMapper;

    public ActivityPO findById(Long id) {
        return activityMapper.findById(id);
    }

    public void insert(ActivityPO activityPO) {
        activityMapper.insert(activityPO);
    }

    public void update(ActivityPO activityPO) {
        activityMapper.update(activityPO);
    }

    public void deleteById(Long id) {
        activityMapper.deleteById(id);
    }

    public List<ActivityPO> findAll() {
        return activityMapper.findAll();
    }
}