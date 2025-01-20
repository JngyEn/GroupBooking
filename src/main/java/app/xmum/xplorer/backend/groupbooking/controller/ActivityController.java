package app.xmum.xplorer.backend.groupbooking.controller;

import app.xmum.xplorer.backend.groupbooking.pojo.ActivityPO;
import app.xmum.xplorer.backend.groupbooking.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/activities")
public class ActivityController {

    @Autowired
    private ActivityService activityService;

    @GetMapping("/{id}")
    public ActivityPO findById(@PathVariable Long id) {
        //hack: 判断时间先后顺序
        return activityService.findById(id);
    }

    @PostMapping
    public void insert(@RequestBody ActivityPO activityPO) {
        activityService.insert(activityPO);
    }

    @PutMapping
    public void update(@RequestBody ActivityPO activityPO) {
        activityService.update(activityPO);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        activityService.deleteById(id);
    }

    @GetMapping
    public List<ActivityPO> findAll() {
        return activityService.findAll();
    }
}