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
import org.dromara.warm.flow.core.entity.HisTask;
import org.dromara.warm.flow.core.entity.Instance;
import org.dromara.warm.flow.core.entity.Node;
import org.dromara.warm.flow.core.entity.RootEntity;
import org.dromara.warm.flow.core.entity.Skip;
import org.dromara.warm.flow.core.entity.Task;
import org.dromara.warm.flow.core.entity.User;
import org.dromara.warm.flow.core.enums.ActivityStatus;
import org.dromara.warm.flow.core.enums.FlowStatus;
import org.dromara.warm.flow.core.enums.NodeType;
import org.dromara.warm.flow.core.enums.PublishStatus;
import org.dromara.warm.flow.core.enums.SkipType;
import org.dromara.warm.flow.core.enums.UserType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Demo management console API for the deployable Jimmer/PostgreSQL runtime.
 *
 * <p>This controller intentionally stays self-contained so the demo module can
 * be deployed as a complete admin console without introducing a frontend build
 * toolchain or an application-specific identity system.</p>
 */
@RestController
@RequestMapping("/demo/admin/api")
public class DemoAdminController {

    private static final int DEFAULT_LIMIT = 200;

    @GetMapping("/overview")
    public ApiResult<Map<String, Object>> overview() {
        Map<String, Object> data = new LinkedHashMap<>();
        List<Definition> definitions = definitions();
        List<Instance> instances = instances();
        List<Task> tasks = tasks();
        List<HisTask> history = history();
        List<User> users = users();

        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("definitions", definitions.size());
        summary.put("publishedDefinitions", countDefinitionsByPublish(definitions, PublishStatus.PUBLISHED.getKey()));
        summary.put("activeDefinitions", countDefinitionsByActivity(definitions, ActivityStatus.ACTIVITY.getKey()));
        summary.put("instances", instances.size());
        summary.put("activeInstances", countInstancesByActivity(instances, ActivityStatus.ACTIVITY.getKey()));
        summary.put("finishedInstances", countInstancesByFlowStatus(instances, FlowStatus.FINISHED.getKey()));
        summary.put("tasks", tasks.size());
        summary.put("history", history.size());
        summary.put("permissionRows", users.size());
        data.put("summary", summary);

        Map<String, Object> identity = new LinkedHashMap<>();
        identity.put("mode", "anonymous-demo-admin");
        identity.put("handler", DemoSecurityConfig.DEFAULT_HANDLER);
        identity.put("permissions", Arrays.asList(DemoSecurityConfig.DEFAULT_HANDLER, DemoSecurityConfig.DEFAULT_PERMISSION));
        identity.put("note", "开发演示环境未启用登录；生产接入时应替换为业务系统认证与权限。 ");
        data.put("identity", identity);

        Map<String, Object> links = new LinkedHashMap<>();
        links.put("admin", "/admin/index.html");
        links.put("designer", "/warm-flow-ui/index.html");
        links.put("designerConfig", "/warm-flow-ui/config");
        links.put("seedDemo", "/demo/workflow/seed");
        links.put("startDemo", "/demo/workflow/start");
        links.put("e2eDemo", "/demo/workflow/e2e");
        data.put("links", links);

        Map<String, Object> recent = new LinkedHashMap<>();
        recent.put("definitions", mapAndLimit(definitions, 5, this::definitionRow));
        recent.put("instances", mapAndLimit(instances, 5, this::instanceRow));
        recent.put("tasks", mapAndLimit(tasks, 5, this::taskRow));
        recent.put("history", mapAndLimit(history, 5, this::historyRow));
        data.put("recent", recent);
        return ApiResult.ok(data);
    }

    @GetMapping("/definitions")
    public ApiResult<List<Map<String, Object>>> listDefinitions(@RequestParam(value = "limit", required = false) Integer limit) {
        return ApiResult.ok(mapAndLimit(definitions(), limit(limit), this::definitionRow));
    }

    @GetMapping("/definitions/{id}")
    public ApiResult<Map<String, Object>> definitionDetail(@PathVariable("id") Long id) {
        Definition definition = requiredDefinition(id);
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("definition", definitionRow(definition));
        data.put("nodes", mapAll(FlowEngine.nodeService().getByDefId(id), this::nodeRow));
        data.put("skips", mapAll(FlowEngine.skipService().getByDefId(id), this::skipRow));
        data.put("instances", mapAndLimit(FlowEngine.insService().getByDefId(id), 50, this::instanceRow));
        data.put("design", FlowEngine.defService().queryDesign(id));
        return ApiResult.ok(data);
    }

    @PostMapping("/definitions/{id}/publish")
    @Transactional(rollbackFor = Exception.class)
    public ApiResult<Map<String, Object>> publishDefinition(@PathVariable("id") Long id) {
        return actionResult("publishDefinition", FlowEngine.defService().publish(id));
    }

    @PostMapping("/definitions/{id}/unpublish")
    @Transactional(rollbackFor = Exception.class)
    public ApiResult<Map<String, Object>> unPublishDefinition(@PathVariable("id") Long id) {
        return actionResult("unPublishDefinition", FlowEngine.defService().unPublish(id));
    }

    @PostMapping("/definitions/{id}/active")
    @Transactional(rollbackFor = Exception.class)
    public ApiResult<Map<String, Object>> activeDefinition(@PathVariable("id") Long id) {
        return actionResult("activeDefinition", FlowEngine.defService().active(id));
    }

    @PostMapping("/definitions/{id}/suspend")
    @Transactional(rollbackFor = Exception.class)
    public ApiResult<Map<String, Object>> suspendDefinition(@PathVariable("id") Long id) {
        return actionResult("suspendDefinition", FlowEngine.defService().unActive(id));
    }

    @PostMapping("/definitions/{id}/copy")
    @Transactional(rollbackFor = Exception.class)
    public ApiResult<Map<String, Object>> copyDefinition(@PathVariable("id") Long id) {
        return actionResult("copyDefinition", FlowEngine.defService().copyDef(id));
    }

    @DeleteMapping("/definitions/{id}")
    @Transactional(rollbackFor = Exception.class)
    public ApiResult<Map<String, Object>> removeDefinition(@PathVariable("id") Long id) {
        return actionResult("removeDefinition", FlowEngine.defService().removeDef(Collections.singletonList(id)));
    }

    @GetMapping("/instances")
    public ApiResult<List<Map<String, Object>>> listInstances(@RequestParam(value = "limit", required = false) Integer limit) {
        return ApiResult.ok(mapAndLimit(instances(), limit(limit), this::instanceRow));
    }

    @GetMapping("/instances/{id}")
    public ApiResult<Map<String, Object>> instanceDetail(@PathVariable("id") Long id) {
        Instance instance = requiredInstance(id);
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("instance", instanceRow(instance));
        data.put("tasks", mapAll(FlowEngine.taskService().getByInsId(id), this::taskRow));
        data.put("history", mapAll(FlowEngine.hisTaskService().getByInsId(id), this::historyRow));
        return ApiResult.ok(data);
    }

    @PostMapping("/instances/start")
    @Transactional(rollbackFor = Exception.class)
    public ApiResult<Map<String, Object>> startInstance(@RequestBody(required = false) StartInstanceRequest request) {
        StartInstanceRequest body = request == null ? new StartInstanceRequest() : request;
        String flowCode = StringUtils.hasText(body.getFlowCode()) ? body.getFlowCode() : "demo_leave_jimmer";
        String businessId = StringUtils.hasText(body.getBusinessId())
            ? body.getBusinessId()
            : "admin-start-" + System.currentTimeMillis();
        String handler = StringUtils.hasText(body.getHandler()) ? body.getHandler() : DemoSecurityConfig.DEFAULT_HANDLER;
        List<String> permissionFlag = body.getPermissionFlag() == null || body.getPermissionFlag().isEmpty()
            ? Collections.singletonList(DemoSecurityConfig.DEFAULT_PERMISSION)
            : body.getPermissionFlag();
        Map<String, Object> variable = body.getVariable() == null ? new LinkedHashMap<String, Object>() : body.getVariable();

        Instance instance = FlowEngine.insService().start(businessId, FlowParams.build()
            .flowCode(flowCode)
            .handler(handler)
            .permissionFlag(permissionFlag)
            .variable(variable));
        return ApiResult.ok(instanceRow(instance));
    }

    @PostMapping("/instances/{id}/active")
    @Transactional(rollbackFor = Exception.class)
    public ApiResult<Map<String, Object>> activeInstance(@PathVariable("id") Long id) {
        return actionResult("activeInstance", FlowEngine.insService().active(id));
    }

    @PostMapping("/instances/{id}/suspend")
    @Transactional(rollbackFor = Exception.class)
    public ApiResult<Map<String, Object>> suspendInstance(@PathVariable("id") Long id) {
        return actionResult("suspendInstance", FlowEngine.insService().unActive(id));
    }

    @DeleteMapping("/instances/{id}")
    @Transactional(rollbackFor = Exception.class)
    public ApiResult<Map<String, Object>> removeInstance(@PathVariable("id") Long id) {
        return actionResult("removeInstance", FlowEngine.insService().remove(Collections.singletonList(id)));
    }

    @GetMapping("/tasks")
    public ApiResult<List<Map<String, Object>>> listTasks(@RequestParam(value = "limit", required = false) Integer limit) {
        return ApiResult.ok(mapAndLimit(tasks(), limit(limit), this::taskRow));
    }

    @GetMapping("/tasks/{id}")
    public ApiResult<Map<String, Object>> taskDetail(@PathVariable("id") Long id) {
        Task task = requiredTask(id);
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("task", taskRow(task));
        data.put("approvalUsers", usersForTask(id, UserType.APPROVAL.getKey()));
        data.put("transferUsers", usersForTask(id, UserType.TRANSFER.getKey()));
        data.put("deputeUsers", usersForTask(id, UserType.DEPUTE.getKey()));
        data.put("history", mapAll(FlowEngine.hisTaskService().listByTaskId(id), this::historyRow));
        return ApiResult.ok(data);
    }

    @PostMapping("/tasks/{id}/pass")
    @Transactional(rollbackFor = Exception.class)
    public ApiResult<Map<String, Object>> passTask(@PathVariable("id") Long id,
                                                   @RequestBody(required = false) TaskActionRequest request) {
        TaskActionRequest body = request == null ? new TaskActionRequest() : request;
        String message = StringUtils.hasText(body.getMessage()) ? body.getMessage() : "admin approved";
        Map<String, Object> variable = body.getVariable() == null ? new LinkedHashMap<String, Object>() : body.getVariable();
        return ApiResult.ok(instanceRow(FlowEngine.taskService().pass(id, message, variable)));
    }

    @PostMapping("/tasks/{id}/reject")
    @Transactional(rollbackFor = Exception.class)
    public ApiResult<Map<String, Object>> rejectTask(@PathVariable("id") Long id,
                                                     @RequestBody(required = false) TaskActionRequest request) {
        TaskActionRequest body = request == null ? new TaskActionRequest() : request;
        String message = StringUtils.hasText(body.getMessage()) ? body.getMessage() : "admin rejected";
        Map<String, Object> variable = body.getVariable() == null ? new LinkedHashMap<String, Object>() : body.getVariable();
        return ApiResult.ok(instanceRow(FlowEngine.taskService().reject(id, message, variable)));
    }

    @GetMapping("/history")
    public ApiResult<List<Map<String, Object>>> listHistory(@RequestParam(value = "limit", required = false) Integer limit) {
        return ApiResult.ok(mapAndLimit(history(), limit(limit), this::historyRow));
    }

    @GetMapping("/users")
    public ApiResult<List<Map<String, Object>>> listUsers(@RequestParam(value = "limit", required = false) Integer limit) {
        return ApiResult.ok(mapAndLimit(users(), limit(limit), this::userRow));
    }

    @GetMapping("/runtime")
    public ApiResult<Map<String, Object>> runtime() {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("database", "PostgreSQL");
        data.put("orm", "Jimmer");
        data.put("databaseValidationMode", "ERROR");
        data.put("adminUrl", "/admin/index.html");
        data.put("designerUrl", "/warm-flow-ui/index.html");
        data.put("anonymousDemoAdmin", true);
        data.put("defaultHandler", DemoSecurityConfig.DEFAULT_HANDLER);
        data.put("defaultPermission", DemoSecurityConfig.DEFAULT_PERMISSION);
        data.put("flowEngine", FlowEngine.getFlowConfig() == null ? null : FlowEngine.getFlowConfig().getFramework().name());
        return ApiResult.ok(data);
    }

    @ExceptionHandler(Exception.class)
    public ApiResult<Map<String, Object>> handleException(Exception exception) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("error", exception.getClass().getSimpleName());
        data.put("message", safeMessage(exception));
        return ApiResult.fail(data, safeMessage(exception));
    }

    private List<Definition> definitions() {
        return FlowEngine.defService().orderByCreateTime().desc().list(FlowEngine.newDef());
    }

    private List<Instance> instances() {
        return FlowEngine.insService().orderByCreateTime().desc().list(FlowEngine.newIns());
    }

    private List<Task> tasks() {
        return FlowEngine.taskService().orderByCreateTime().desc().list(FlowEngine.newTask());
    }

    private List<HisTask> history() {
        return FlowEngine.hisTaskService().orderByCreateTime().desc().list(FlowEngine.newHisTask());
    }

    private List<User> users() {
        return FlowEngine.userService().orderByCreateTime().desc().list(FlowEngine.newUser());
    }

    private Definition requiredDefinition(Long id) {
        Definition definition = FlowEngine.defService().getById(id);
        if (definition == null) {
            throw new IllegalArgumentException("流程定义不存在: " + id);
        }
        return definition;
    }

    private Instance requiredInstance(Long id) {
        Instance instance = FlowEngine.insService().getById(id);
        if (instance == null) {
            throw new IllegalArgumentException("流程实例不存在: " + id);
        }
        return instance;
    }

    private Task requiredTask(Long id) {
        Task task = FlowEngine.taskService().getById(id);
        if (task == null) {
            throw new IllegalArgumentException("待办任务不存在: " + id);
        }
        return task;
    }

    private Map<String, Object> definitionRow(Definition definition) {
        Map<String, Object> row = baseRow(definition);
        row.put("flowCode", definition.getFlowCode());
        row.put("flowName", definition.getFlowName());
        row.put("modelValue", definition.getModelValue());
        row.put("category", definition.getCategory());
        row.put("version", definition.getVersion());
        row.put("isPublish", definition.getIsPublish());
        row.put("publishLabel", nullDefault(PublishStatus.getValueByKey(definition.getIsPublish()), "未知"));
        row.put("activityStatus", definition.getActivityStatus());
        row.put("activityLabel", activityLabel(definition.getActivityStatus()));
        row.put("formCustom", definition.getFormCustom());
        row.put("formPath", definition.getFormPath());
        row.put("nodes", FlowEngine.nodeService().selectCount(FlowEngine.newNode().setDefinitionId(definition.getId())));
        row.put("skips", FlowEngine.skipService().selectCount(FlowEngine.newSkip().setDefinitionId(definition.getId())));
        row.put("instances", FlowEngine.insService().selectCount(FlowEngine.newIns().setDefinitionId(definition.getId())));
        row.put("designerUrl", "/warm-flow-ui/index.html?id=" + definition.getId());
        return row;
    }

    private Map<String, Object> instanceRow(Instance instance) {
        Map<String, Object> row = baseRow(instance);
        row.put("definitionId", instance.getDefinitionId());
        row.put("flowName", instance.getFlowName());
        row.put("businessId", instance.getBusinessId());
        row.put("nodeType", instance.getNodeType());
        row.put("nodeTypeLabel", nullDefault(NodeType.getValueByKey(instance.getNodeType()), "未知"));
        row.put("nodeCode", instance.getNodeCode());
        row.put("nodeName", instance.getNodeName());
        row.put("flowStatus", instance.getFlowStatus());
        row.put("flowStatusLabel", nullDefault(FlowStatus.getValueByKey(instance.getFlowStatus()), "未知"));
        row.put("activityStatus", instance.getActivityStatus());
        row.put("activityLabel", activityLabel(instance.getActivityStatus()));
        row.put("tasks", FlowEngine.taskService().selectCount(FlowEngine.newTask().setInstanceId(instance.getId())));
        row.put("history", FlowEngine.hisTaskService().selectCount(FlowEngine.newHisTask().setInstanceId(instance.getId())));
        row.put("chartUrl", "/warm-flow-ui/index.html?id=" + instance.getId() + "&type=FlowChart");
        return row;
    }

    private Map<String, Object> taskRow(Task task) {
        Map<String, Object> row = baseRow(task);
        row.put("definitionId", task.getDefinitionId());
        row.put("instanceId", task.getInstanceId());
        row.put("flowName", task.getFlowName());
        row.put("businessId", task.getBusinessId());
        row.put("nodeCode", task.getNodeCode());
        row.put("nodeName", task.getNodeName());
        row.put("nodeType", task.getNodeType());
        row.put("nodeTypeLabel", nullDefault(NodeType.getValueByKey(task.getNodeType()), "未知"));
        row.put("flowStatus", task.getFlowStatus());
        row.put("flowStatusLabel", nullDefault(FlowStatus.getValueByKey(task.getFlowStatus()), "未知"));
        row.put("permissionList", task.getPermissionList());
        row.put("approvalUsers", usersForTask(task.getId(), UserType.APPROVAL.getKey()));
        return row;
    }

    private Map<String, Object> historyRow(HisTask hisTask) {
        Map<String, Object> row = baseRow(hisTask);
        row.put("definitionId", hisTask.getDefinitionId());
        row.put("instanceId", hisTask.getInstanceId());
        row.put("taskId", hisTask.getTaskId());
        row.put("flowName", hisTask.getFlowName());
        row.put("businessId", hisTask.getBusinessId());
        row.put("nodeCode", hisTask.getNodeCode());
        row.put("nodeName", hisTask.getNodeName());
        row.put("nodeType", hisTask.getNodeType());
        row.put("nodeTypeLabel", nullDefault(NodeType.getValueByKey(hisTask.getNodeType()), "未知"));
        row.put("targetNodeCode", hisTask.getTargetNodeCode());
        row.put("targetNodeName", hisTask.getTargetNodeName());
        row.put("approver", hisTask.getApprover());
        row.put("collaborator", hisTask.getCollaborator());
        row.put("skipType", hisTask.getSkipType());
        row.put("skipTypeLabel", nullDefault(SkipType.getValueByKey(hisTask.getSkipType()), "无动作"));
        row.put("flowStatus", hisTask.getFlowStatus());
        row.put("flowStatusLabel", nullDefault(FlowStatus.getValueByKey(hisTask.getFlowStatus()), "未知"));
        row.put("message", hisTask.getMessage());
        return row;
    }

    private Map<String, Object> userRow(User user) {
        Map<String, Object> row = baseRow(user);
        row.put("associated", user.getAssociated());
        row.put("processedBy", user.getProcessedBy());
        row.put("type", user.getType());
        row.put("typeLabel", nullDefault(UserType.getValueByKey(user.getType()), "未知"));
        return row;
    }

    private Map<String, Object> nodeRow(Node node) {
        Map<String, Object> row = baseRow(node);
        row.put("definitionId", node.getDefinitionId());
        row.put("nodeCode", node.getNodeCode());
        row.put("nodeName", node.getNodeName());
        row.put("nodeType", node.getNodeType());
        row.put("nodeTypeLabel", nullDefault(NodeType.getValueByKey(node.getNodeType()), "未知"));
        row.put("permissionFlag", node.getPermissionFlag());
        row.put("coordinate", node.getCoordinate());
        row.put("formCustom", node.getFormCustom());
        row.put("formPath", node.getFormPath());
        return row;
    }

    private Map<String, Object> skipRow(Skip skip) {
        Map<String, Object> row = baseRow(skip);
        row.put("definitionId", skip.getDefinitionId());
        row.put("nodeId", skip.getNodeId());
        row.put("nowNodeCode", skip.getNowNodeCode());
        row.put("nowNodeType", skip.getNowNodeType());
        row.put("nextNodeCode", skip.getNextNodeCode());
        row.put("nextNodeType", skip.getNextNodeType());
        row.put("skipName", skip.getSkipName());
        row.put("skipType", skip.getSkipType());
        row.put("skipTypeLabel", nullDefault(SkipType.getValueByKey(skip.getSkipType()), "无动作"));
        row.put("skipCondition", skip.getSkipCondition());
        return row;
    }

    private Map<String, Object> baseRow(RootEntity entity) {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("id", entity.getId());
        row.put("createTime", format(entity.getCreateTime()));
        row.put("updateTime", format(entity.getUpdateTime()));
        row.put("createBy", entity.getCreateBy());
        row.put("updateBy", entity.getUpdateBy());
        row.put("tenantId", entity.getTenantId());
        row.put("delFlag", entity.getDelFlag());
        return row;
    }

    private List<Map<String, Object>> usersForTask(Long taskId, String type) {
        if (taskId == null) {
            return Collections.emptyList();
        }
        return mapAll(FlowEngine.userService().listByAssociatedAndTypes(taskId, type), this::userRow);
    }

    private ApiResult<Map<String, Object>> actionResult(String action, boolean success) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("action", action);
        data.put("success", success);
        data.put("overview", overview().getData().get("summary"));
        return success ? ApiResult.ok(data) : ApiResult.fail(data, "操作未生效");
    }

    private int countDefinitionsByPublish(List<Definition> rows, Integer publishStatus) {
        int count = 0;
        for (Definition row : rows) {
            if (publishStatus.equals(row.getIsPublish())) {
                count++;
            }
        }
        return count;
    }

    private int countDefinitionsByActivity(List<Definition> rows, Integer activityStatus) {
        int count = 0;
        for (Definition row : rows) {
            if (activityStatus.equals(row.getActivityStatus())) {
                count++;
            }
        }
        return count;
    }

    private int countInstancesByActivity(List<Instance> rows, Integer activityStatus) {
        int count = 0;
        for (Instance row : rows) {
            if (activityStatus.equals(row.getActivityStatus())) {
                count++;
            }
        }
        return count;
    }

    private int countInstancesByFlowStatus(List<Instance> rows, String flowStatus) {
        int count = 0;
        for (Instance row : rows) {
            if (flowStatus.equals(row.getFlowStatus())) {
                count++;
            }
        }
        return count;
    }

    private <T> List<Map<String, Object>> mapAll(List<T> rows, Function<T, Map<String, Object>> mapper) {
        return mapAndLimit(rows, rows == null ? 0 : rows.size(), mapper);
    }

    private <T> List<Map<String, Object>> mapAndLimit(List<T> rows, int limit, Function<T, Map<String, Object>> mapper) {
        if (rows == null || rows.isEmpty()) {
            return new ArrayList<>();
        }
        int max = Math.min(rows.size(), limit <= 0 ? DEFAULT_LIMIT : limit);
        List<Map<String, Object>> result = new ArrayList<>(max);
        for (int i = 0; i < max; i++) {
            result.add(mapper.apply(rows.get(i)));
        }
        return result;
    }

    private int limit(Integer limit) {
        if (limit == null || limit <= 0) {
            return DEFAULT_LIMIT;
        }
        return Math.min(limit, 1000);
    }

    private String activityLabel(Integer status) {
        if (ActivityStatus.ACTIVITY.getKey().equals(status)) {
            return ActivityStatus.ACTIVITY.getValue();
        }
        if (ActivityStatus.SUSPENDED.getKey().equals(status)) {
            return ActivityStatus.SUSPENDED.getValue();
        }
        return "未知";
    }

    private String format(Date date) {
        if (date == null) {
            return null;
        }
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
    }

    private String nullDefault(String value, String defaultValue) {
        return value == null ? defaultValue : value;
    }

    private String safeMessage(Exception exception) {
        String message = exception.getMessage();
        if (!StringUtils.hasText(message)) {
            message = exception.getClass().getSimpleName();
        }
        return message.replaceAll("(?i)(password|pwd|secret)=([^&\\s]+)", "$1=***");
    }

    public static class StartInstanceRequest {
        private String flowCode;
        private String businessId;
        private String handler;
        private List<String> permissionFlag;
        private Map<String, Object> variable;

        public String getFlowCode() {
            return flowCode;
        }

        public void setFlowCode(String flowCode) {
            this.flowCode = flowCode;
        }

        public String getBusinessId() {
            return businessId;
        }

        public void setBusinessId(String businessId) {
            this.businessId = businessId;
        }

        public String getHandler() {
            return handler;
        }

        public void setHandler(String handler) {
            this.handler = handler;
        }

        public List<String> getPermissionFlag() {
            return permissionFlag;
        }

        public void setPermissionFlag(List<String> permissionFlag) {
            this.permissionFlag = permissionFlag;
        }

        public Map<String, Object> getVariable() {
            return variable;
        }

        public void setVariable(Map<String, Object> variable) {
            this.variable = variable;
        }
    }

    public static class TaskActionRequest {
        private String message;
        private Map<String, Object> variable;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public Map<String, Object> getVariable() {
            return variable;
        }

        public void setVariable(Map<String, Object> variable) {
            this.variable = variable;
        }
    }
}
