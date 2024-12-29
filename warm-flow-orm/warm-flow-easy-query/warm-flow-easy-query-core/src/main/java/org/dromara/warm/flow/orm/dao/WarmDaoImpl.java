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

import com.easy.query.api.proxy.client.EasyEntityQuery;
import com.easy.query.core.api.pagination.EasyPageResult;
import com.easy.query.core.enums.SQLExecuteStrategyEnum;
import com.easy.query.core.expression.builder.core.NotNullOrEmptyValueFilter;
import com.easy.query.core.proxy.ProxyEntity;
import com.easy.query.core.proxy.ProxyEntityAvailable;
import org.dromara.warm.flow.core.FlowFactory;
import org.dromara.warm.flow.core.orm.dao.WarmDao;
import org.dromara.warm.flow.core.entity.RootEntity;
import org.dromara.warm.flow.core.invoker.FrameInvoker;
import org.dromara.warm.flow.core.orm.agent.WarmQuery;
import org.dromara.warm.flow.core.utils.page.Page;
import org.dromara.warm.flow.orm.request.UISort;
import org.dromara.warm.flow.orm.utils.TenantDeleteUtil;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * BaseMapper接口
 *
 * @author link2fun
 */
public abstract class WarmDaoImpl<T extends RootEntity & ProxyEntityAvailable<T, TProxy>, TProxy extends ProxyEntity<TProxy, T>>
    implements WarmDao<T> {

    protected EasyEntityQuery entityQuery(){
        return FrameInvoker.getBean(EasyEntityQuery.class);
    };

    public abstract Class<T> entityClass();


    @Override
    public T selectById(Serializable id) {
        boolean logicDelete = FlowFactory.getFlowConfig().isLogicDelete();
        return entityQuery().queryable(entityClass())
            .useLogicDelete(logicDelete)
            .whereById(id)
            .singleOrNull();
    }


    @Override
    public List<T> selectByIds(Collection<? extends Serializable> ids) {
        final boolean logicDelete = FlowFactory.getFlowConfig().isLogicDelete();
        return entityQuery().queryable(entityClass())
            .useLogicDelete(logicDelete)
            .whereByIds(ids)
            .toList();
    }

    @Override
    public Page<T> selectPage(T entity, Page<T> page) {
        final boolean logicDelete = FlowFactory.getFlowConfig().isLogicDelete();
        TenantDeleteUtil.applyContextCondition(entity);
        long total = entityQuery().queryable(entityClass())
            .filterConfigure(NotNullOrEmptyValueFilter.DEFAULT) // 忽略空值查询
            .useLogicDelete(logicDelete)
            .whereObject(entity)
            .count();
        if (total > 0) {
            UISort objectSort = UISort.of(page);
            EasyPageResult<T> pageResult = entityQuery().queryable(entityClass())
                .filterConfigure(NotNullOrEmptyValueFilter.DEFAULT) // 忽略空值查询
                .useLogicDelete(logicDelete)
                .whereObject(entity)
                .orderByObject(Objects.nonNull(objectSort), objectSort)
                .toPageResult(page.getPageNum(), page.getPageSize(), total);
            return new Page<>(pageResult.getData(), total);
        }

        return Page.empty();
    }

    @Override
    public List<T> selectList(T entity, WarmQuery<T> query) {
        TenantDeleteUtil.applyContextCondition(entity);
        final boolean logicDelete = FlowFactory.getFlowConfig().isLogicDelete();
        UISort uiSort = UISort.of(query);
        return entityQuery().queryable(entityClass())
            .filterConfigure(NotNullOrEmptyValueFilter.DEFAULT) // 忽略空值查询
            .useLogicDelete(logicDelete)
            .whereObject(entity)
            .orderByObject(Objects.nonNull(uiSort),uiSort)
            .toList();
    }

    @Override
    public long selectCount(T entity) {
        final boolean logicDelete = FlowFactory.getFlowConfig().isLogicDelete();
        TenantDeleteUtil.applyContextCondition(entity);
        return entityQuery().queryable(entityClass())
            .filterConfigure(NotNullOrEmptyValueFilter.DEFAULT) // 忽略空值查询
            .useLogicDelete(logicDelete)
            .whereObject(entity)
            .count();
    }

    @Override
    public int save(T entity) {
        TenantDeleteUtil.applyContextCondition(entity);
        return (int) entityQuery().insertable(entity).executeRows();
    }

    @Override
    public int updateById(T entity) {
        TenantDeleteUtil.applyContextCondition(entity);
        final boolean logicDelete = FlowFactory.getFlowConfig().isLogicDelete();
        return (int) entityQuery().updatable(entity)
            .useLogicDelete(logicDelete)
            .setSQLStrategy(SQLExecuteStrategyEnum.ONLY_NOT_NULL_COLUMNS) // 只更新非空字段
            .executeRows();
    }


    @Override
    public int deleteById(Serializable id) {
        final boolean logicDelete = FlowFactory.getFlowConfig().isLogicDelete();
        return (int) entityQuery().deletable(entityClass())
            .useLogicDelete(logicDelete)
            .allowDeleteStatement(!logicDelete)
            .whereById(id)
            .executeRows();
    }

    @Override
    public int deleteByIds(Collection<? extends Serializable> ids) {
        final boolean logicDelete = FlowFactory.getFlowConfig().isLogicDelete();
        return (int) entityQuery().deletable(entityClass())
            .useLogicDelete(logicDelete)
            .allowDeleteStatement(!logicDelete)
            .whereByIds(ids)
            .executeRows();
    }

    @Override
    public void saveBatch(List<T> list) {
        for (T record : list) {
            TenantDeleteUtil.applyContextCondition(record);
        }
        entityQuery().insertable(list)
            .batch()
            .executeRows();

    }

    @Override
    public void updateBatch(List<T> list) {
        for (T record : list) {
            TenantDeleteUtil.applyContextCondition(record);
        }
        final boolean logicDelete = FlowFactory.getFlowConfig().isLogicDelete();
        entityQuery().updatable(list)
            .useLogicDelete(logicDelete)
            .batch()
            .executeRows();

    }


}
