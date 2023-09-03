package com.warm.flow.core.webService;

import com.warm.flow.core.service.IFlowNodeService;

import javax.annotation.Resource;

/**
 * @author minliuhua
 * @description: 任务历史记录对外提供
 * @date: 2023/3/30 15:24
 */
public class NodeAppService {

    @Resource
    private IFlowNodeService flowNodeService;
    public IFlowNodeService getService(){
        return flowNodeService;
    }
}
