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
package org.dromara.warm.flow.core.entity;

import org.dromara.warm.flow.core.FlowEngine;

import java.util.Date;
import java.util.List;

/**
 * 流程定义对象 flow_definition
 *
 * @author warm
 * @since 2023-03-29
 */
public interface Definition extends RootEntity {

    @Override
    Long getId();

    @Override
    Definition setId(Long id);

    @Override
    Date getCreateTime();

    @Override
    Definition setCreateTime(Date createTime);

    @Override
    Date getUpdateTime();

    @Override
    Definition setUpdateTime(Date updateTime);

    @Override
    String getCreateBy();

    @Override
    Definition setCreateBy(String createBy);

    @Override
    String getUpdateBy();

    @Override
    Definition setUpdateBy(String updateBy);

    @Override
    String getTenantId();

    @Override
    Definition setTenantId(String tenantId);

    @Override
    String getDelFlag();

    @Override
    Definition setDelFlag(String delFlag);

    /**
     * 获取流程编码
     * @return 流程编码
     */
    String getFlowCode();

    /**
     * 设置流程编码
     * @param flowCode flowCode
     * @return Definition
     */
    Definition setFlowCode(String flowCode);

    /**
     * 获取流程名称
     * @return 流程名称
     */
    String getFlowName();

    /**
     * 设置流程名称
     * @param flowName flowName
     * @return Definition
     */
    Definition setFlowName(String flowName);

    /**
     * 设计器模型（CLASSICS经典模型 MIMIC仿钉钉模型）
     * @see org.dromara.warm.flow.core.enums.ModelEnum
     * @return  设计器模型
     */
    String getModelValue();

    Definition setModelValue(String modelValue);

    String getCategory();

    Definition setCategory(String category);

    /**
     * 获取流程定义的版本号
     * @return 版本号
     */
    String getVersion();

    Definition setVersion(String version);

    /**
     * 获取是否发布状态 (0未发布 1已发布 9已失效)
     * @return 发布状态
     */
    Integer getIsPublish();

    Definition setIsPublish(Integer isPublish);

    /**
     * 审批表单是否自定义（Y=是 N=否）
     * @return 是否自定义
     */
    String getFormCustom();

    Definition setFormCustom(String formCustom);

    String getFormPath();

    Definition setFormPath(String formPath);

    String getExt();

    Definition setExt(String ext);

    List<Node> getNodeList();

    Definition setNodeList(List<Node> nodeList);

    List<User> getUserList();

    Definition setUserList(List<User> userList);

    /**
     * 流程激活状态（0=挂起 1=激活）
     * @see org.dromara.warm.flow.core.enums.ActivityStatus
     * @return 流程激活状态
     */
    Integer getActivityStatus();

    Definition setActivityStatus(Integer activityStatus);

    /**
     * 获取监听器类型
     * @return 监听器类型
     */
    String getListenerType();

    Definition setListenerType(String listenerType);

    /**
     * 获取监听器路径
     * @return 监听器路径
     */
    String getListenerPath();

    Definition setListenerPath(String listenerPath);

    default Definition copy() {
        return FlowEngine.newDef()
            .setTenantId(this.getTenantId())
            .setDelFlag(this.getDelFlag())
            .setFlowCode(this.getFlowCode())
            .setFlowName(this.getFlowName())
            .setModelValue(this.getModelValue())
            .setCategory(this.getCategory())
            .setVersion(this.getVersion())
            .setFormCustom(this.getFormCustom())
            .setFormPath(this.getFormPath())
            .setListenerType(this.getListenerType())
            .setListenerPath(this.getListenerPath())
            .setExt(this.getExt())
            .setCreateBy(this.getCreateBy())
            .setUpdateBy(this.getUpdateBy());

    }
}
