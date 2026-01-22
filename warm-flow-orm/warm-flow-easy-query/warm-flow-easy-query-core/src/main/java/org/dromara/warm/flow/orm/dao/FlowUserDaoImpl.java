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
package org.dromara.warm.flow.orm.dao;

import com.easy.query.core.expression.lambda.SQLActionExpression1;
import com.easy.query.core.util.EasyArrayUtil;
import com.easy.query.core.util.EasyCollectionUtil;
import org.dromara.warm.flow.core.orm.dao.FlowUserDao;
import org.dromara.warm.flow.core.utils.StringUtils;
import org.dromara.warm.flow.orm.entity.FlowUser;
import org.dromara.warm.flow.orm.entity.proxy.FlowUserProxy;

import java.util.List;
import java.util.Objects;

/**
 * 流程用户Mapper接口
 * @author link2fun
 */
public class FlowUserDaoImpl extends WarmDaoImpl<FlowUser, FlowUserProxy> implements FlowUserDao<FlowUser> {

    @Override
    public FlowUser newEntity() {
        return new FlowUser();
    }

    @Override
    public int deleteByTaskIds(List<Long> taskIdList) {
        return (int) deletable()
            .where(proxy -> {
                proxy.associated().in(taskIdList);
            })
            .useLogicDelete(isLogicDelete())
            .allowDeleteStatement(!isLogicDelete())
            .executeRows();

    }

    @Override
    public List<FlowUser> listByAssociatedAndTypes(List<Long> associatedList, String[] types) {

        return queryable()
            .useLogicDelete(isLogicDelete())
            .where(proxy -> {
                proxy.associated().in(EasyCollectionUtil.isNotEmpty(associatedList), associatedList);
                proxy.type().in(EasyArrayUtil.isNotEmpty(types), types);
            })
            .toList();
    }

    @Override
    public List<FlowUser> listByProcessedBys(Long associated, List<String> processedBys, String[] types) {
        return  queryable()
            .useLogicDelete(isLogicDelete())
            .where(proxy -> {
                proxy.associated().eq(Objects.nonNull(associated), associated);
                proxy.processedBy().in(EasyCollectionUtil.isNotEmpty(processedBys), processedBys);
                proxy.type().in(EasyArrayUtil.isNotEmpty(types),types);
            })
            .toList();
    }

    @Override
    public int delete(FlowUser entity) {
        // 没有使用逻辑删除， 直接物理删除
        return (int) deletable()
            .useLogicDelete(isLogicDelete())
            .allowDeleteStatement(!isLogicDelete())
            .where(buildWhereCondition(entity))
            .executeRows();
    }

    /** 参照 mybatis 实现， 构建删除条件 */
    @Override
    public SQLActionExpression1<FlowUserProxy> buildWhereCondition(FlowUser entity) {
        return o -> {
            o.id().eq(Objects.nonNull(entity.getId()), entity.getId());
            o.type().eq(StringUtils.isNotEmpty(entity.getType()), entity.getType());
            o.processedBy().eq(StringUtils.isNotEmpty(entity.getProcessedBy()), entity.getProcessedBy());
            o.associated().eq(Objects.nonNull(entity.getAssociated()), entity.getAssociated());
            o.createBy().eq(StringUtils.isNotEmpty(entity.getCreateBy()), entity.getCreateBy());
            o.createTime().eq(Objects.nonNull(entity.getCreateTime()), entity.getCreateTime());
            o.updateTime().eq(Objects.nonNull(entity.getUpdateTime()), entity.getUpdateTime());
            o.tenantId().eq(StringUtils.isNotEmpty(entity.getTenantId()), entity.getTenantId());
            o.createBy().eq(StringUtils.isNotEmpty(entity.getCreateBy()), entity.getCreateBy());
            o.updateBy().eq(StringUtils.isNotEmpty(entity.getUpdateBy()), entity.getUpdateBy());
        };

    }
}
