package com.monkey.flow.core;

import com.monkey.flow.core.service.IFlowNodeService;

/**
 * @author minliuhua
 * @description: 任务历史记录对外提供
 * @date: 2023/3/30 15:24
 */
public interface NodeAppService {

    /**
     * 获取流程结点服务
     * @return
     */
    public IFlowNodeService getService();
}
