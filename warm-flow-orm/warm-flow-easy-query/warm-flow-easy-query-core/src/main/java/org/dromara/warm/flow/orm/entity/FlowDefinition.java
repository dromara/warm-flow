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
import org.dromara.warm.flow.core.entity.Definition;
import org.dromara.warm.flow.core.entity.Node;
import org.dromara.warm.flow.core.entity.User;
import org.dromara.warm.flow.orm.entity.proxy.FlowDefinitionProxy;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 流程定义对象 flow_definition
 *
 * @author warm
 * @date 2023-03-29
 */
@Getter
@Setter
@Accessors(chain = true)
@EntityProxy
@EasyAlias("flowDefinition")
@Table("flow_definition")
public class FlowDefinition implements Definition,ProxyEntityAvailable<FlowDefinition, FlowDefinitionProxy> {

    /** 主键 */
    @EasyWhereCondition(type = EasyWhereCondition.Condition.EQUAL)
    @Column(value = "id", primaryKey = true)
    private Long id;

    /** 创建时间 */
    private Date createTime;

    /** 更新时间 */
    private Date updateTime;

    /** 租户ID */
    @EasyWhereCondition(type = EasyWhereCondition.Condition.EQUAL)
    private String tenantId;

    /** 删除标记 */
    @LogicDelete(strategy = LogicDeleteStrategyEnum.CUSTOM,strategyName = "WarmFlowLogicDelete")
    private String delFlag;

    /** 流程编码 */
    @EasyWhereCondition(type = EasyWhereCondition.Condition.EQUAL)
    private String flowCode;

    /** 流程名称 */
    @EasyWhereCondition(type = EasyWhereCondition.Condition.EQUAL)
    private String flowName;

    /** 流程类别 */
    @EasyWhereCondition(type = EasyWhereCondition.Condition.EQUAL)
    private String category;

    /** 流程版本 */
    @EasyWhereCondition(type = EasyWhereCondition.Condition.EQUAL)
    private String version;

    /** 是否发布（0未发布 1已发布 9失效） */
    @EasyWhereCondition(type = EasyWhereCondition.Condition.EQUAL)
    private Integer isPublish;

    /** 审批表单是否自定义（Y是 2否） */
    @EasyWhereCondition(type = EasyWhereCondition.Condition.EQUAL)
    private String formCustom;

    /** 审批表单是否自定义（Y是 2否） */
    @EasyWhereCondition(type = EasyWhereCondition.Condition.EQUAL)
    private String formPath;

    /** 监听器类型 */
    @EasyWhereCondition(type = EasyWhereCondition.Condition.EQUAL)
    private String listenerType;

    /** 监听器路径 */
    @EasyWhereCondition(type = EasyWhereCondition.Condition.EQUAL)
    private String listenerPath;

    /** 流程激活状态（0挂起 1激活）*/
    @EasyWhereCondition(type = EasyWhereCondition.Condition.EQUAL)
    private Integer activityStatus;

    /** 扩展字段 */
    @EasyWhereCondition(type = EasyWhereCondition.Condition.EQUAL)
    private String ext;

    /** 审批表单是否自定义（Y是 2否） */
    @ColumnIgnore
    private String xmlString;

    @ColumnIgnore
    private List<Node> nodeList = new ArrayList<>();

    @ColumnIgnore
    private List<User> userList = new ArrayList<>();


    @Override
    public String toString() {
        return "FlowDefinition{" +
            "id=" + id +
            ", createTime=" + createTime +
            ", updateTime=" + updateTime +
            ", flowCode='" + flowCode + '\'' +
            ", flowName='" + flowName + '\'' +
            ", category='" + category + '\'' +
            ", version='" + version + '\'' +
            ", isPublish=" + isPublish +
            ", formCustom='" + formCustom + '\'' +
            ", formPath='" + formPath + '\'' +
            ", activityStatus=" + activityStatus +
            ", listenerType='" + listenerType + '\'' +
            ", listenerPath='" + listenerPath + '\'' +
            ", ext='" + ext + '\'' +
            ", xmlString='" + xmlString + '\'' +
            ", nodeList=" + nodeList +
            '}';
    }
}
