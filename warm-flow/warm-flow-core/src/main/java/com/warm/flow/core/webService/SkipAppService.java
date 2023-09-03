package com.warm.flow.core.webService;

import com.warm.flow.core.service.IFlowSkipService;

import javax.annotation.Resource;

/**
 * @author minliuhua
 * @description: 流程跳转对外提供
 * @date: 2023/3/30 15:24
 */
public class SkipAppService {

    @Resource
    private IFlowSkipService skipService;

    public IFlowSkipService getService() {
        return skipService;
    }

}
