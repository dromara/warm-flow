/*
 *    Copyright 2024-2025, Warm-Flow (290631660@qq.com).
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 */
package org.dromara.warm.flow.orm.dao;

import org.dromara.warm.flow.orm.entity.FlowTask;
import org.junit.Test;

import static org.junit.Assert.*;

public class JimmerOrderSafetyTest {

    private final InspectableFlowTaskDao dao = new InspectableFlowTaskDao();

    @Test
    public void convertsSafeColumnNamesToJimmerProperties() {
        assertEquals("createTime", dao.property("create_time"));
        assertEquals("updateTime", dao.property("update-time"));
        assertEquals("nodeCode", dao.property("node_code"));
        assertEquals("id", dao.property("id"));
    }

    @Test
    public void rejectsUnsafeOrderByFragments() {
        assertUnsafe("id desc");
        assertUnsafe("id;drop table flow_task");
        assertUnsafe("lower(id)");
        assertUnsafe("1id");
        assertUnsafe("id/*comment*/");
    }

    @Test
    public void allowsOnlyWhitelistedOrderPropertiesByDefault() {
        assertTrue(dao.orderable("id"));
        assertTrue(dao.orderable("createTime"));
        assertTrue(dao.orderable("updateTime"));
        assertFalse(dao.orderable("flowStatus"));
    }

    @Test
    public void rejectsUnsupportedButSafeOrderFieldBeforeQueryExecution() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> dao.verifyOrderField("flow_status"));
        assertTrue(ex.getMessage().contains("Unsupported orderBy field"));
    }

    private void assertUnsafe(String orderBy) {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> dao.property(orderBy));
        assertTrue(ex.getMessage().contains("Unsafe orderBy field"));
    }

    private static class InspectableFlowTaskDao extends FlowTaskDaoImpl {
        String property(String orderBy) {
            return toPropertyName(orderBy);
        }

        boolean orderable(String prop) {
            return isOrderable(prop);
        }

        void verifyOrderField(String orderBy) {
            String prop = toPropertyName(orderBy);
            if (!isOrderable(prop)) {
                throw new IllegalArgumentException("Unsupported orderBy field for Jimmer adapter: " + orderBy);
            }
        }
    }
}
