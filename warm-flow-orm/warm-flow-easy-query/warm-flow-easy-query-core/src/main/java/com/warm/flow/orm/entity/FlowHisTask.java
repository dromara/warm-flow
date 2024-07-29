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
import com.warm.flow.core.entity.HisTask;
import com.warm.flow.orm.entity.proxy.FlowHisTaskProxy;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

/**
 * 历史任务记录对象 flow_his_task
 *
 * @author warm
 * @date 2023-03-29
 */
@Getter
@Setter
@EntityProxy
@Accessors(chain = true)
@EasyAlias("flowHisTask")
@Table("flow_his_task")
public class FlowHisTask implements HisTask, ProxyEntityAvailable<FlowHisTask, FlowHisTaskProxy> {

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

    /** 对应flow_definition表的id */
    @EasyWhereCondition(type = EasyWhereCondition.Condition.EQUAL)
    private Long definitionId;

    /** 流程名称 */
    @ColumnIgnore
    private String flowName;

    /** 流程实例表id */
    @EasyWhereCondition(type = EasyWhereCondition.Condition.EQUAL)
    private Long instanceId;

    /** 任务表id */
    @EasyWhereCondition(type = EasyWhereCondition.Condition.EQUAL)
    private Long taskId;

    /** 协作方式(1审批 2转办 3委派 4会签 5票签 6加签 7减签) */
    @EasyWhereCondition(type = EasyWhereCondition.Condition.EQUAL)
    private Integer cooperateType;

    /** 业务id */
    @ColumnIgnore
    private String businessId;

    /** 开始节点编码 */
    @EasyWhereCondition(type = EasyWhereCondition.Condition.EQUAL)
    private String nodeCode;

    /** 开始节点名称 */
    @EasyWhereCondition(type = EasyWhereCondition.Condition.EQUAL)
    private String nodeName;

    /** 开始节点类型（0开始节点 1中间节点 2结束节点 3互斥网关 4并行网关） */
    @EasyWhereCondition(type = EasyWhereCondition.Condition.EQUAL)
    private Integer nodeType;

    /** 目标节点编码 */
    @EasyWhereCondition(type = EasyWhereCondition.Condition.EQUAL)
    private String targetNodeCode;

    /** 结束节点名称 */
    @EasyWhereCondition(type = EasyWhereCondition.Condition.EQUAL)
    private String targetNodeName;

    /** 审批者 */
    @EasyWhereCondition(type = EasyWhereCondition.Condition.EQUAL)
    private String approver;

    /** 协作人(只有转办、会签、票签、委派) */
    @EasyWhereCondition(type = EasyWhereCondition.Condition.EQUAL)
    private String collaborator;

    /** 权限标识 permissionFlag的list形式 */
    @ColumnIgnore
    private List<String> permissionList;

    /**
     * 跳转类型（PASS通过 REJECT退回 NONE无动作）
     */
    @EasyWhereCondition(type = EasyWhereCondition.Condition.EQUAL)
    private String skipType;

    /** 流程状态（1审批中 2 审批通过 9已退回 10失效） */
    private Integer flowStatus;

    /** 审批意见 */
    private String message;

    /** 业务详情 存业务类的json */
    private String ext;

    /** 创建者 */
    @ColumnIgnore
    private String createBy;


    /** 审批表单是否自定义（Y是 2否） */
    @EasyWhereCondition(type = EasyWhereCondition.Condition.EQUAL)
    private String formCustom;

    /** 审批表单是否自定义（Y是 2否） */
    @EasyWhereCondition(type = EasyWhereCondition.Condition.EQUAL)
    private String formPath;


    @Override
    public String toString() {
        return "FlowHisTask{" +
            "id=" + id +
            ", createTime=" + createTime +
            ", updateTime=" + updateTime +
            ", definitionId=" + definitionId +
            ", flowName='" + flowName + '\'' +
            ", instanceId=" + instanceId +
            ", taskId=" + taskId +
            ", cooperateType=" + cooperateType +
            ", tenantId='" + tenantId + '\'' +
            ", businessId='" + businessId + '\'' +
            ", nodeCode='" + nodeCode + '\'' +
            ", nodeName='" + nodeName + '\'' +
            ", nodeType=" + nodeType +
            ", targetNodeCode='" + targetNodeCode + '\'' +
            ", targetNodeName='" + targetNodeName + '\'' +
            ", approver='" + approver + '\'' +
            ", collaborator='" + collaborator + '\'' +
            ", permissionList=" + permissionList +
            ", skipType=" + skipType +
            ", flowStatus=" + flowStatus +
            ", message='" + message + '\'' +
            ", ext='" + ext + '\'' +
            ", createBy='" + createBy + '\'' +
            ", formCustom='" + formCustom + '\'' +
            ", formPath='" + formPath + '\'' +
            "} " + super.toString();
    }
}
