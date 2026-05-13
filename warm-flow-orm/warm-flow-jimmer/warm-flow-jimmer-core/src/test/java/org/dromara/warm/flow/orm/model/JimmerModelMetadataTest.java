/*
 *    Copyright 2024-2025, Warm-Flow (290631660@qq.com).
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 */
package org.dromara.warm.flow.orm.model;

import org.babyfish.jimmer.meta.ImmutableType;
import org.babyfish.jimmer.sql.dialect.PostgresDialect;
import org.babyfish.jimmer.sql.meta.DatabaseSchemaStrategy;
import org.babyfish.jimmer.sql.meta.ForeignKeyStrategy;
import org.babyfish.jimmer.sql.meta.MetaStringResolver;
import org.babyfish.jimmer.sql.meta.MetadataStrategy;
import org.babyfish.jimmer.sql.meta.ScalarTypeStrategy;
import org.babyfish.jimmer.sql.meta.Storage;
import org.babyfish.jimmer.sql.runtime.DefaultDatabaseNamingStrategy;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class JimmerModelMetadataTest {

    private static final MetadataStrategy STRATEGY = new MetadataStrategy(
        DatabaseSchemaStrategy.IMPLICIT,
        DefaultDatabaseNamingStrategy.LOWER_CASE,
        ForeignKeyStrategy.FORCED_FAKE,
        new PostgresDialect(),
        prop -> null,
        MetaStringResolver.NO_OP
    );

    @Test
    public void registersEveryPostgresTableModel() {
        assertTable(FlowDefinitionModel.class, "flow_definition");
        assertTable(FlowFormModel.class, "flow_form");
        assertTable(FlowHisTaskModel.class, "flow_his_task");
        assertTable(FlowInstanceModel.class, "flow_instance");
        assertTable(FlowNodeModel.class, "flow_node");
        assertTable(FlowSkipModel.class, "flow_skip");
        assertTable(FlowTaskModel.class, "flow_task");
        assertTable(FlowUserModel.class, "flow_user");
    }

    @Test
    public void mapsReservedPostgresColumnsExplicitly() {
        assertStorageContains(FlowDefinitionModel.class, "version", "version");
        assertStorageContains(FlowFormModel.class, "version", "version");
        assertStorageContains(FlowNodeModel.class, "version", "version");
        assertStorageContains(FlowUserModel.class, "type", "type");
        assertStorageContains(FlowDefinitionModel.class, "publishStatus", "is_publish");
    }

    @Test
    public void excludesDtoOnlyFieldsFromTaskAndHistoryModels() {
        assertNoProps(FlowTaskModel.class, "flowName", "businessId", "permissionList", "userList");
        assertNoProps(FlowHisTaskModel.class, "flowName", "businessId", "permissionList");
        assertNoProps(FlowInstanceModel.class, "flowName");
    }

    private void assertTable(Class<?> modelClass, String tableName) {
        ImmutableType type = ImmutableType.get(modelClass);
        assertTrue(type.isEntity());
        assertEquals(tableName, type.getTableName(STRATEGY));
        assertEquals("id", type.getIdProp().getName());
    }

    private void assertStorageContains(Class<?> modelClass, String prop, String expectedColumn) {
        Storage storage = ImmutableType.get(modelClass).getProp(prop).getStorage(STRATEGY);
        assertTrue("storage should include " + expectedColumn + ": " + storage,
            storage.toString().contains(expectedColumn));
    }

    private void assertNoProps(Class<?> modelClass, String... props) {
        ImmutableType type = ImmutableType.get(modelClass);
        Arrays.stream(props).forEach(prop -> assertFalse(prop, type.getProps().containsKey(prop)));
    }
}
