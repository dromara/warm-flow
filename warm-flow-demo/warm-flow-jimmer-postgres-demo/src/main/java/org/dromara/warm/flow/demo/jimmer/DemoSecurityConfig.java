/*
 *    Copyright 2024-2025, Warm-Flow (290631660@qq.com).
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 */
package org.dromara.warm.flow.demo.jimmer;

import org.dromara.warm.flow.core.handler.PermissionHandler;
import org.dromara.warm.flow.core.utils.CollUtil;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Development-only permission provider used by the demo app.
 *
 * <p>The production application should replace this with its own user, role,
 * department, or tenant authority mapping.</p>
 */
@Component
public class DemoSecurityConfig implements PermissionHandler {

    public static final String DEFAULT_HANDLER = "admin";
    public static final String DEFAULT_PERMISSION = "approver";

    @Override
    public List<String> permissions() {
        Set<String> permissions = new LinkedHashSet<>();
        permissions.add(DEFAULT_HANDLER);
        permissions.add(DEFAULT_PERMISSION);
        permissions.addAll(headerValues("X-Warm-Flow-Permissions"));
        return new ArrayList<>(permissions);
    }

    @Override
    public String getHandler() {
        String handler = header("X-Warm-Flow-Handler");
        return StringUtils.hasText(handler) ? handler : DEFAULT_HANDLER;
    }

    @Override
    public List<String> convertPermissions(List<String> permissions) {
        if (CollUtil.isEmpty(permissions)) {
            return Collections.emptyList();
        }
        Set<String> converted = new LinkedHashSet<>(permissions);
        for (String permission : permissions) {
            if (DEFAULT_PERMISSION.equals(permission)) {
                converted.add(DEFAULT_HANDLER);
            }
        }
        return new ArrayList<>(converted);
    }

    private List<String> headerValues(String name) {
        String value = header(name);
        if (!StringUtils.hasText(value)) {
            return Collections.emptyList();
        }
        return Arrays.asList(value.split("\\s*,\\s*"));
    }

    private String header(String name) {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (!(attributes instanceof ServletRequestAttributes)) {
            return null;
        }
        HttpServletRequest request = ((ServletRequestAttributes) attributes).getRequest();
        return request.getHeader(name);
    }
}
