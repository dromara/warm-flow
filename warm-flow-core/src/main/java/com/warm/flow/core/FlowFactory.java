package com.warm.flow.core;

import com.warm.flow.core.service.*;

/**
 * 流程定义工程
 *
 * @author warm
 */
public class FlowFactory {


    private static IFlowDefinitionService defService = null;
    private static IFlowHisTaskService hisTaskService = null;
    private static IFlowInstanceService insService = null;
    private static IFlowNodeService nodeService = null;
    private static IFlowSkipService skipService = null;
    private static IFlowTaskService taskService = null;

    public FlowFactory(IFlowDefinitionService defService, IFlowHisTaskService hisTaskService
            , IFlowInstanceService insService, IFlowNodeService nodeService
            , IFlowSkipService skipService, IFlowTaskService taskService) {
        FlowFactory.defService = defService;
        FlowFactory.hisTaskService = hisTaskService;
        FlowFactory.insService = insService;
        FlowFactory.nodeService = nodeService;
        FlowFactory.skipService = skipService;
        FlowFactory.taskService = taskService;
    }


    public static IFlowDefinitionService defService() {
        return defService;
    }

    public static IFlowHisTaskService hisTaskService() {
        return hisTaskService;
    }

    public static IFlowInstanceService insService() {
        return insService;
    }

    public static IFlowNodeService nodeService() {
        return nodeService;
    }

    public static IFlowSkipService skipService() {
        return skipService;
    }

    public static IFlowTaskService taskService() {
        return taskService;
    }

}
