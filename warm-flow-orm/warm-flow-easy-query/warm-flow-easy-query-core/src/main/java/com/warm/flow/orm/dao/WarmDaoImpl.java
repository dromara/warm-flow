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
package com.warm.flow.orm.dao;

import com.easy.query.api.proxy.client.EasyEntityQuery;
import com.easy.query.core.api.pagination.EasyPageResult;
import com.easy.query.core.enums.SQLExecuteStrategyEnum;
import com.easy.query.core.expression.builder.core.NotNullOrEmptyValueFilter;
import com.easy.query.core.proxy.ProxyEntity;
import com.easy.query.core.proxy.ProxyEntityAvailable;
import com.warm.flow.core.FlowFactory;
import com.warm.flow.core.dao.WarmDao;
import com.warm.flow.core.entity.RootEntity;
import com.warm.flow.core.invoker.FrameInvoker;
import com.warm.flow.core.orm.agent.WarmQuery;
import com.warm.flow.core.utils.page.Page;
import com.warm.flow.orm.request.UISort;
import com.warm.flow.orm.utils.TenantDeleteUtil;

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
        return entityQuery().queryable(entityClass())
            .whereById(id)
            .singleOrNull();
    }


    @Override
    public List<T> selectByIds(Collection<? extends Serializable> ids) {
        return entityQuery().queryable(entityClass())
            .whereByIds(ids)
            .toList();
    }

    @Override
    public Page<T> selectPage(T entity, Page<T> page) {
        TenantDeleteUtil.getEntity(entity);
        long total = entityQuery().queryable(entityClass())
            .filterConfigure(NotNullOrEmptyValueFilter.DEFAULT) // 忽略空值查询
            .whereObject(entity)
            .count();
        if (total > 0) {
            EasyPageResult<T> pageResult = entityQuery().queryable(entityClass())
                .filterConfigure(NotNullOrEmptyValueFilter.DEFAULT) // 忽略空值查询
                .whereObject(entity)
                .orderByObject(UISort.of(page))
                .toPageResult(page.getPageNum(), page.getPageSize(), total);
            return new Page<>(pageResult.getData(), total);
        }

        return Page.empty();
    }

    @Override
    public List<T> selectList(T entity, WarmQuery<T> query) {
        TenantDeleteUtil.getEntity(entity);

        UISort uiSort = UISort.of(query);
        return entityQuery().queryable(entityClass())
            .filterConfigure(NotNullOrEmptyValueFilter.DEFAULT) // 忽略空值查询
            .whereObject(entity)
            .orderByObject(Objects.nonNull(uiSort),uiSort)
            .toList();
    }

    @Override
    public long selectCount(T entity) {
        TenantDeleteUtil.getEntity(entity);
        return entityQuery().queryable(entityClass())
            .filterConfigure(NotNullOrEmptyValueFilter.DEFAULT) // 忽略空值查询
            .whereObject(entity)
            .count();
    }

    @Override
    public int save(T entity) {
        TenantDeleteUtil.getEntity(entity);
        return (int) entityQuery().insertable(entity).executeRows();
    }

    @Override
    public int updateById(T entity) {
        TenantDeleteUtil.getEntity(entity);
        return (int) entityQuery().updatable(entity)
            .setSQLStrategy(SQLExecuteStrategyEnum.ONLY_NOT_NULL_COLUMNS) // 只更新非空字段
            .executeRows();
    }


    @Override
    public int deleteById(Serializable id) {
        if (FlowFactory.getFlowConfig().isLogicDelete()) {
            return (int) entityQuery().getEasyQueryClient().updatable(entityClass())
                .set("del_flag", FlowFactory.getFlowConfig().getLogicDeleteValue())
                .whereById(id)
                .executeRows();
        }

        return (int) entityQuery().deletable(entityClass())
            .allowDeleteStatement(true)
            .whereById(id)
            .executeRows();
    }

    @Override
    public int deleteByIds(Collection<? extends Serializable> ids) {
        if (FlowFactory.getFlowConfig().isLogicDelete()) {
           return (int) entityQuery().getEasyQueryClient().updatable(entityClass())
                .set("del_flag", FlowFactory.getFlowConfig().getLogicDeleteValue())
                .whereByIds(ids)
                .executeRows();
        }
        return (int) entityQuery().deletable(entityClass())
            .allowDeleteStatement(true)
            .whereByIds(ids)
            .executeRows();
    }

    @Override
    public void saveBatch(List<T> list) {
        for (T record : list) {
            TenantDeleteUtil.getEntity(record);
        }
        entityQuery().insertable(list)
            .batch()
            .executeRows();

    }

    @Override
    public void updateBatch(List<T> list) {
        for (T record : list) {
            TenantDeleteUtil.getEntity(record);
        }
        entityQuery().updatable(list)
            .batch()
            .executeRows();

    }


}
