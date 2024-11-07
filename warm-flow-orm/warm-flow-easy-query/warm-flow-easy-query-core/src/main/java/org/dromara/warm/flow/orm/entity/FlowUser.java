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
import org.dromara.warm.flow.core.entity.User;
import org.dromara.warm.flow.orm.entity.proxy.FlowUserProxy;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 流程用户对象 flow_user
 *
 * @author xiarg
 * @since 2024/5/10 10:58
 */
@Getter
@Setter
@EntityProxy
@Accessors(chain = true)
@Table("flow_user")
public class FlowUser implements User, ProxyEntityAvailable<FlowUser, FlowUserProxy> {

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

    /** 人员类型（1代办任务的审批人权限 2代办任务的转办人权限 3待办任务的委托人权限） */
    @EasyWhereCondition(type = EasyWhereCondition.Condition.EQUAL)
    private String type;

    /** 权限人 */
    @EasyWhereCondition(type = EasyWhereCondition.Condition.EQUAL)
    private String processedBy;

    /** 任务表id */
    @EasyWhereCondition(type = EasyWhereCondition.Condition.EQUAL)
    private Long associated;

    /** 创建人：比如作为委托的人保存 */
    @EasyWhereCondition(type = EasyWhereCondition.Condition.EQUAL)
    private String createBy;


    @Override
    public String toString() {
        return "FlowUser{" +
            "id=" + id +
            ", createTime=" + createTime +
            ", updateTime=" + updateTime +
            ", tenantId='" + tenantId + '\'' +
            ", delFlag='" + delFlag + '\'' +
            ", type='" + type + '\'' +
            ", processed_by='" + processedBy + '\'' +
            ", associated=" + associated +
            '}';
    }
}
