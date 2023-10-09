package com.warm.mybatis.core.service.impl;


import com.warm.mybatis.core.agent.WarmQuery;
import com.warm.mybatis.core.invoker.MapperInvoker;
import com.warm.mybatis.core.mapper.WarmMapper;
import com.warm.mybatis.core.page.Page;
import com.warm.mybatis.core.service.IWarmService;
import com.warm.mybatis.core.utils.SqlHelper;
import com.warm.tools.utils.CollUtil;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * BaseService层处理
 *
 * @author warm
 * @date 2023-03-17
 */
public abstract class WarmServiceImpl<M extends WarmMapper<T>, T> implements IWarmService<T> {

    protected M getMapper() {
        return (M) MapperInvoker.mapperInvoker.getMapper(getMapperClass());
    }

    @Override
    public T getById(Serializable id) {
        return getMapper().selectById(id);
    }

    @Override
    public List<T> getByIds(Collection<? extends Serializable> ids) {
        return getMapper().selectByIds(ids);
    }

    @Override
    public Page<T> page(T entity, Page<T> page, String order) {
        long total = getMapper().selectCount(entity);
        if (total > 0) {
            List<T> list = getMapper().selectList(entity, page, order);
            return new Page<>(list, total);
        }
        return Page.empty();
    }

    @Override
    public List<T> list(T entity) {
        return getMapper().selectList(entity);
    }

    @Override
    public List<T> list(T entity, String order) {
        return getMapper().selectList(entity, null, order);
    }

    @Override
    public T getOne(T entity) {
        List<T> list = getMapper().selectList(entity);
        return CollUtil.getOne(list);
    }

    @Override
    public boolean save(T entity) {
        return SqlHelper.retBool(getMapper().save(entity));
    }

    @Override
    public boolean updateById(T entity) {
        return SqlHelper.retBool(getMapper().modifyById(entity));
    }

    @Override
    public boolean removeById(Serializable id) {
        return SqlHelper.retBool(getMapper().deleteById(id));
    }

    @Override
    public boolean removeByIds(Collection<? extends Serializable> ids) {
        return SqlHelper.retBool(getMapper().deleteByIds(ids));
    }

    @Override
    public void saveBatch(final List<T> list) {
        if (CollUtil.isEmpty(list)) {
            return;
        }
        for (T record : list) {
            getMapper().save(record);
        }
    }

    @Override
    public void updateBatch(final List<T> list) {
        if (CollUtil.isEmpty(list)) {
            return;
        }
        for (T record : list) {
            getMapper().modifyById(record);
        }
    }

    @Override
    public WarmQuery<T> orderByCreateTime() {
        return new WarmQuery<T>(this).orderByCreateTime();
    }

    @Override
    public WarmQuery<T> orderByUpdateTime() {
        return new WarmQuery<T>(this).orderByUpdateTime();
    }

    @Override
    public WarmQuery<T> orderByAsc(String orderByField) {
        return new WarmQuery<T>(this).orderByAsc(orderByField);
    }

    @Override
    public WarmQuery<T> orderByDesc(String orderByField) {
        return new WarmQuery<T>(this).orderByDesc(orderByField);
    }

    @Override
    public WarmQuery<T> orderBy(String orderByField) {
        return new WarmQuery<T>(this).orderBy(orderByField);
    }
}