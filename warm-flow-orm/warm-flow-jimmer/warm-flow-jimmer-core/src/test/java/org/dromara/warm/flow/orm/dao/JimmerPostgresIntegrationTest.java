/*
 *    Copyright 2024-2025, Warm-Flow (290631660@qq.com).
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 */
package org.dromara.warm.flow.orm.dao;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.babyfish.jimmer.sql.JSqlClient;
import org.babyfish.jimmer.sql.dialect.PostgresDialect;
import org.babyfish.jimmer.sql.runtime.ConnectionManager;
import org.dromara.warm.flow.core.FlowEngine;
import org.dromara.warm.flow.core.config.WarmFlow;
import org.dromara.warm.flow.core.dto.FlowParams;
import org.dromara.warm.flow.core.entity.Instance;
import org.dromara.warm.flow.core.entity.Task;
import org.dromara.warm.flow.core.enums.ActivityStatus;
import org.dromara.warm.flow.core.enums.FlowStatus;
import org.dromara.warm.flow.core.enums.NodeType;
import org.dromara.warm.flow.core.enums.PublishStatus;
import org.dromara.warm.flow.core.enums.SkipType;
import org.dromara.warm.flow.core.enums.UserType;
import org.dromara.warm.flow.core.handler.DataFillHandler;
import org.dromara.warm.flow.core.handler.PermissionHandler;
import org.dromara.warm.flow.core.handler.TenantHandler;
import org.dromara.warm.flow.core.invoker.FrameInvoker;
import org.dromara.warm.flow.core.json.JsonConvert;
import org.dromara.warm.flow.core.service.ChartService;
import org.dromara.warm.flow.core.service.DefService;
import org.dromara.warm.flow.core.service.FormService;
import org.dromara.warm.flow.core.service.HisTaskService;
import org.dromara.warm.flow.core.service.InsService;
import org.dromara.warm.flow.core.service.NodeService;
import org.dromara.warm.flow.core.service.SkipService;
import org.dromara.warm.flow.core.service.TaskService;
import org.dromara.warm.flow.core.service.UserService;
import org.dromara.warm.flow.core.service.impl.ChartServiceImpl;
import org.dromara.warm.flow.core.service.impl.DefServiceImpl;
import org.dromara.warm.flow.core.service.impl.FormServiceImpl;
import org.dromara.warm.flow.core.service.impl.HisTaskServiceImpl;
import org.dromara.warm.flow.core.service.impl.InsServiceImpl;
import org.dromara.warm.flow.core.service.impl.NodeServiceImpl;
import org.dromara.warm.flow.core.service.impl.SkipServiceImpl;
import org.dromara.warm.flow.core.service.impl.TaskServiceImpl;
import org.dromara.warm.flow.core.service.impl.UserServiceImpl;
import org.dromara.warm.flow.orm.entity.FlowDefinition;
import org.dromara.warm.flow.orm.entity.FlowForm;
import org.dromara.warm.flow.orm.entity.FlowHisTask;
import org.dromara.warm.flow.orm.entity.FlowInstance;
import org.dromara.warm.flow.orm.entity.FlowNode;
import org.dromara.warm.flow.orm.entity.FlowSkip;
import org.dromara.warm.flow.orm.entity.FlowTask;
import org.dromara.warm.flow.orm.entity.FlowUser;
import org.dromara.warm.flow.orm.utils.JimmerClients;
import org.junit.After;
import org.junit.Assume;
import org.junit.Test;
import org.postgresql.ds.PGSimpleDataSource;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static org.junit.Assert.*;

public class JimmerPostgresIntegrationTest {

    private String schema;

    @After
    public void cleanUp() throws Exception {
        try {
            JimmerClients.setSqlClient(null);
            resetFlowEngine();
            if (schema != null && hasPostgresCredentials()) {
                try (Connection connection = DriverManager.getConnection(postgresUrl(), postgresUser(), postgresPassword());
                     Statement statement = connection.createStatement()) {
                    statement.execute("drop schema if exists " + schema + " cascade");
                }
            }
        } finally {
            schema = null;
        }
    }

    @Test
    public void executesCrudAgainstPostgresSchema() throws Exception {
        Assume.assumeTrue("Set -Dwarm.jimmer.it.postgres.url to run PostgreSQL integration smoke",
            hasPostgresCredentials());

        schema = "warm_flow_jimmer_it_" + System.currentTimeMillis();
        createSchema(schema);
        JimmerClients.setSqlClient(postgresClient(schema));

        FlowDefinitionDaoImpl dao = new FlowDefinitionDaoImpl();
        FlowDefinition definition = new FlowDefinition()
            .setId(910000000000000001L)
            .setFlowCode("it_leave")
            .setFlowName("Integration Leave")
            .setModelValue("CLASSICS")
            .setVersion("1.0")
            .setIsPublish(0)
            .setActivityStatus(1)
            .setDelFlag("0")
            .setTenantId("it");

        assertEquals(1, dao.save(definition));

        FlowDefinition selected = dao.selectById(definition.getId());
        assertNotNull(selected);
        assertEquals("it_leave", selected.getFlowCode());
        assertEquals(Integer.valueOf(0), selected.getIsPublish());

        dao.updatePublishStatus(Collections.singletonList(definition.getId()), 1);
        FlowDefinition published = dao.selectById(definition.getId());
        assertEquals(Integer.valueOf(1), published.getIsPublish());

        List<FlowDefinition> byCode = dao.queryByCodeList(Collections.singletonList("it_leave"));
        assertEquals(1, byCode.size());
        assertEquals(definition.getId(), byCode.get(0).getId());

        assertEquals(1, dao.deleteById(definition.getId()));
        assertNull(dao.selectById(definition.getId()));
    }

    @Test
    public void customDaoMethodsRespectTenantAndLogicalDeleteScope() throws Exception {
        Assume.assumeTrue("Set -Dwarm.jimmer.it.postgres.url to run PostgreSQL integration smoke",
            hasPostgresCredentials());

        schema = "warm_flow_jimmer_scope_it_" + System.currentTimeMillis();
        createSchema(schema);
        JimmerClients.setSqlClient(postgresClient(schema));
        setWarmFlow(true, "tenant-a");

        FlowDefinitionDaoImpl definitionDao = new FlowDefinitionDaoImpl();
        FlowTaskDaoImpl taskDao = new FlowTaskDaoImpl();

        FlowDefinition tenantA = new FlowDefinition()
            .setId(910000000000000101L)
            .setFlowCode("scoped_flow")
            .setFlowName("Scoped A")
            .setModelValue("CLASSICS")
            .setVersion("1.0")
            .setIsPublish(0)
            .setActivityStatus(1);
        FlowDefinition tenantB = new FlowDefinition()
            .setId(910000000000000102L)
            .setFlowCode("scoped_flow")
            .setFlowName("Scoped B")
            .setModelValue("CLASSICS")
            .setVersion("1.0")
            .setIsPublish(0)
            .setActivityStatus(1)
            .setTenantId("tenant-b")
            .setDelFlag("0");

        definitionDao.save(tenantA);
        temporarilyWithoutTenant(() -> definitionDao.save(tenantB));

        List<FlowDefinition> scoped = definitionDao.queryByCodeList(Collections.singletonList("scoped_flow"));
        assertEquals(1, scoped.size());
        assertEquals(tenantA.getId(), scoped.get(0).getId());

        definitionDao.updatePublishStatus(Arrays.asList(tenantA.getId(), tenantB.getId()), 1);
        assertEquals(Integer.valueOf(1), definitionDao.selectById(tenantA.getId()).getIsPublish());
        assertNull(definitionDao.selectById(tenantB.getId()));
        temporarilyWithoutTenant(() -> assertEquals(Integer.valueOf(0), definitionDao.selectById(tenantB.getId()).getIsPublish()));

        FlowTask task = new FlowTask()
            .setId(910000000000000201L)
            .setDefinitionId(tenantA.getId())
            .setInstanceId(910000000000000301L)
            .setNodeCode("approve")
            .setNodeName("Approve")
            .setNodeType(1)
            .setFlowStatus("1");
        taskDao.save(task);
        assertEquals(1, taskDao.deleteByInsIds(Collections.singletonList(task.getInstanceId())));
        assertNull(taskDao.getByInsIdAndNodeCodes(task.getInstanceId(), Collections.singletonList("approve")).stream()
            .filter(row -> task.getId().equals(row.getId()))
            .findFirst()
            .orElse(null));
        temporarilyWithoutLogicDelete(() -> assertEquals("2", taskDao.selectById(task.getId()).getDelFlag()));
    }


    @Test
    public void physicalDeleteByIdMethodsRespectTenantScope() throws Exception {
        Assume.assumeTrue("Set -Dwarm.jimmer.it.postgres.url to run PostgreSQL integration smoke",
            hasPostgresCredentials());

        schema = "warm_flow_jimmer_physical_it_" + System.currentTimeMillis();
        createSchema(schema);
        JimmerClients.setSqlClient(postgresClient(schema));
        setWarmFlow(false, "tenant-a");

        FlowDefinitionDaoImpl dao = new FlowDefinitionDaoImpl();
        FlowDefinition tenantA = new FlowDefinition()
            .setId(910000000000000501L)
            .setFlowCode("physical_scope_a")
            .setFlowName("Physical A")
            .setModelValue("CLASSICS")
            .setVersion("1.0")
            .setIsPublish(0)
            .setActivityStatus(1);
        FlowDefinition tenantB = new FlowDefinition()
            .setId(910000000000000502L)
            .setFlowCode("physical_scope_b")
            .setFlowName("Physical B")
            .setModelValue("CLASSICS")
            .setVersion("1.0")
            .setIsPublish(0)
            .setActivityStatus(1)
            .setTenantId("tenant-b");

        dao.save(tenantA);
        temporarilyWithoutTenant(() -> dao.save(tenantB));

        assertEquals(0, dao.deleteById(tenantB.getId()));
        temporarilyWithoutTenant(() -> assertNotNull(dao.selectById(tenantB.getId())));

        assertEquals(1, dao.deleteByIds(Arrays.asList(tenantA.getId(), tenantB.getId())));
        assertNull(dao.selectById(tenantA.getId()));
        temporarilyWithoutTenant(() -> assertNotNull(dao.selectById(tenantB.getId())));
    }


    @Test
    public void updateByIdRespectsTenantAndLogicalDeleteScope() throws Exception {
        Assume.assumeTrue("Set -Dwarm.jimmer.it.postgres.url to run PostgreSQL integration smoke",
            hasPostgresCredentials());

        schema = "warm_flow_jimmer_update_it_" + System.currentTimeMillis();
        createSchema(schema);
        JimmerClients.setSqlClient(postgresClient(schema));
        setWarmFlow(true, "tenant-a");

        FlowDefinitionDaoImpl dao = new FlowDefinitionDaoImpl();
        FlowDefinition tenantA = new FlowDefinition()
            .setId(910000000000000601L)
            .setFlowCode("update_scope_a")
            .setFlowName("Update A")
            .setModelValue("CLASSICS")
            .setVersion("1.0")
            .setIsPublish(0)
            .setActivityStatus(1);
        FlowDefinition tenantB = new FlowDefinition()
            .setId(910000000000000602L)
            .setFlowCode("update_scope_b")
            .setFlowName("Update B")
            .setModelValue("CLASSICS")
            .setVersion("1.0")
            .setIsPublish(0)
            .setActivityStatus(1)
            .setTenantId("tenant-b")
            .setDelFlag("0");

        dao.save(tenantA);
        temporarilyWithoutTenant(() -> dao.save(tenantB));

        assertEquals(0, dao.updateById(new FlowDefinition()
            .setId(tenantB.getId())
            .setFlowName("Cross tenant update")
            .setIsPublish(1)));
        temporarilyWithoutTenant(() -> assertEquals("Update B", dao.selectById(tenantB.getId()).getFlowName()));

        assertEquals(1, dao.updateById(new FlowDefinition()
            .setId(tenantA.getId())
            .setFlowName("Scoped update")
            .setTenantId("malicious-tenant")
            .setDelFlag("9")));
        FlowDefinition updated = dao.selectById(tenantA.getId());
        assertEquals("Scoped update", updated.getFlowName());
        assertEquals("tenant-a", updated.getTenantId());
        assertEquals("0", updated.getDelFlag());

        assertEquals(1, dao.deleteById(tenantA.getId()));
        assertEquals(0, dao.updateById(new FlowDefinition()
            .setId(tenantA.getId())
            .setFlowName("Deleted row update")));
        temporarilyWithoutLogicDelete(() -> assertEquals("Scoped update", dao.selectById(tenantA.getId()).getFlowName()));
    }

    @Test
    public void skipDaoPersistsNodeIdAgainstPostgresSchema() throws Exception {
        Assume.assumeTrue("Set -Dwarm.jimmer.it.postgres.url to run PostgreSQL integration smoke",
            hasPostgresCredentials());

        schema = "warm_flow_jimmer_skip_it_" + System.currentTimeMillis();
        createSchema(schema);
        JimmerClients.setSqlClient(postgresClient(schema));
        setWarmFlow(true, "skip-tenant");

        FlowSkipDaoImpl dao = new FlowSkipDaoImpl();
        FlowSkip skip = new FlowSkip()
            .setId(910000000000000701L)
            .setDefinitionId(910000000000000702L)
            .setNodeId(910000000000000703L)
            .setNowNodeCode("start")
            .setNowNodeType(0)
            .setNextNodeCode("approve")
            .setNextNodeType(1)
            .setSkipType("PASS")
            .setDelFlag("0")
            .setTenantId("skip-tenant");

        assertEquals(1, dao.save(skip));
        FlowSkip selected = dao.selectById(skip.getId());
        assertNotNull(selected);
        assertEquals(skip.getNodeId(), selected.getNodeId());
    }

    @Test
    public void formDaoExecutesAgainstPostgresSchema() throws Exception {
        Assume.assumeTrue("Set -Dwarm.jimmer.it.postgres.url to run PostgreSQL integration smoke",
            hasPostgresCredentials());

        schema = "warm_flow_jimmer_form_it_" + System.currentTimeMillis();
        createSchema(schema);
        JimmerClients.setSqlClient(postgresClient(schema));
        setWarmFlow(true, "form-tenant");

        FlowFormDaoImpl dao = new FlowFormDaoImpl();
        FlowForm form = new FlowForm()
            .setId(910000000000000401L)
            .setFormCode("expense_form")
            .setFormName("Expense Form")
            .setVersion("1.0")
            .setIsPublish(0)
            .setFormType(0)
            .setFormContent("{}")
            .setDelFlag("0")
            .setTenantId("form-tenant");

        assertEquals(1, dao.save(form));
        assertEquals("expense_form", dao.selectById(form.getId()).getFormCode());
        assertEquals(1, dao.queryByCodeList(Collections.singletonList("expense_form")).size());
        assertEquals(1, dao.deleteById(form.getId()));
        assertNull(dao.selectById(form.getId()));
        temporarilyWithoutLogicDelete(() -> assertEquals("2", dao.selectById(form.getId()).getDelFlag()));
    }

    @Test
    public void executesEndToEndWorkflowAgainstPostgresSchema() throws Exception {
        Assume.assumeTrue("Set -Dwarm.jimmer.it.postgres.url to run PostgreSQL integration smoke",
            hasPostgresCredentials());

        schema = "warm_flow_jimmer_e2e_it_" + System.currentTimeMillis();
        createSchema(schema);
        JimmerClients.setSqlClient(postgresClient(schema));
        setWarmFlow(true, "workflow-tenant");
        setFlowEngineRuntimeServices();

        Long definitionId = 910000000001000001L;
        Long startNodeId = 910000000001000002L;
        Long approveNodeId = 910000000001000003L;
        Long endNodeId = 910000000001000004L;
        String flowCode = "jimmer_e2e_leave";

        FlowDefinition definition = new FlowDefinition()
            .setId(definitionId)
            .setFlowCode(flowCode)
            .setFlowName("Jimmer E2E Leave")
            .setModelValue("CLASSICS")
            .setIsPublish(PublishStatus.PUBLISHED.getKey())
            .setActivityStatus(ActivityStatus.ACTIVITY.getKey())
            .setFormCustom("N");
        FlowNode start = new FlowNode()
            .setId(startNodeId)
            .setDefinitionId(definitionId)
            .setNodeType(NodeType.START.getKey())
            .setNodeCode("start")
            .setNodeName("Start")
            .setNodeRatio("0");
        FlowNode approve = new FlowNode()
            .setId(approveNodeId)
            .setDefinitionId(definitionId)
            .setNodeType(NodeType.BETWEEN.getKey())
            .setNodeCode("approve")
            .setNodeName("Approve")
            .setNodeRatio("0")
            .setPermissionFlag("approver");
        FlowNode end = new FlowNode()
            .setId(endNodeId)
            .setDefinitionId(definitionId)
            .setNodeType(NodeType.END.getKey())
            .setNodeCode("end")
            .setNodeName("End")
            .setNodeRatio("0");
        FlowSkip startToApprove = new FlowSkip()
            .setId(910000000001000005L)
            .setDefinitionId(definitionId)
            .setNodeId(startNodeId)
            .setNowNodeCode("start")
            .setNowNodeType(NodeType.START.getKey())
            .setNextNodeCode("approve")
            .setNextNodeType(NodeType.BETWEEN.getKey())
            .setSkipType(SkipType.PASS.getKey());
        FlowSkip approveToEnd = new FlowSkip()
            .setId(910000000001000006L)
            .setDefinitionId(definitionId)
            .setNodeId(approveNodeId)
            .setNowNodeCode("approve")
            .setNowNodeType(NodeType.BETWEEN.getKey())
            .setNextNodeCode("end")
            .setNextNodeType(NodeType.END.getKey())
            .setSkipType(SkipType.PASS.getKey());

        FlowEngine.defService().insertFlow(definition,
            Arrays.asList(start, approve, end),
            Arrays.asList(startToApprove, approveToEnd));

        Map<String, Object> startVariables = new HashMap<>();
        startVariables.put("days", 3);
        Instance instance = FlowEngine.insService().start("biz-jimmer-e2e-1", FlowParams.build()
            .flowCode(flowCode)
            .handler("starter")
            .variable(startVariables));

        assertNotNull(instance.getId());
        assertEquals(definitionId, instance.getDefinitionId());
        assertEquals("approve", instance.getNodeCode());
        assertEquals(NodeType.BETWEEN.getKey(), instance.getNodeType());
        assertEquals(FlowStatus.APPROVAL.getKey(), instance.getFlowStatus());

        FlowTaskDaoImpl taskDao = new FlowTaskDaoImpl();
        FlowInstanceDaoImpl instanceDao = new FlowInstanceDaoImpl();
        FlowHisTaskDaoImpl hisTaskDao = new FlowHisTaskDaoImpl();
        FlowUserDaoImpl userDao = new FlowUserDaoImpl();

        FlowInstance persistedInstance = instanceDao.selectById(instance.getId());
        assertNotNull(persistedInstance);
        assertEquals("biz-jimmer-e2e-1", persistedInstance.getBusinessId());
        assertEquals("approve", persistedInstance.getNodeCode());

        List<FlowTask> approvalTasks = taskDao.getByInsIdAndNodeCodes(instance.getId(), Collections.singletonList("approve"));
        assertEquals(1, approvalTasks.size());
        Task approvalTask = approvalTasks.get(0);
        assertEquals(FlowStatus.APPROVAL.getKey(), approvalTask.getFlowStatus());
        assertEquals(1, userDao.listByAssociatedAndTypes(Collections.singletonList(approvalTask.getId()),
            new String[] {UserType.APPROVAL.getKey()}).size());

        Map<String, Object> passVariables = new HashMap<>();
        passVariables.put("approved", true);
        Instance finished = FlowEngine.taskService().pass(approvalTask.getId(), "approved", passVariables);

        assertEquals(instance.getId(), finished.getId());
        assertEquals("end", finished.getNodeCode());
        assertEquals(NodeType.END.getKey(), finished.getNodeType());
        assertEquals(FlowStatus.FINISHED.getKey(), finished.getFlowStatus());

        FlowInstance finishedInstance = instanceDao.selectById(instance.getId());
        assertNotNull(finishedInstance);
        assertEquals("end", finishedInstance.getNodeCode());
        assertEquals(NodeType.END.getKey(), finishedInstance.getNodeType());
        assertEquals(FlowStatus.FINISHED.getKey(), finishedInstance.getFlowStatus());
        assertEquals(Boolean.TRUE, finishedInstance.getVariableMap().get("approved"));

        assertTrue(taskDao.getByInsIdAndNodeCodes(instance.getId(), Collections.singletonList("approve")).isEmpty());
        assertTrue(userDao.listByAssociatedAndTypes(Collections.singletonList(approvalTask.getId()), new String[] {UserType.APPROVAL.getKey()}).isEmpty());

        List<FlowHisTask> history = hisTaskDao.selectList(new FlowHisTask().setInstanceId(instance.getId()), null);
        assertEquals(2, history.size());
        assertTrue(history.stream().anyMatch(row -> "start".equals(row.getNodeCode())
            && "approve".equals(row.getTargetNodeCode())
            && SkipType.PASS.getKey().equals(row.getSkipType())));
        assertTrue(history.stream().anyMatch(row -> "approve".equals(row.getNodeCode())
            && "end".equals(row.getTargetNodeCode())
            && "approver".equals(row.getApprover())
            && SkipType.PASS.getKey().equals(row.getSkipType())
            && FlowStatus.PASS.getKey().equals(row.getFlowStatus())));
    }

    private void setWarmFlow(boolean logicDelete, String tenantId) throws Exception {
        WarmFlow warmFlow = new WarmFlow();
        warmFlow.setLogicDelete(logicDelete);
        warmFlow.setLogicNotDeleteValue("0");
        warmFlow.setLogicDeleteValue("2");
        FlowEngine.setFlowConfig(warmFlow);
        setTenantHandler(() -> tenantId);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void setFlowEngineRuntimeServices() throws Exception {
        FlowEngine.setNewDef(FlowDefinition::new);
        FlowEngine.setNewIns(FlowInstance::new);
        FlowEngine.setNewHisTask(FlowHisTask::new);
        FlowEngine.setNewNode(FlowNode::new);
        FlowEngine.setNewSkip(FlowSkip::new);
        FlowEngine.setNewTask(FlowTask::new);
        FlowEngine.setNewUser(FlowUser::new);
        FlowEngine.setNewForm(FlowForm::new);
        setDataFillHandler(new DataFillHandler() {});
        setPermissionHandler(new PermissionHandler() {
            @Override
            public List<String> permissions() {
                return Collections.singletonList("approver");
            }

            @Override
            public String getHandler() {
                return "approver";
            }
        });
        FlowEngine.jsonConvert = new TestJsonConvert();

        FlowDefinitionDaoImpl definitionDao = new FlowDefinitionDaoImpl();
        FlowNodeDaoImpl nodeDao = new FlowNodeDaoImpl();
        FlowSkipDaoImpl skipDao = new FlowSkipDaoImpl();
        FlowInstanceDaoImpl instanceDao = new FlowInstanceDaoImpl();
        FlowTaskDaoImpl taskDao = new FlowTaskDaoImpl();
        FlowHisTaskDaoImpl hisTaskDao = new FlowHisTaskDaoImpl();
        FlowUserDaoImpl userDao = new FlowUserDaoImpl();
        FlowFormDaoImpl formDao = new FlowFormDaoImpl();

        DefService defService = new DefServiceImpl().setDao((org.dromara.warm.flow.core.orm.dao.FlowDefinitionDao) definitionDao);
        NodeService nodeService = new NodeServiceImpl().setDao((org.dromara.warm.flow.core.orm.dao.FlowNodeDao) nodeDao);
        SkipService skipService = new SkipServiceImpl().setDao((org.dromara.warm.flow.core.orm.dao.FlowSkipDao) skipDao);
        InsService insService = new InsServiceImpl().setDao((org.dromara.warm.flow.core.orm.dao.FlowInstanceDao) instanceDao);
        TaskService taskService = new TaskServiceImpl().setDao((org.dromara.warm.flow.core.orm.dao.FlowTaskDao) taskDao);
        HisTaskService hisTaskService = new HisTaskServiceImpl().setDao((org.dromara.warm.flow.core.orm.dao.FlowHisTaskDao) hisTaskDao);
        UserService userService = new UserServiceImpl().setDao((org.dromara.warm.flow.core.orm.dao.FlowUserDao) userDao);
        FormService formService = new FormServiceImpl().setDao((org.dromara.warm.flow.core.orm.dao.FlowFormDao) formDao);
        ChartService chartService = new ChartServiceImpl();

        FrameInvoker.setBeanFunction((Function<Class<Object>, Object>) beanType -> {
            if (DefService.class.equals(beanType)) {
                return defService;
            }
            if (NodeService.class.equals(beanType)) {
                return nodeService;
            }
            if (SkipService.class.equals(beanType)) {
                return skipService;
            }
            if (InsService.class.equals(beanType)) {
                return insService;
            }
            if (TaskService.class.equals(beanType)) {
                return taskService;
            }
            if (HisTaskService.class.equals(beanType)) {
                return hisTaskService;
            }
            if (UserService.class.equals(beanType)) {
                return userService;
            }
            if (FormService.class.equals(beanType)) {
                return formService;
            }
            if (ChartService.class.equals(beanType)) {
                return chartService;
            }
            return null;
        });
    }

    private void temporarilyWithoutTenant(CheckedRunnable runnable) throws Exception {
        TenantHandler previous = FlowEngine.tenantHandler();
        setTenantHandler(null);
        try {
            runnable.run();
        } finally {
            setTenantHandler(previous);
        }
    }

    private void temporarilyWithoutLogicDelete(CheckedRunnable runnable) throws Exception {
        boolean previous = FlowEngine.getFlowConfig().isLogicDelete();
        FlowEngine.getFlowConfig().setLogicDelete(false);
        try {
            runnable.run();
        } finally {
            FlowEngine.getFlowConfig().setLogicDelete(previous);
        }
    }

    private void resetFlowEngine() throws Exception {
        FlowEngine.setFlowConfig(null);
        setTenantHandler(null);
        setDataFillHandler(null);
        setPermissionHandler(null);
        FlowEngine.jsonConvert = null;
        FrameInvoker.setBeanFunction(beanType -> null);
    }

    private void setTenantHandler(TenantHandler tenantHandler) throws Exception {
        Field field = FlowEngine.class.getDeclaredField("tenantHandler");
        field.setAccessible(true);
        field.set(null, tenantHandler);
    }

    private void setDataFillHandler(DataFillHandler dataFillHandler) throws Exception {
        Field field = FlowEngine.class.getDeclaredField("dataFillHandler");
        field.setAccessible(true);
        field.set(null, dataFillHandler);
    }

    private void setPermissionHandler(PermissionHandler permissionHandler) throws Exception {
        Field field = FlowEngine.class.getDeclaredField("permissionHandler");
        field.setAccessible(true);
        field.set(null, permissionHandler);
    }

    private interface CheckedRunnable {
        void run() throws Exception;
    }

    private static class TestJsonConvert implements JsonConvert {

        private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

        @Override
        public Map<String, Object> strToMap(String jsonStr) {
            if (jsonStr == null || jsonStr.trim().isEmpty()) {
                return new HashMap<>();
            }
            try {
                return OBJECT_MAPPER.readValue(jsonStr, new TypeReference<Map<String, Object>>() {});
            } catch (Exception e) {
                throw new IllegalStateException("Cannot parse JSON map", e);
            }
        }

        @Override
        public <T> T strToBean(String jsonStr, Class<T> clazz) {
            if (jsonStr == null || jsonStr.trim().isEmpty()) {
                return null;
            }
            try {
                return OBJECT_MAPPER.readValue(jsonStr, clazz);
            } catch (Exception e) {
                throw new IllegalStateException("Cannot parse JSON bean", e);
            }
        }

        @Override
        public <T> List<T> strToList(String jsonStr) {
            if (jsonStr == null || jsonStr.trim().isEmpty()) {
                return Collections.emptyList();
            }
            try {
                return OBJECT_MAPPER.readValue(jsonStr, new TypeReference<List<T>>() {});
            } catch (Exception e) {
                throw new IllegalStateException("Cannot parse JSON list", e);
            }
        }

        @Override
        public String objToStr(Object variable) {
            if (variable == null) {
                return null;
            }
            try {
                return OBJECT_MAPPER.writeValueAsString(variable);
            } catch (Exception e) {
                throw new IllegalStateException("Cannot write JSON", e);
            }
        }
    }

    private void createSchema(String schemaName) throws Exception {
        try (Connection connection = DriverManager.getConnection(postgresUrl(), postgresUser(), postgresPassword());
             Statement statement = connection.createStatement()) {
            statement.execute("drop schema if exists " + schemaName + " cascade");
            statement.execute("create schema " + schemaName);
            statement.execute("set search_path to " + schemaName);
            for (String sql : readPostgresDdl().split(";")) {
                String trimmed = sql.trim();
                if (!trimmed.isEmpty()) {
                    statement.execute(trimmed);
                }
            }
        }
    }

    private JSqlClient postgresClient(String schemaName) {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setUrl(withCurrentSchema(postgresUrl(), schemaName));
        dataSource.setUser(postgresUser());
        dataSource.setPassword(postgresPassword());
        return JSqlClient.newBuilder()
            .setConnectionManager(ConnectionManager.simpleConnectionManager(dataSource))
            .setDialect(new PostgresDialect())
            .setMutationTransactionRequired(false)
            .build();
    }

    private String readPostgresDdl() throws Exception {
        Path current = Paths.get("").toAbsolutePath();
        for (int i = 0; i < 8 && current != null; i++) {
            Path ddl = current.resolve("sql/postgresql/postgresql-warm-flow-all.sql");
            if (Files.isRegularFile(ddl)) {
                return new String(Files.readAllBytes(ddl), StandardCharsets.UTF_8);
            }
            current = current.getParent();
        }
        throw new IllegalStateException("Cannot find sql/postgresql/postgresql-warm-flow-all.sql");
    }

    private boolean hasPostgresCredentials() {
        return notBlank(postgresUrl()) && notBlank(postgresUser()) && notBlank(postgresPassword());
    }

    private boolean notBlank(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private String withCurrentSchema(String url, String schemaName) {
        String separator = url.indexOf('?') >= 0 ? "&" : "?";
        return url + separator + "currentSchema=" + schemaName;
    }

    private String postgresUrl() {
        return System.getProperty("warm.jimmer.it.postgres.url", System.getenv("WARM_JIMMER_IT_POSTGRES_URL"));
    }

    private String postgresUser() {
        return System.getProperty("warm.jimmer.it.postgres.user", System.getenv("WARM_JIMMER_IT_POSTGRES_USER"));
    }

    private String postgresPassword() {
        return System.getProperty("warm.jimmer.it.postgres.password", System.getenv("WARM_JIMMER_IT_POSTGRES_PASSWORD"));
    }
}
