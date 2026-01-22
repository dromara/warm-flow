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
import org.dromara.warm.flow.core.entity.HisTask;
import org.dromara.warm.flow.orm.strategy.WarmFlowLogicDeleteStrategy;
import org.dromara.warm.flow.orm.entity.proxy.FlowHisTaskProxy;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

/**
 * 历史任务记录对象 flow_his_task
 *
 * @author warm
 * @since 2023-03-29
 */
@Data
@Accessors(chain = true)
@EntityProxy
@Table("flow_his_task")
public class FlowHisTask implements HisTask, ProxyEntityAvailable<FlowHisTask, FlowHisTaskProxy> {

    /** 主键 */
    @Column(value = "id", primaryKey = true)
    private Long id;

    /** 任务开始时间 */
    private Date createTime;

    /** 审批完成时间 */
    private Date updateTime;

    /** 租户ID */
    private String tenantId;

    /** 删除标记 */
    @LogicDelete(strategy = LogicDeleteStrategyEnum.CUSTOM,strategyName = WarmFlowLogicDeleteStrategy.NAME)
    private String delFlag;

    /** 对应flow_definition表的id */
    private Long definitionId;

    /** 流程名称 */
    @ColumnIgnore
    private String flowName;

    /** 流程实例表id */
    private Long instanceId;

    /** 任务表id */
    private Long taskId;

    /** 协作方式(1审批 2转办 3委派 4会签 5票签 6加签 7减签) */
    private Integer cooperateType;

    /** 业务id */
    @ColumnIgnore
    private String businessId;

    /** 开始节点编码 */
    private String nodeCode;

    /** 开始节点名称 */
    private String nodeName;

    /** 开始节点类型（0开始节点 1中间节点 2结束节点 3互斥网关 4并行网关） */
    private Integer nodeType;

    /** 目标节点编码 */
    private String targetNodeCode;

    /** 结束节点名称 */
    private String targetNodeName;

    /** 审批者 */
    private String approver;

    /** 协作人(只有转办、会签、票签、委派) */
    private String collaborator;

    /** 权限标识 permissionFlag的list形式 */
    @ColumnIgnore
    private List<String> permissionList;

    /**
     * 跳转类型（PASS通过 REJECT退回 NONE无动作）
     */
    private String skipType;

    /**
     * 流程状态（0待提交 1审批中 2审批通过 4终止 5作废 6撤销 8已完成 9已退回 10失效 11拿回）
     **/
    private String flowStatus;

    /** 审批意见 */
    private String message;

    /** 流程变量 */
    private String variable;

    /** 业务详情 存业务类的json */
    private String ext;

    /** 创建者 */
    @ColumnIgnore
    private String createBy;


    /** 审批表单是否自定义（Y=是 N=否） */
    private String formCustom;

    /** 审批表单是否自定义（Y是 2否） */
    private String formPath;

}
