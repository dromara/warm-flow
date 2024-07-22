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
import com.easy.query.core.proxy.ProxyEntityAvailable;
import com.warm.flow.core.entity.Node;
import com.warm.flow.core.entity.Skip;
import com.warm.flow.orm.entity.proxy.FlowNodeProxy;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 流程节点对象 flow_node
 *
 * @author warm
 * @date 2023-03-29
 */
@Getter
@Setter
@EntityProxy
@Accessors(chain = true)
@Table("flow_node")
public class FlowNode implements Node, ProxyEntityAvailable<FlowNode, FlowNodeProxy> {

    /** 跳转条件 */
    @ColumnIgnore
    List<Skip> skipList = new ArrayList<>();
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
    @EasyWhereCondition(type = EasyWhereCondition.Condition.EQUAL)
    private String delFlag;

    /** 节点类型（0开始节点 1中间节点 2结束节点 3互斥网关 4并行网关） */
    @EasyWhereCondition(type = EasyWhereCondition.Condition.EQUAL)
    private Integer nodeType;

    /** 流程id */
    @EasyWhereCondition(type = EasyWhereCondition.Condition.EQUAL)
    private Long definitionId;

    /** 流程节点编码   每个流程的nodeCode是唯一的,即definitionId+nodeCode唯一,在数据库层面做了控制 */
    @EasyWhereCondition(type = EasyWhereCondition.Condition.EQUAL)
    private String nodeCode;

    /** 流程节点名称 */
    @EasyWhereCondition(type = EasyWhereCondition.Condition.EQUAL)
    private String nodeName;

    /** 权限标识（权限类型:权限标识，可以多个，用逗号隔开) */
    @EasyWhereCondition(type = EasyWhereCondition.Condition.EQUAL)
    private String permissionFlag;

    /** 流程签署比例值 */
    @EasyWhereCondition(type = EasyWhereCondition.Condition.EQUAL)
    private BigDecimal nodeRatio;

    /** 动态权限标识（权限类型:权限标识，可以多个，如role:1,role:2) */
    @ColumnIgnore
    private List<String> dynamicPermissionFlagList;

    /** 流程节点坐标 */
    @EasyWhereCondition(type = EasyWhereCondition.Condition.EQUAL)
    private String coordinate;

    /** 版本 */
    @EasyWhereCondition(type = EasyWhereCondition.Condition.EQUAL)
    private String version;

    /** 是否可以跳转任意节点（Y是 N否） */
    @EasyWhereCondition(type = EasyWhereCondition.Condition.EQUAL)
    private String skipAnyNode;

    /** 监听器类型 */
    @EasyWhereCondition(type = EasyWhereCondition.Condition.EQUAL)
    private String listenerType;

    /** 监听器路径 */
    @EasyWhereCondition(type = EasyWhereCondition.Condition.EQUAL)
    private String listenerPath;

    /** 处理器类型 */
    @EasyWhereCondition(type = EasyWhereCondition.Condition.EQUAL)
    private String handlerType;

    /** 处理器路径 */
    @EasyWhereCondition(type = EasyWhereCondition.Condition.EQUAL)
    private String handlerPath;


    @Override
    public String toString() {
        return "FlowNode{" +
            "skipList=" + skipList +
            ", id=" + id +
            ", createTime=" + createTime +
            ", updateTime=" + updateTime +
            ", tenantId='" + tenantId + '\'' +
            ", delFlag='" + delFlag + '\'' +
            ", nodeType=" + nodeType +
            ", definitionId=" + definitionId +
            ", nodeCode='" + nodeCode + '\'' +
            ", nodeName='" + nodeName + '\'' +
            ", permissionFlag='" + permissionFlag + '\'' +
            ", nodeRatio=" + nodeRatio +
            ", dynamicPermissionFlagList=" + dynamicPermissionFlagList +
            ", coordinate='" + coordinate + '\'' +
            ", version='" + version + '\'' +
            ", skipAnyNode='" + skipAnyNode + '\'' +
            ", listenerType='" + listenerType + '\'' +
            ", listenerPath='" + listenerPath + '\'' +
            ", handlerType='" + handlerType + '\'' +
            ", handlerPath='" + handlerPath + '\'' +
            '}';
    }
}
