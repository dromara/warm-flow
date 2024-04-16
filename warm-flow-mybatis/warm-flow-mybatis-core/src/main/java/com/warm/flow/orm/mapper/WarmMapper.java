package com.warm.flow.orm.mapper;

import com.warm.tools.utils.page.Page;
import org.apache.ibatis.annotations.Param;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * BaseMapper接口
 *
 * @author warm
 * @date 2023-03-17
 */
public interface WarmMapper<T> {

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
     * 查询列表
     *
     * @param entity 实体列表
     * @return 集合
     */
    default List<T> selectList(@Param("entity") T entity) {
        return selectList(entity, null);
    }

    /**
     * 分页查询
     *
     * @param entity 实体列表
     * @return 集合
     */
    default List<T> selectList(@Param("entity") T entity, @Param("page") Page<T> page) {
        return selectList(entity, page, null);
    }

    /**
     * 分页查询
     *
     * @param entity 实体列表
     * @return 集合
     */
    List<T> selectList(@Param("entity") T entity, @Param("page") Page<T> page, @Param("order") String order);

    /**
     * 查询数量
     *
     * @param entity 实体列表
     * @return 集合
     */
    long selectCount(@Param("entity") T entity);

    /**
     * 新增
     *
     * @param entity 实体
     * @return 结果
     */
    int insert(T entity);

    /**
     * 根据id修改
     *
     * @param entity 实体
     * @return 结果
     */
    int updateById(T entity);

    /**
     * 根据entity删除
     *
     * @param entity 实体
     * @return 结果
     */
    int delete(T entity);

    /**
     * 根据id删除
     *
     * @param id 主键
     * @return 结果
     */
    int deleteById(Serializable id);

    /**
     * 根据ids批量删除
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    int deleteByIds(Collection<? extends Serializable> ids);

}
