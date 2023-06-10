package com.monkey.mybatis.core.service;


import com.monkey.mybatis.core.entity.FlowEntity;
import com.monkey.mybatis.core.page.PageResult;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * Service接口
 *
 * @author hh
 * @date 2023-03-17
 */
public interface IFlowBaseService<T extends FlowEntity>
{

    /**
     * 根据id查询
     *
     * @param id 主键
     * @return 实体
     */
    T selectById(Serializable id);

    /**
     * 根据ids查询
     *
     * @param ids 主键
     * @return 实体
     */
    List<T> selectByIds(Collection<? extends Serializable> ids);

    /**
     * 分页查询
     *
     * @param entity 实体
     * @return 集合
     */
    PageResult<T> selectPage(T entity);

    /**
     * 查询列表
     *
     * @param entity 实体列表
     * @return 集合
     */
    List<T> selectList(T entity);

    /**
     * 查询一条记录
     *
     * @param entity 实体列表
     * @return 结果
     */
    T selectOne(T entity);

    /**
     * 新增
     *
     * @param entity 实体
     * @return 结果
     */
    boolean insert(T entity);

    /**
     * 新增
     * @param entity
     * @return
     */
    boolean save(T entity);

    /**
     * 根据id修改
     *
     * @param entity 实体
     * @return 结果
     */
    boolean updateById(T entity);

    /**
     * 先校验后修改
     *
     * @param entity 实体
     * @return 结果
     */
    boolean edit(T entity);

    /**
     * 根据id删除
     *
     * @param id 主键
     * @return 结果
     */
    boolean deleteById(Serializable id);

    /**
     * 根据ids批量删除
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    boolean deleteByIds(Collection<? extends Serializable> ids);

    /**
     * 批量新增
     * @param list
     */
    void batchInsert(List<T> list);

    /**
     * 批量更新
     * @param list
     */
    void batchUpdate(List<T> list);

}
