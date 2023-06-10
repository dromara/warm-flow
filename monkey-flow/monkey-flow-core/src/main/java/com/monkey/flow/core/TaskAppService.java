package com.monkey.flow.core;

import com.monkey.flow.core.service.IFlowTaskService;

/**
 * @author minliuhua
 * @description: 待办任务对外提供
 * @date: 2023/3/30 15:24
 */
public interface TaskAppService {

    /**
     * 获取任务历史记录服务
     * @return
     */
    public IFlowTaskService getService();

}
