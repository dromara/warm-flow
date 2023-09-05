package com.warm.mybatis.core.service.impl;


import com.warm.mybatis.core.agent.FlowQuery;
import com.warm.mybatis.core.entity.FlowEntity;
import com.warm.mybatis.core.handler.DataFillHandler;
import com.warm.mybatis.core.handler.DataFillHandlerFactory;
import com.warm.mybatis.core.invoker.MapperInvoker;
import com.warm.mybatis.core.mapper.FlowMapper;
import com.warm.mybatis.core.page.Page;
import com.warm.mybatis.core.service.IFlowService;
import com.warm.mybatis.core.utils.SqlHelper;
import com.warm.tools.utils.CollUtil;
import com.warm.tools.utils.ObjectUtil;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * BaseService层处理
 *
 * @author hh
 * @date 2023-03-17
 */
public abstract class FlowServiceImpl<M extends FlowMapper<T>, T extends FlowEntity> implements IFlowService<T> {

    protected Class<M> mapperClass() {
        Type superClassType = this.getClass().getGenericSuperclass();
        ParameterizedType pt = (ParameterizedType)superClassType;

        Type[] genTypeArr = pt.getActualTypeArguments();
        Type genType = genTypeArr[0];
        try {
            Class<?> aClass = Class.forName(genType.getTypeName());
            return (Class<M>) aClass;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 执行mapper方法
     *
     * @param function
     */
    public <R> R have(Function<M, R> function) {
        return MapperInvoker.have(function, mapperClass());
    }

    /**
     * 执行mapper方法
     *
     * @param consumer
     */
    public void noHave(Consumer<M> consumer) {
        MapperInvoker.noHave(consumer, mapperClass());
    }

    /**
     * 根据id查询
     *
     * @param id 主键
     * @return 实体
     */
    @Override
    public T getById(Serializable id) {
        return MapperInvoker.have(baseMapper -> baseMapper.selectById(id), mapperClass());
    }

    /**
     * 根据ids查询
     *
     * @param ids 主键
     * @return 实体
     */
    @Override
    public List<T> getByIds(Collection<? extends Serializable> ids) {
        return MapperInvoker.have(baseMapper -> baseMapper.selectByIds(ids), mapperClass());
    }

    /**
     * 分页查询并且排序
     *
     * @param entity 实体列表
     * @return 集合
     */
    @Override
    public Page<T> page(T entity, Page<T> page, String order) {
        long total = MapperInvoker.have(baseMapper -> baseMapper.selectCount(entity), mapperClass());
        if (total > 0) {
            List<T> list = MapperInvoker.have(baseMapper -> baseMapper.selectList(entity, page, order), mapperClass());
            ;
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
        return MapperInvoker.have(baseMapper -> baseMapper.selectList(entity), mapperClass());
    }

    /**
     * 查询列表并排序
     *
     * @param entity 实体列表
     * @return 集合
     */
    @Override
    public List<T> list(T entity, String order) {
        return MapperInvoker.have(baseMapper -> baseMapper.selectList(entity, null, order), mapperClass());
    }

    /**
     * 查询一条记录
     *
     * @param entity 实体列表
     * @return 结果
     */
    @Override
    public T getOne(T entity) {
        List<T> list = MapperInvoker.have(baseMapper -> baseMapper.selectList(entity), mapperClass());
        return CollUtil.isEmpty(list) ? null : list.get(0);
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
        Integer result = MapperInvoker.have(baseMapper -> baseMapper.insert(entity), mapperClass());
        return SqlHelper.retBool(result);
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
        Integer result = MapperInvoker.have(baseMapper -> baseMapper.updateById(entity), mapperClass());
        return SqlHelper.retBool(result);
    }

    /**
     * 根据id删除
     *
     * @param id 主键
     * @return 结果
     */
    @Override
    public boolean removeById(Serializable id) {
        Integer result = MapperInvoker.have(baseMapper -> baseMapper.deleteById(id), mapperClass());
        return SqlHelper.retBool(result);
    }

    /**
     * 根据ids批量删除
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    @Override
    public boolean removeByIds(Collection<? extends Serializable> ids) {
        Integer result = MapperInvoker.have(baseMapper -> baseMapper.deleteByIds(ids), mapperClass());
        return SqlHelper.retBool(result);
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
            MapperInvoker.have(baseMapper -> baseMapper.insert(record), mapperClass());
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
            MapperInvoker.have(baseMapper -> baseMapper.updateById(record), mapperClass());
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
        DataFillHandler dataFillHandler = DataFillHandlerFactory.get();
        if (ObjectUtil.isNotNull(dataFillHandler)) {
            dataFillHandler.insertFill(entity);
        }
    }

    private void updateFill(T entity) {
        DataFillHandler dataFillHandler = DataFillHandlerFactory.get();
        if (ObjectUtil.isNotNull(dataFillHandler)) {
            dataFillHandler.updateFill(entity);
        }
    }

}