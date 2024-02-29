package com.warm.flow.orm.dao;

import com.warm.flow.core.dao.WarmDao;
import com.warm.flow.orm.handler.DataFillHandler;
import com.warm.flow.orm.handler.DataFillHandlerFactory;
import com.warm.flow.orm.mapper.WarmMapper;
import com.warm.tools.utils.ObjectUtil;
import com.warm.tools.utils.page.Page;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * BaseMapper接口
 *
 * @author warm
 * @date 2023-03-17
 */
public abstract class WarmDaoImpl<T> implements WarmDao<T> {

    public abstract WarmMapper<T> getMapper();

    /**
     * 根据id查询
     *
     * @param id 主键
     * @return 实体
     */
    @Override
    public T selectById(Serializable id) {
        return getMapper().selectById(id);
    }


    /**
     * 根据ids查询
     *
     * @param ids 主键
     * @return 实体
     */
    @Override
    public List<T> selectByIds(Collection<? extends Serializable> ids) {
        return getMapper().selectByIds(ids);
    }

    /**
     * 分页查询
     *
     * @param entity 实体列表
     * @return 集合
     */
    @Override
    public List<T> selectList(T entity, Page<T> page, String order) {
        return getMapper().selectList(entity, page, order);
    }

    /**
     * 查询数量
     *
     * @param entity 实体列表
     * @return 集合
     */
    @Override
    public long selectCount(T entity) {
        return getMapper().selectCount(entity);
    }

    /**
     * 新增 通过继承DataFillHandler支持填充
     *
     * @param entity 实体
     * @return 结果
     */
    @Override
    public int save(T entity) {
        insertFill(entity);
        return insert(entity);
    }

    /**
     * 新增
     *
     * @param entity 实体
     * @return 结果
     */
    public int insert(T entity) {
        return getMapper().insert(entity);
    }

    /**
     * 根据id修改 通过继承DataFillHandler支持填充
     *
     * @param entity 实体
     * @return 结果
     */
    @Override
    public int modifyById(T entity) {
        updateFill(entity);
        return updateById(entity);
    }

    /**
     * 根据id修改
     *
     * @param entity 实体
     * @return 结果
     */
    public int updateById(T entity) {
        return getMapper().updateById(entity);
    }

    /**
     * 根据entity删除
     *
     * @param entity 实体
     * @return 结果
     */
    @Override
    public int delete(T entity) {
        return getMapper().delete(entity);
    }

    /**
     * 根据id删除
     *
     * @param id 主键
     * @return 结果
     */
    @Override
    public int deleteById(Serializable id) {
        return getMapper().deleteById(id);
    }

    /**
     * 根据ids批量删除
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    @Override
    public int deleteByIds(Collection<? extends Serializable> ids) {
        return getMapper().deleteByIds(ids);
    }

    public void insertFill(T entity) {
        DataFillHandler dataFillHandler = DataFillHandlerFactory.get();
        if (ObjectUtil.isNotNull(dataFillHandler)) {
            dataFillHandler.insertFill(entity);
        }
    }

    public void updateFill(T entity) {
        DataFillHandler dataFillHandler = DataFillHandlerFactory.get();
        if (ObjectUtil.isNotNull(dataFillHandler)) {
            dataFillHandler.updateFill(entity);
        }
    }
}
