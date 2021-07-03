package com.hlj.quartz.controller;


import com.hlj.quartz.quartz.service.ScheduleTriggerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @Description
 * @Author HealerJean
 * @Date 2018/3/22  下午4:47.
 */
@RestController
public class QuartzController {


    @Autowired
    private ScheduleTriggerService scheduleTriggerService;


    @GetMapping("quartzStart")
    public String startNNoQuartz(){
        scheduleTriggerService.refreshTrigger();

        return "定时器任务开始执行，请注意观察控制台";
    }




}
