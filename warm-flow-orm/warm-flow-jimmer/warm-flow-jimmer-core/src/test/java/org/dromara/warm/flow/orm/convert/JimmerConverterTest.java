/*
 *    Copyright 2024-2025, Warm-Flow (290631660@qq.com).
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 */
package org.dromara.warm.flow.orm.convert;

import org.dromara.warm.flow.core.entity.RootEntity;
import org.dromara.warm.flow.orm.entity.*;
import org.dromara.warm.flow.orm.model.*;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

public class JimmerConverterTest {

    @Test
    public void convertsDefinitionWithoutLosingPublishStatus() {
        Date createTime = new Date(1710000000000L);
        FlowDefinition entity = new FlowDefinition()
            .setId(1L)
            .setCreateTime(createTime)
            .setFlowCode("leave")
            .setFlowName("Leave Approval")
            .setVersion("1.0")
            .setIsPublish(1)
            .setDelFlag("0")
            .setTenantId("tenant-a");

        FlowDefinitionModel model = JimmerConverter.toModel(entity);
        assertEquals(1L, model.id());
        assertEquals("leave", model.flowCode());
        assertEquals(1, model.publishStatus());

        FlowDefinition copy = JimmerConverter.fromModel(model, FlowDefinition::new);
        assertEquals(entity.getId(), copy.getId());
        assertEquals(entity.getFlowCode(), copy.getFlowCode());
        assertEquals(entity.getFlowName(), copy.getFlowName());
        assertEquals(entity.getVersion(), copy.getVersion());
        assertEquals(entity.getIsPublish(), copy.getIsPublish());
        assertEquals(entity.getTenantId(), copy.getTenantId());
        assertEquals(createTime, copy.getCreateTime());
    }

    @Test
    public void convertsAllPersistentEntityTypes() {
        assertRoundTrip(new FlowDefinition().setId(1L).setFlowCode("def").setVersion("1"), FlowDefinitionModel.class);
        assertRoundTrip(new FlowForm().setId(2L).setFormCode("form").setVersion("1"), FlowFormModel.class);
        assertRoundTrip(new FlowHisTask().setId(3L).setInstanceId(4L).setTaskId(5L).setNodeCode("n1"), FlowHisTaskModel.class);
        assertRoundTrip(new FlowInstance().setId(4L).setDefinitionId(1L).setBusinessId("biz"), FlowInstanceModel.class);
        assertRoundTrip(new FlowNode().setId(5L).setDefinitionId(1L).setNodeCode("n1").setVersion("1"), FlowNodeModel.class);
        assertRoundTrip(new FlowSkip().setId(6L).setDefinitionId(1L).setNowNodeCode("n1").setNextNodeCode("n2"), FlowSkipModel.class);
        assertRoundTrip(new FlowTask().setId(7L).setInstanceId(4L).setNodeCode("n1"), FlowTaskModel.class);
        assertRoundTrip(new FlowUser().setId(8L).setType("1").setProcessedBy("u1").setAssociated(7L), FlowUserModel.class);
    }

    @Test
    public void rejectsPersistingEntitiesWithoutWarmFlowId() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> JimmerConverter.toModel(new FlowTask().setNodeCode("n1")));
        assertTrue(ex.getMessage().contains("FlowTask.id is required"));
    }

    private void assertRoundTrip(RootEntity entity, Class<?> expectedModelType) {
        Object model = JimmerConverter.toModel(entity);
        assertTrue(expectedModelType.isInstance(model));
        RootEntity copy = JimmerConverter.fromModel(model, () -> entity);
        assertEquals(entity.getClass(), copy.getClass());
        assertEquals(entity.getId(), copy.getId());
    }
}
