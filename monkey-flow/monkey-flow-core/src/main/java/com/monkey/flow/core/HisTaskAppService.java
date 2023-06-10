package com.monkey.flow.core;

import com.monkey.flow.core.service.IFlowHisTaskService;

/**
 * @author minliuhua
 * @description: 任务历史记录对外提供
 * @date: 2023/3/30 15:24
 */
public interface HisTaskAppService {

    /**
     * 获取任务历史记录服务
     * @return
     */
    public IFlowHisTaskService getService();

}
