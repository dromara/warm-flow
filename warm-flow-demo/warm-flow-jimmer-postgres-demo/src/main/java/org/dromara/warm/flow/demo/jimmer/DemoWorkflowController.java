/*
 *    Copyright 2024-2025, Warm-Flow (290631660@qq.com).
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 */
package org.dromara.warm.flow.demo.jimmer;

import org.dromara.warm.flow.core.FlowEngine;
import org.dromara.warm.flow.core.dto.ApiResult;
import org.dromara.warm.flow.core.dto.FlowParams;
import org.dromara.warm.flow.core.entity.Definition;
import org.dromara.warm.flow.core.entity.Instance;
import org.dromara.warm.flow.core.entity.Node;
import org.dromara.warm.flow.core.entity.Task;
import org.dromara.warm.flow.core.enums.ActivityStatus;
import org.dromara.warm.flow.core.enums.FlowStatus;
import org.dromara.warm.flow.core.enums.NodeType;
import org.dromara.warm.flow.core.enums.PublishStatus;
import org.dromara.warm.flow.core.enums.SkipType;
import org.dromara.warm.flow.core.enums.UserType;
import org.dromara.warm.flow.orm.entity.FlowDefinition;
import org.dromara.warm.flow.orm.entity.FlowNode;
import org.dromara.warm.flow.orm.entity.FlowSkip;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
public class DemoWorkflowController {

    private static final Long DEFINITION_ID = 920000000001000001L;
    private static final Long START_NODE_ID = 920000000001000002L;
    private static final Long APPROVE_NODE_ID = 920000000001000003L;
    private static final Long END_NODE_ID = 920000000001000004L;
    private static final Long START_SKIP_ID = 920000000001000005L;
    private static final Long APPROVE_SKIP_ID = 920000000001000006L;
    private static final String FLOW_CODE = "demo_leave_jimmer";

    @PostMapping("/demo/workflow/seed")
    @Transactional(rollbackFor = Exception.class)
    public ApiResult<Map<String, Object>> seed() {
        ensureDefinition();
        return ApiResult.ok(summary("seeded", null, null));
    }

    @PostMapping("/demo/workflow/start")
    @Transactional(rollbackFor = Exception.class)
    public ApiResult<Map<String, Object>> start() {
        ensureDefinition();
        Map<String, Object> variables = new HashMap<>();
        variables.put("days", 3);
        Instance instance = FlowEngine.insService().start("biz-demo-" + System.currentTimeMillis(), FlowParams.build()
            .flowCode(FLOW_CODE)
            .handler(DemoSecurityConfig.DEFAULT_HANDLER)
            .permissionFlag(Collections.singletonList(DemoSecurityConfig.DEFAULT_PERMISSION))
            .variable(variables));
        List<Task> tasks = FlowEngine.taskService().list(FlowEngine.newTask().setInstanceId(instance.getId()));
        Long taskId = tasks.isEmpty() ? null : tasks.get(0).getId();
        return ApiResult.ok(summary("started", instance, taskId));
    }

    @PostMapping("/demo/workflow/e2e")
    @Transactional(rollbackFor = Exception.class)
    public ApiResult<Map<String, Object>> e2e() {
        ensureDefinition();
        Map<String, Object> variables = new HashMap<>();
        variables.put("days", 3);
        Instance instance = FlowEngine.insService().start("biz-demo-e2e-" + System.currentTimeMillis(), FlowParams.build()
            .flowCode(FLOW_CODE)
            .handler(DemoSecurityConfig.DEFAULT_HANDLER)
            .permissionFlag(Collections.singletonList(DemoSecurityConfig.DEFAULT_PERMISSION))
            .variable(variables));
        List<Task> tasks = FlowEngine.taskService().list(FlowEngine.newTask().setInstanceId(instance.getId()));
        if (tasks.isEmpty()) {
            return ApiResult.fail(summary("started_without_task", instance, null), "未生成审批任务");
        }
        Long taskId = tasks.get(0).getId();
        Map<String, Object> passVariables = new HashMap<>();
        passVariables.put("approved", true);
        Instance finished = FlowEngine.taskService().pass(taskId, "demo approved", passVariables);
        return ApiResult.ok(summary("finished", finished, taskId));
    }

    @GetMapping("/demo/workflow/status")
    public ApiResult<Map<String, Object>> status() {
        Definition definition = FlowEngine.defService().getById(DEFINITION_ID);
        return ApiResult.ok(summary(definition == null ? "empty" : "ready", null, null));
    }

    private void ensureDefinition() {
        Definition existing = FlowEngine.defService().getById(DEFINITION_ID);
        if (existing != null) {
            return;
        }
        FlowDefinition definition = new FlowDefinition()
            .setId(DEFINITION_ID)
            .setFlowCode(FLOW_CODE)
            .setFlowName("Jimmer PostgreSQL 演示请假")
            .setModelValue("CLASSICS")
            .setIsPublish(PublishStatus.PUBLISHED.getKey())
            .setActivityStatus(ActivityStatus.ACTIVITY.getKey())
            .setFormCustom("N")
            .setDelFlag("0");
        FlowNode start = new FlowNode()
            .setId(START_NODE_ID)
            .setDefinitionId(DEFINITION_ID)
            .setNodeType(NodeType.START.getKey())
            .setNodeCode("start")
            .setNodeName("开始")
            .setNodeRatio("0")
            .setDelFlag("0");
        FlowNode approve = new FlowNode()
            .setId(APPROVE_NODE_ID)
            .setDefinitionId(DEFINITION_ID)
            .setNodeType(NodeType.BETWEEN.getKey())
            .setNodeCode("approve")
            .setNodeName("审批")
            .setNodeRatio("0")
            .setPermissionFlag(DemoSecurityConfig.DEFAULT_PERMISSION)
            .setDelFlag("0");
        FlowNode end = new FlowNode()
            .setId(END_NODE_ID)
            .setDefinitionId(DEFINITION_ID)
            .setNodeType(NodeType.END.getKey())
            .setNodeCode("end")
            .setNodeName("结束")
            .setNodeRatio("0")
            .setDelFlag("0");
        FlowSkip startToApprove = new FlowSkip()
            .setId(START_SKIP_ID)
            .setDefinitionId(DEFINITION_ID)
            .setNodeId(START_NODE_ID)
            .setNowNodeCode("start")
            .setNowNodeType(NodeType.START.getKey())
            .setNextNodeCode("approve")
            .setNextNodeType(NodeType.BETWEEN.getKey())
            .setSkipType(SkipType.PASS.getKey())
            .setDelFlag("0");
        FlowSkip approveToEnd = new FlowSkip()
            .setId(APPROVE_SKIP_ID)
            .setDefinitionId(DEFINITION_ID)
            .setNodeId(APPROVE_NODE_ID)
            .setNowNodeCode("approve")
            .setNowNodeType(NodeType.BETWEEN.getKey())
            .setNextNodeCode("end")
            .setNextNodeType(NodeType.END.getKey())
            .setSkipType(SkipType.PASS.getKey())
            .setDelFlag("0");
        FlowEngine.defService().insertFlow(definition,
            Arrays.<Node>asList(start, approve, end),
            Arrays.asList(startToApprove, approveToEnd));
        FlowEngine.defService().publish(DEFINITION_ID);
    }

    private Map<String, Object> summary(String state, Instance instance, Long taskId) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("state", state);
        data.put("flowCode", FLOW_CODE);
        data.put("definitionId", DEFINITION_ID);
        data.put("definitionExists", FlowEngine.defService().getById(DEFINITION_ID) != null);
        if (instance != null) {
            data.put("instanceId", instance.getId());
            data.put("businessId", instance.getBusinessId());
            data.put("nodeCode", instance.getNodeCode());
            data.put("nodeType", instance.getNodeType());
            data.put("flowStatus", instance.getFlowStatus());
            data.put("finished", FlowStatus.FINISHED.getKey().equals(instance.getFlowStatus()));
        }
        data.put("taskId", taskId);
        if (taskId != null) {
            data.put("remainingApprovalUsers", FlowEngine.userService()
                .listByAssociatedAndTypes(taskId, UserType.APPROVAL.getKey()).size());
        }
        data.put("activeTasks", FlowEngine.taskService().selectCount(FlowEngine.newTask()));
        return data;
    }
}
