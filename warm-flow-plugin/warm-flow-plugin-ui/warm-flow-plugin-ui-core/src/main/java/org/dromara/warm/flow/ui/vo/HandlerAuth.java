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
package org.dromara.warm.flow.ui.vo;

/**
 * 流程设计器-办理人权限设置列表
 * 办理人权限列表选择框，可能存在多个，比如：部门、角色、用户的情况
 *
 * @author warm
 */
public class HandlerAuth {

    /** 入库权限主键，比如怕角色和用户id重复，可拼接为role:id */
    private String storageId;

    /** 权限编码，如：zhang、roleAdmin、deptAdmin等编码 */
    private String handlerCode;

    /** 权限名称，如：管理员、角色管理员、部门管理员等名称 */
    private String handlerName;

    /** 权限分组名称，如：角色、部门等名称 */
    private String groupName;

    /** 创建时间 */
    private String createTime;

    public String getStorageId() {
        return storageId;
    }

    public HandlerAuth setStorageId(String storageId) {
        this.storageId = storageId;
        return this;
    }

    public String getHandlerCode() {
        return handlerCode;
    }

    public HandlerAuth setHandlerCode(String handlerCode) {
        this.handlerCode = handlerCode;
        return this;
    }

    public String getHandlerName() {
        return handlerName;
    }

    public HandlerAuth setHandlerName(String handlerName) {
        this.handlerName = handlerName;
        return this;
    }

    public String getGroupName() {
        return groupName;
    }

    public HandlerAuth setGroupName(String groupName) {
        this.groupName = groupName;
        return this;
    }

    public String getCreateTime() {
        return createTime;
    }

    public HandlerAuth setCreateTime(String createTime) {
        this.createTime = createTime;
        return this;
    }
}
