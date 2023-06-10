package com.monkey.flow.core.webService;

import com.monkey.flow.core.TaskAppService;
import com.monkey.flow.core.service.IFlowTaskService;

import javax.annotation.Resource;

/**
 * @author minliuhua
 * @description: 任务历史记录对外提供
 * @date: 2023/3/30 15:24
 */
public class TaskAppServiceImpl implements TaskAppService {
    @Resource
    private IFlowTaskService taskService;

    @Override
    public IFlowTaskService getService(){
        return taskService;
    }

}
