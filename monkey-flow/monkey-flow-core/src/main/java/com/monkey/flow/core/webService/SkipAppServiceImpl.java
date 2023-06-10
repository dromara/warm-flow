package com.monkey.flow.core.webService;

import com.monkey.flow.core.SkipAppService;
import com.monkey.flow.core.service.IFlowSkipService;

import javax.annotation.Resource;

/**
 * @author minliuhua
 * @description: 流程跳转对外提供
 * @date: 2023/3/30 15:24
 */
public class SkipAppServiceImpl implements SkipAppService {

    @Resource
    private IFlowSkipService skipService;

    @Override
    public IFlowSkipService getService() {
        return skipService;
    }

}
