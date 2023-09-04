package com.warm.flow.core;

import com.warm.flow.core.service.*;

/**
 * 流程定义工程
 *
 * @author warm
 */
public class FlowFactory {


    public static FlowFactory flowFactory = null;
    private final IFlowDefinitionService defService;
    private final IFlowHisTaskService hisTaskService;
    private final IFlowInstanceService insService;
    private final IFlowNodeService nodeService;

    private final IFlowSkipService skipService;

    private final IFlowTaskService taskService;

    public FlowFactory(IFlowDefinitionService defService, IFlowHisTaskService hisTaskService
            , IFlowInstanceService insService, IFlowNodeService nodeService
            , IFlowSkipService skipService, IFlowTaskService taskService) {
        this.defService = defService;
        this.hisTaskService = hisTaskService;
        this.insService = insService;
        this.nodeService = nodeService;
        this.skipService = skipService;
        this.taskService = taskService;
    }

    public IFlowDefinitionService defService() {
        return defService;
    }

    public IFlowHisTaskService hisTaskService() {
        return hisTaskService;
    }

    public IFlowInstanceService insService() {
        return insService;
    }

    public IFlowNodeService nodeService() {
        return nodeService;
    }

    public IFlowSkipService skipService() {
        return skipService;
    }

    public IFlowTaskService taskService() {
        return taskService;
    }

}
