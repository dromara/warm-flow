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
import org.dromara.warm.flow.core.entity.Task;
import org.dromara.warm.flow.core.entity.User;
import org.dromara.warm.flow.orm.entity.proxy.FlowTaskProxy;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

/**
 * 待办任务记录对象 flow_task
 *
 * @author warm
 * @since 2023-03-29
 */
@Getter
@Setter
@Accessors(chain = true)
@EntityProxy
@Table("flow_task")
public class FlowTask implements Task, ProxyEntityAvailable<FlowTask, FlowTaskProxy> {

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

    /** 对应flow_definition表的id */
    @EasyWhereCondition(type = EasyWhereCondition.Condition.EQUAL)
    private Long definitionId;

    /** 流程实例表id */
    @EasyWhereCondition(type = EasyWhereCondition.Condition.EQUAL)
    private Long instanceId;

    /** 流程名称 */
    @ColumnIgnore
    private String flowName;

    /** 业务id */
    @ColumnIgnore
    private String businessId;

    /** 节点编码 */
    @EasyWhereCondition(type = EasyWhereCondition.Condition.EQUAL)
    private String nodeCode;

    /** 节点名称 */
    @EasyWhereCondition(type = EasyWhereCondition.Condition.EQUAL)
    private String nodeName;

    /** 节点类型（0开始节点 1中间节点 2结束节点 3互斥网关 4并行网关） */
    @EasyWhereCondition(type = EasyWhereCondition.Condition.EQUAL)
    private Integer nodeType;

    /** 权限标识 permissionFlag的list形式 */
    @ColumnIgnore
    private List<String> permissionList;

    /** 流程用户列表 */
    @ColumnIgnore
    private List<User> userList;

    /** 审批表单是否自定义（Y是 2否） */
    @EasyWhereCondition(type = EasyWhereCondition.Condition.EQUAL)
    private String formCustom;

    /** 审批表单是否自定义（Y是 2否） */
    @EasyWhereCondition(type = EasyWhereCondition.Condition.EQUAL)
    private String formPath;


    @Override
    public String toString() {
        return "FlowTask{" +
            "id=" + id +
            ", createTime=" + createTime +
            ", updateTime=" + updateTime +
            ", definitionId=" + definitionId +
            ", instanceId=" + instanceId +
            ", tenantId='" + tenantId + '\'' +
            ", flowName='" + flowName + '\'' +
            ", businessId='" + businessId + '\'' +
            ", nodeCode='" + nodeCode + '\'' +
            ", nodeName='" + nodeName + '\'' +
            ", nodeType=" + nodeType +
            ", permissionList=" + permissionList +
            ", userList=" + userList +
            ", formCustom='" + formCustom + '\'' +
            ", formPath='" + formPath + '\'' +
            "} " + super.toString();
    }
}
