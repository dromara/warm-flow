package com.monkey.mybatis.core.service.impl;


import com.monkey.mybatis.core.agent.FlowQuery;
import com.monkey.mybatis.core.entity.FlowEntity;
import com.monkey.mybatis.core.mapper.FlowMapper;
import com.monkey.mybatis.core.page.Page;
import com.monkey.mybatis.core.service.IFlowService;
import com.monkey.mybatis.core.utils.SqlHelper;
import com.monkey.tools.utils.CollUtil;
import com.monkey.tools.utils.IdUtils;
import com.monkey.tools.utils.ObjectUtil;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * BaseService层处理
 *
 * @author hh
 * @date 2023-03-17
 */
public abstract class FlowServiceImpl<T extends FlowEntity> implements IFlowService<T> {

    protected abstract FlowMapper<T> getBaseMapper();

    /**
     * 根据id查询
     *
     * @param id 主键
     * @return 实体
     */
    @Override
    public T getById(Serializable id) {
        return getBaseMapper().selectById(id);
    }

    /**
     * 根据ids查询
     *
     * @param ids 主键
     * @return 实体
     */
    @Override
    public List<T> getByIds(Collection<? extends Serializable> ids) {
        return getBaseMapper().selectByIds(ids);
    }

    /**
     * 分页查询并且排序
     *
     * @param entity 实体列表
     * @return 集合
     */
    @Override
    public Page<T> page(T entity, Page<T> page, String order) {
        long total = getBaseMapper().selectCount(entity);
        if (total > 0) {
            List<T> list = getBaseMapper().selectList(entity, page, order);
            return new Page<>(list, total);
        }
        return Page.empty();
    }

    /**
     * 查询列表
     *
     * @param entity 实体列表
     * @return 集合
     */
    @Override
    public List<T> list(T entity) {
        return getBaseMapper().selectList(entity);
    }

    /**
     * 查询列表并排序
     *
     * @param entity 实体列表
     * @return 集合
     */
    @Override
    public List<T> list(T entity, String order) {
        return getBaseMapper().selectList(entity, null, order);
    }

    /**
     * 查询一条记录
     *
     * @param entity 实体列表
     * @return 结果
     */
    @Override
    public T getOne(T entity) {
        List<T> list = getBaseMapper().selectList(entity);
        return CollUtil.isEmpty(list) ? null: list.get(0);
    }

    /**
     * 新增
     *
     * @param entity 实体
     * @return 结果
     */
    @Override
    public boolean save(T entity) {
        insertFill(entity);
        return SqlHelper.retBool(getBaseMapper().insert(entity));
    }

    /**
     * 根据id修改
     *
     * @param entity 实体
     * @return 结果
     */
    @Override
    public boolean updateById(T entity) {
        updateFill(entity);
        return SqlHelper.retBool(getBaseMapper().updateById(entity));
    }

    /**
     * 根据id删除
     *
     * @param id 主键
     * @return 结果
     */
    @Override
    public boolean removeById(Serializable id) {
        return SqlHelper.retBool(getBaseMapper().deleteById(id));
    }

    /**
     * 根据ids批量删除
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    @Override
    public boolean removeByIds(Collection<? extends Serializable> ids) {
        return SqlHelper.retBool(getBaseMapper().deleteByIds(ids));
    }

    /**
     * 批量新增
     *
     * @param list
     */
    @Override
    public void saveBatch(final List<T> list) {
        if (CollUtil.isEmpty(list)) {
            return;
        }
        for (T record : list) {
            insertFill(record);
            getBaseMapper().insert(record);
        }
    }

    /**
     * 批量更新
     *
     * @param list
     */
    @Override
    public void updateBatch(final List<T> list) {
        if (CollUtil.isEmpty(list)) {
            return;
        }
        for (T record : list) {
            insertFill(record);
            getBaseMapper().updateById(record);
        }
    }

    /**
     * 创建时间设置正序排列
     *
     * @return 集合
     */
    @Override
    public FlowQuery orderByCreateTime() {
        FlowQuery flowQuery = new FlowQuery(this);
        flowQuery.orderByCreateTime();
        return flowQuery;
    }

    /**
     * 更新时间设置正序排列
     *
     * @return 集合
     */
    public FlowQuery orderByUpdateTime() {
        FlowQuery flowQuery = new FlowQuery(this);
        flowQuery.orderByUpdateTime();
        return flowQuery;
    }

    /**
     * 设置正序排列
     *
     * @return 集合
     */
    public FlowQuery desc() {
        FlowQuery flowQuery = new FlowQuery(this);
        flowQuery.desc();
        return flowQuery;
    }

    /**
     * 设置正序排列
     *
     * @param orderByField 排序字段
     * @return 集合
     */
    public FlowQuery orderByAsc(String orderByField) {
        FlowQuery flowQuery = new FlowQuery(this);
        flowQuery.orderByAsc(orderByField);
        return flowQuery;
    }

    /**
     * 设置倒序排列
     *
     * @param orderByField 排序字段
     * @return 集合
     */
    public FlowQuery orderByDesc(String orderByField) {
        FlowQuery flowQuery = new FlowQuery(this);
        flowQuery.orderByDesc(orderByField);
        return flowQuery;
    }

    /**
     * 用户自定义排序方案
     *
     * @param orderByField 排序字段
     * @return 集合
     */
    public FlowQuery orderBy(String orderByField) {
        FlowQuery flowQuery = new FlowQuery(this);
        flowQuery.orderBy(orderByField);
        return flowQuery;
    }

    private void insertFill(T entity) {
        if (ObjectUtil.isNotNull(entity)) {
            if (Objects.isNull(entity.getId()))
            {
                entity.setId(IdUtils.nextId());
            }
            Date date = ObjectUtil.isNotNull(entity.getCreateTime())
                    ? entity.getCreateTime() : new Date();
            entity.setCreateTime(date);
            entity.setUpdateTime(date);
        }
    }

    private void updateFill(T entity) {
        if (ObjectUtil.isNotNull(entity)) {
            entity.setUpdateTime(new Date());
        }
    }

}
