package com.warm.flow.core.webService;

import com.warm.flow.core.service.IFlowTaskService;

import javax.annotation.Resource;

/**
 * @author minliuhua
 * @description: 任务历史记录对外提供
 * @date: 2023/3/30 15:24
 */
public class TaskAppService {
    @Resource
    private IFlowTaskService taskService;

    public IFlowTaskService getService(){
        return taskService;
    }

}
