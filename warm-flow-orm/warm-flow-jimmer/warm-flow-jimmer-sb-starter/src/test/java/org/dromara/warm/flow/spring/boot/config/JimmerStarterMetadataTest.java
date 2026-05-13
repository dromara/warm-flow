/*
 *    Copyright 2024-2025, Warm-Flow (290631660@qq.com).
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 */
package org.dromara.warm.flow.spring.boot.config;

import org.junit.Test;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.*;

public class JimmerStarterMetadataTest {

    @Test
    public void exposesBoot2AndBoot3AutoConfigurationMetadata() throws Exception {
        assertResourceContains("META-INF/spring.factories", FlowAutoConfig.class.getName());
        assertResourceContains("META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports",
            FlowAutoConfig.class.getName());
    }

    private void assertResourceContains(String path, String expected) throws Exception {
        try (InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(path)) {
            assertNotNull(path, in);
            byte[] bytes = new byte[in.available()];
            assertEquals(bytes.length, in.read(bytes));
            assertTrue(new String(bytes, StandardCharsets.UTF_8).contains(expected));
        }
    }
}
