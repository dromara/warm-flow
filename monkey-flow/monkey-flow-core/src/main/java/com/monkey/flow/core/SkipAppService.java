package com.monkey.flow.core;

import com.monkey.flow.core.service.IFlowSkipService;

/**
 * @author minliuhua
 * @description: 流程跳转对外提供
 * @date: 2023/3/30 15:24
 */
public interface SkipAppService {

    /**
     * 获取流程跳转服务
     * @return
     */
    public IFlowSkipService getService();

}
