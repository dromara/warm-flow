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
package org.dromara.warm.flow.core.orm.service.impl;


import org.dromara.warm.flow.core.FlowEngine;
import org.dromara.warm.flow.core.orm.dao.WarmDao;
import org.dromara.warm.flow.core.handler.DataFillHandler;
import org.dromara.warm.flow.core.orm.agent.WarmQuery;
import org.dromara.warm.flow.core.orm.service.IWarmService;
import org.dromara.warm.flow.core.utils.CollUtil;
import org.dromara.warm.flow.core.utils.ObjectUtil;
import org.dromara.warm.flow.core.utils.SqlHelper;
import org.dromara.warm.flow.core.utils.page.Page;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * BaseService层处理
 *
 * @author warm
 * @since 2023-03-17
 */
public abstract class WarmServiceImpl<M extends WarmDao<T>, T> implements IWarmService<T> {

    protected M warmDao;

    public M getDao() {
        return warmDao;
    }

    protected abstract IWarmService<T> setDao(M warmDao);

    @Override
    public T getById(Serializable id) {
        return getDao().selectById(id);
    }

    @Override
    public List<T> getByIds(Collection<? extends Serializable> ids) {
        return getDao().selectByIds(ids);
    }

    @Override
    public Page<T> page(T entity, Page<T> page) {
        return getDao().selectPage(entity, page);
    }

    @Override
    public List<T> list(T entity) {
        return getDao().selectList(entity, null);
    }

    @Override
    public List<T> list(T entity, WarmQuery<T> query) {
        return getDao().selectList(entity, query);
    }

    @Override
    public T getOne(T entity) {
        List<T> list = getDao().selectList(entity, null);
        return CollUtil.getOne(list);
    }

    @Override
    public long selectCount(T entity) {
        return getDao().selectCount(entity);
    }

    @Override
    public Boolean exists(T entity) {
        long count = selectCount(entity);
        return count > 0;
    }

    @Override
    public boolean save(T entity) {
        insertFill(entity);
        return SqlHelper.retBool(getDao().save(entity));
    }

    @Override
    public boolean updateById(T entity) {
        updateFill(entity);
        return SqlHelper.retBool(getDao().updateById(entity));
    }

    @Override
    public boolean removeById(Serializable id) {
        return SqlHelper.retBool(getDao().deleteById(id));
    }

    @Override
    public boolean remove(T entity) {
        return SqlHelper.retBool(getDao().delete(entity));
    }

    @Override
    public boolean removeByIds(Collection<? extends Serializable> ids) {
        return SqlHelper.retBool(getDao().deleteByIds(ids));
    }

    @Override
    public void saveBatch(List<T> list) {
        if (CollUtil.isEmpty(list)) {
            return;
        }
        this.saveBatch(list, 1000);
    }

    @Override
    public void saveBatch(List<T> list, int batchSize) {
        if (CollUtil.isEmpty(list)) {
            return;
        }

        List<List<T>> split = CollUtil.split(list, batchSize > 0 ? batchSize : 1000);

        for (List<T> ts : split) {
            ts.forEach(this::insertFill);
            getDao().saveBatch(ts);
        }
    }

    @Override
    public void updateBatch(List<T> list) {
        if (CollUtil.isEmpty(list)) {
            return;
        }
        list.forEach(this::updateFill);
        getDao().updateBatch(list);
    }

    @Override
    public WarmQuery<T> orderById() {
        return new WarmQuery<>(this).orderById();
    }

    @Override
    public WarmQuery<T> orderByCreateTime() {
        return new WarmQuery<>(this).orderByCreateTime();
    }

    @Override
    public WarmQuery<T> orderByUpdateTime() {
        return new WarmQuery<>(this).orderByUpdateTime();
    }

    @Override
    public WarmQuery<T> orderByAsc(String orderByField) {
        return new WarmQuery<>(this).orderByAsc(orderByField);
    }

    @Override
    public WarmQuery<T> orderByDesc(String orderByField) {
        return new WarmQuery<>(this).orderByDesc(orderByField);
    }

    @Override
    public WarmQuery<T> orderBy(String orderByField) {
        return new WarmQuery<>(this).orderBy(orderByField);
    }


    public void insertFill(T entity) {
        DataFillHandler dataFillHandler = FlowEngine.dataFillHandler();
        if (ObjectUtil.isNotNull(dataFillHandler)) {
            dataFillHandler.idFill(entity);
            dataFillHandler.insertFill(entity);
        }
    }

    public void updateFill(T entity) {
        DataFillHandler dataFillHandler = FlowEngine.dataFillHandler();
        if (ObjectUtil.isNotNull(dataFillHandler)) {
            dataFillHandler.updateFill(entity);
        }
    }
}
