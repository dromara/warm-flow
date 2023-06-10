package com.monkey.mybatis.core.service.impl;


import com.monkey.mybatis.core.entity.FlowEntity;
import com.monkey.mybatis.core.mapper.FlowBaseMapper;
import com.monkey.mybatis.core.page.PageResult;
import com.monkey.mybatis.core.service.IFlowBaseService;
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
public abstract class FlowBaseServiceImpl<T extends FlowEntity> implements IFlowBaseService<T> {

    protected abstract FlowBaseMapper<T> getBaseMapper();

    /**
     * 根据id查询
     *
     * @param id 主键
     * @return 实体
     */
    @Override
    public T selectById(Serializable id) {
        return getBaseMapper().selectById(id);
    }

    /**
     * 根据ids查询
     *
     * @param ids 主键
     * @return 实体
     */
    @Override
    public List<T> selectByIds(Collection<? extends Serializable> ids) {
        return getBaseMapper().selectByIds(ids);
    }

    /**
     * 查询列表
     *
     * @param entity 实体列表
     * @return 集合
     */
    @Override
    public PageResult<T> selectPage(T entity) {
        long total = getBaseMapper().selectCount(entity);
        if (total > 0) {
            List<T> list = getBaseMapper().selectPage(entity);
            return new PageResult<>(list, total);
        }
        return PageResult.empty();
    }

    /**
     * 查询列表
     *
     * @param entity 实体列表
     * @return 集合
     */
    @Override
    public List<T> selectList(T entity) {
        return getBaseMapper().selectList(entity);
    }

    /**
     * 查询一条记录
     *
     * @param entity 实体列表
     * @return 结果
     */
    @Override
    public T selectOne(T entity) {
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
    public boolean insert(T entity) {
        insertFill(entity);
        return SqlHelper.retBool(getBaseMapper().insert(entity));
    }

    /**
     * 先校验后修改
     *
     * @param entity 实体
     * @return 结果
     */
    @Override
    public boolean save(T entity) {

        checkSave(entity);
        return insert(entity);
    }

    /**
     * 新增前校验
     * @param entity
     */
    protected void checkSave(T entity)
    {

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
     * 先校验后修改
     *
     * @param entity 实体
     * @return 结果
     */
    @Override
    public boolean edit(T entity)
    {
        checkEdit(entity);
        return updateById(entity);
    }

    public void checkEdit(T entity)
    {

    }

    /**
     * 根据id删除
     *
     * @param id 主键
     * @return 结果
     */
    @Override
    public boolean deleteById(Serializable id) {
        return SqlHelper.retBool(getBaseMapper().deleteById(id));
    }

    /**
     * 根据ids批量删除
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    @Override
    public boolean deleteByIds(Collection<? extends Serializable> ids) {
        return SqlHelper.retBool(getBaseMapper().deleteByIds(ids));
    }

    /**
     * 批量新增
     *
     * @param list
     */
    @Override
    public void batchInsert(final List<T> list) {
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
    public void batchUpdate(final List<T> list) {
        if (CollUtil.isEmpty(list)) {
            return;
        }
        for (T record : list) {
            insertFill(record);
            getBaseMapper().updateById(record);
        }
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
