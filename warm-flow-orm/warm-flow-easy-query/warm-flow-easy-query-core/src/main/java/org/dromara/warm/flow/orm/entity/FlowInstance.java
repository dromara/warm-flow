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
import org.dromara.warm.flow.core.entity.Instance;
import org.dromara.warm.flow.orm.entity.proxy.FlowInstanceProxy;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 流程实例对象 flow_instance
 *
 * @author warm
 * @since 2023-03-29
 */
@Data
@EntityProxy
@Accessors(chain = true)
@Table("flow_instance")
public class FlowInstance implements Instance, ProxyEntityAvailable<FlowInstance, FlowInstanceProxy> {

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

    /** 对应flow_definition表的id */
    @EasyWhereCondition(type = EasyWhereCondition.Condition.EQUAL)
    private Long definitionId;

    /** 流程名称 */
    @ColumnIgnore
    private String flowName;

    /** 业务id */
    @EasyWhereCondition(type = EasyWhereCondition.Condition.EQUAL)
    private String businessId;

    /** 节点类型（0开始节点 1中间节点 2结束节点 3互斥网关 4并行网关） */
    @EasyWhereCondition(type = EasyWhereCondition.Condition.EQUAL)
    private Integer nodeType;

    /** 流程节点编码   每个流程的nodeCode是唯一的,即definitionId+nodeCode唯一,在数据库层面做了控制 */
    @EasyWhereCondition(type = EasyWhereCondition.Condition.EQUAL)
    private String nodeCode;

    /** 流程节点名称 */
    @EasyWhereCondition(type = EasyWhereCondition.Condition.EQUAL)
    private String nodeName;

    /** 流程变量 */
    private String variable;

    /** 流程状态（0待提交 1审批中 2 审批通过 3自动通过 4终止 5作废 6撤销 7取回  8已完成 9已退回 10失效） */
    @EasyWhereCondition(type = EasyWhereCondition.Condition.EQUAL)
    private String flowStatus;

    /** 流程激活状态（0挂起 1激活）*/
    @EasyWhereCondition(type = EasyWhereCondition.Condition.EQUAL)
    private Integer activityStatus;

    /** 创建者 */
    @EasyWhereCondition(type = EasyWhereCondition.Condition.EQUAL)
    private String createBy;

    /** 审批表单是否自定义（Y是 2否） */
    @ColumnIgnore
    private String formCustom;

    /** 审批表单是否自定义（Y是 2否） */
    @ColumnIgnore
    private String formPath;

    /** 扩展字段 */
    @EasyWhereCondition(type = EasyWhereCondition.Condition.EQUAL)
    private String ext;

}
