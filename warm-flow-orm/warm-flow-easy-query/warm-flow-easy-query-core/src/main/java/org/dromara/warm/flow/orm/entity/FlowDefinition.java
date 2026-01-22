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
package org.dromara.warm.flow.orm.entity;

import com.easy.query.core.annotation.*;
import com.easy.query.core.basic.extension.logicdel.LogicDeleteStrategyEnum;
import com.easy.query.core.proxy.ProxyEntityAvailable;
import lombok.Data;
import lombok.experimental.Accessors;
import org.dromara.warm.flow.core.entity.Definition;
import org.dromara.warm.flow.core.entity.Node;
import org.dromara.warm.flow.core.entity.User;
import org.dromara.warm.flow.orm.entity.proxy.FlowDefinitionProxy;
import org.dromara.warm.flow.orm.strategy.WarmFlowLogicDeleteStrategy;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 流程定义对象 flow_definition
 *
 * @author warm
 * @since 2023-03-29
 */
@Data
@Accessors(chain = true)
@EntityProxy
@Table("flow_definition")
public class FlowDefinition implements Definition,ProxyEntityAvailable<FlowDefinition, FlowDefinitionProxy> {

    /** 主键 */
    @Column(value = "id", primaryKey = true)
    private Long id;

    /** 创建时间 */
    private Date createTime;

    /** 更新时间 */
    private Date updateTime;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 更新人
     */
    private String updateBy;

    /** 租户ID */
    private String tenantId;

    /** 删除标记 */
    @LogicDelete(strategy = LogicDeleteStrategyEnum.CUSTOM,strategyName = WarmFlowLogicDeleteStrategy.NAME)
    private String delFlag;

    /** 流程编码 */
    private String flowCode;

    /** 流程名称 */
    private String flowName;

    /**
     * 设计器模型（CLASSICS经典模型 MIMIC仿钉钉模型）
     */
    private String modelValue;

    /** 流程类别 */
    private String category;

    /** 流程版本 */
    private String version;

    /** 是否发布（0未发布 1已发布 9失效） */
    private Integer isPublish;

    /** 审批表单是否自定义（Y是 N否） */
    private String formCustom;

    /** 审批表单路径 */
    private String formPath;

    /** 监听器类型 */
    private String listenerType;

    /** 监听器路径 */
    private String listenerPath;

    /** 流程激活状态（0挂起 1激活）*/
    private Integer activityStatus;

    /** 扩展字段 */
    private String ext;

    @ColumnIgnore
    private List<Node> nodeList = new ArrayList<>();

    @ColumnIgnore
    private List<User> userList = new ArrayList<>();

}
