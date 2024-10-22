/*
 *    Copyright 2024-2025, Warm-Flow (290631660@qq.com).
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.warm.flow.ui.dto;

import java.util.List;
import java.util.function.Function;

/**
 * 办理人权限设置列表Function
 *
 * @author warm
 */
public class HandlerFunDto<T> {
    /** 权限列表 */
    private List<T> list;

    /** 权限列表list总数 */
    private long total;

    /** 获取入库权限主键Function */
    private Function<T, String> storageId;

    /** 获取权限编码Function */
    private Function<T, String> handlerCode;

    /** 获取权限名称Function */
    private Function<T, String> handlerName;

    /** 获取权限分组名称Function */
    private Function<T, String> groupName;

    /** 获取创建时间Function */
    private Function<T, String> createTime;

    public HandlerFunDto(List<T> list, long total) {
        this.list = list;
        this.total = total;
    }

    public List<T> getList() {
        return list;
    }

    public HandlerFunDto<T> setList(List<T> list) {
        this.list = list;
        return this;
    }

    public long getTotal() {
        return total;
    }

    public HandlerFunDto<T> setTotal(long total) {
        this.total = total;
        return this;
    }

    public Function<T, String> getStorageId() {
        return storageId;
    }

    public HandlerFunDto<T> setStorageId(Function<T, String> storageId) {
        this.storageId = storageId;
        return this;
    }

    public Function<T, String> getHandlerCode() {
        return handlerCode;
    }

    public HandlerFunDto<T> setHandlerCode(Function<T, String> handlerCode) {
        this.handlerCode = handlerCode;
        return this;
    }

    public Function<T, String> getHandlerName() {
        return handlerName;
    }

    public HandlerFunDto<T> setHandlerName(Function<T, String> handlerName) {
        this.handlerName = handlerName;
        return this;
    }

    public Function<T, String> getGroupName() {
        return groupName;
    }

    public HandlerFunDto<T> setGroupName(Function<T, String> groupName) {
        this.groupName = groupName;
        return this;
    }

    public Function<T, String> getCreateTime() {
        return createTime;
    }

    public HandlerFunDto<T> setCreateTime(Function<T, String> createTime) {
        this.createTime = createTime;
        return this;
    }
}
