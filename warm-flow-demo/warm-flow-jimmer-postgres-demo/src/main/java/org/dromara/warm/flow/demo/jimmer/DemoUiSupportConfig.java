/*
 *    Copyright 2024-2025, Warm-Flow (290631660@qq.com).
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 */
package org.dromara.warm.flow.demo.jimmer;

import org.dromara.warm.flow.core.dto.FlowPage;
import org.dromara.warm.flow.core.dto.Tree;
import org.dromara.warm.flow.core.utils.HttpStatus;
import org.dromara.warm.flow.ui.dto.HandlerQuery;
import org.dromara.warm.flow.ui.service.CategoryService;
import org.dromara.warm.flow.ui.service.HandlerSelectService;
import org.dromara.warm.flow.ui.vo.HandlerAuth;
import org.dromara.warm.flow.ui.vo.HandlerSelectVo;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Minimal designer lookup data for the deployable demo.
 */
@Component
public class DemoUiSupportConfig implements CategoryService, HandlerSelectService {

    @Override
    public List<Tree> queryCategory() {
        return Collections.singletonList(new Tree().setId("demo").setName("演示流程").setParentId("0"));
    }

    @Override
    public List<String> getHandlerType() {
        return Collections.singletonList("用户");
    }

    @Override
    public HandlerSelectVo getHandlerSelect(HandlerQuery query) {
        List<HandlerAuth> rows = Arrays.asList(
            user("admin", "admin", "演示管理员"),
            user("approver", "approver", "演示审批人")
        );
        if (query != null && query.getHandlerName() != null && !query.getHandlerName().trim().isEmpty()) {
            String keyword = query.getHandlerName().trim();
            rows = rows.stream()
                .filter(row -> row.getHandlerName().contains(keyword) || row.getHandlerCode().contains(keyword))
                .collect(Collectors.toList());
        }
        FlowPage<HandlerAuth> page = new FlowPage<HandlerAuth>()
            .setCode(HttpStatus.SUCCESS)
            .setMsg("查询成功")
            .setRows(rows)
            .setTotal(rows.size());
        return new HandlerSelectVo().setHandlerAuths(page);
    }

    private HandlerAuth user(String storageId, String code, String name) {
        return new HandlerAuth()
            .setStorageId(storageId)
            .setHandlerCode(code)
            .setHandlerName(name)
            .setGroupName("演示用户");
    }
}
