package com.warm.flow.core.webService;

import com.warm.flow.core.service.IFlowHisTaskService;

import javax.annotation.Resource;

/**
 * @author minliuhua
 * @description: 任务历史记录对外提供
 * @date: 2023/3/30 15:24
 */
public class HisTaskAppService {
    @Resource
    private IFlowHisTaskService hisTaskService;

    public IFlowHisTaskService getService() {
        return hisTaskService;
    }

}
