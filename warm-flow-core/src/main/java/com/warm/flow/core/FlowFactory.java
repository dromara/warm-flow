package com.warm.flow.core;

import com.warm.flow.core.service.*;

/**
 * 流程定义工程
 *
 * @author warm
 */
public class FlowFactory {


    private static DefService defService = null;
    private static HisTaskService hisTaskService = null;
    private static InsService insService = null;
    private static NodeService nodeService = null;
    private static SkipService skipService = null;
    private static TaskService taskService = null;

    public FlowFactory(DefService defService, HisTaskService hisTaskService
            , InsService insService, NodeService nodeService
            , SkipService skipService, TaskService taskService) {
        FlowFactory.defService = defService;
        FlowFactory.hisTaskService = hisTaskService;
        FlowFactory.insService = insService;
        FlowFactory.nodeService = nodeService;
        FlowFactory.skipService = skipService;
        FlowFactory.taskService = taskService;
    }


    public static DefService defService() {
        return defService;
    }

    public static HisTaskService hisTaskService() {
        return hisTaskService;
    }

    public static InsService insService() {
        return insService;
    }

    public static NodeService nodeService() {
        return nodeService;
    }

    public static SkipService skipService() {
        return skipService;
    }

    public static TaskService taskService() {
        return taskService;
    }

}
