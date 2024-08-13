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
package com.warm.flow.orm.entity;

import com.easy.query.core.annotation.*;
import com.easy.query.core.basic.extension.logicdel.LogicDeleteStrategyEnum;
import com.easy.query.core.proxy.ProxyEntityAvailable;
import com.warm.flow.core.entity.Skip;
import com.warm.flow.orm.entity.proxy.FlowSkipProxy;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 节点跳转关联对象 flow_skip
 *
 * @author warm
 * @date 2023-03-29
 */
@Getter
@Setter
@EntityProxy
@Accessors(chain = true)
@Table("flow_skip")
public class FlowSkip implements Skip, ProxyEntityAvailable<FlowSkip, FlowSkipProxy> {

    /** 主键 */
    @Column(value = "id", primaryKey = true)
    @EasyWhereCondition(type = EasyWhereCondition.Condition.EQUAL)
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

    /** 流程id */
    @EasyWhereCondition(type = EasyWhereCondition.Condition.EQUAL)
    private Long definitionId;

    /** 节点id */
    @ColumnIgnore
    private Long nodeId;

    /** 当前流程节点的编码 */
    @EasyWhereCondition(type = EasyWhereCondition.Condition.EQUAL)
    private String nowNodeCode;

    /** 当前节点类型（0开始节点 1中间节点 2结束节点 3互斥网关 4并行网关） */
    @EasyWhereCondition(type = EasyWhereCondition.Condition.EQUAL)
    private Integer nowNodeType;

    /** 下一个流程节点的编码 */
    @EasyWhereCondition(type = EasyWhereCondition.Condition.EQUAL)
    private String nextNodeCode;

    /** 下一个节点类型（0开始节点 1中间节点 2结束节点 3互斥网关 4并行网关） */
    @EasyWhereCondition(type = EasyWhereCondition.Condition.EQUAL)
    private Integer nextNodeType;

    /** 跳转名称 */
    @EasyWhereCondition(type = EasyWhereCondition.Condition.EQUAL)
    private String skipName;

    /** 跳转类型（PASS审批通过 REJECT退回） */
    @EasyWhereCondition(type = EasyWhereCondition.Condition.EQUAL)
    private String skipType;

    /** 跳转条件 */
    @EasyWhereCondition(type = EasyWhereCondition.Condition.EQUAL)
    private String skipCondition;

    /** 流程跳转坐标 */
    @EasyWhereCondition(type = EasyWhereCondition.Condition.EQUAL)
    private String coordinate;


    @Override
    public String toString() {
        return "FlowSkip{" +
            "id=" + id +
            ", createTime=" + createTime +
            ", updateTime=" + updateTime +
            ", definitionId=" + definitionId +
            ", nodeId=" + nodeId +
            ", nowNodeCode='" + nowNodeCode + '\'' +
            ", nowNodeType=" + nowNodeType +
            ", nextNodeCode='" + nextNodeCode + '\'' +
            ", nextNodeType=" + nextNodeType +
            ", skipName='" + skipName + '\'' +
            ", skipType='" + skipType + '\'' +
            ", skipCondition='" + skipCondition + '\'' +
            ", coordinate='" + coordinate + '\'' +
            "} " + super.toString();
    }
}
