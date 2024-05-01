package com.warm.flow.orm.dao;

import com.warm.flow.core.FlowFactory;
import com.warm.flow.core.dao.WarmDao;
import com.warm.flow.core.handler.DataFillHandler;
import com.warm.flow.core.orm.agent.WarmQuery;
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

    @Override
    public Page<T> selectPage(T entity, Page<T> page) {
        long total = getMapper().selectCount(entity);
        if (total > 0) {
            List<T> list = getMapper().selectList(entity, page, page.getOrderBy() + " " + page.getIsAsc());
            return new Page<>(list, total);
        }
        return Page.empty();
    }

    @Override
    public List<T> selectList(T entity, WarmQuery<T> query) {
        if (ObjectUtil.isNull(query)) {
            return getMapper().selectList(entity, null, null);
        }
        return getMapper().selectList(entity, null, query.getOrderBy() + " " + query.getIsAsc());
    }

    @Override
    public long selectCount(T entity) {
        return getMapper().selectCount(entity);
    }

    @Override
    public int save(T entity) {
        insertFill(entity);
        return insert(entity);
    }

    public int insert(T entity) {
        return getMapper().insert(entity);
    }

    @Override
    public int modifyById(T entity) {
        updateFill(entity);
        return updateById(entity);
    }

    public int updateById(T entity) {
        return getMapper().updateById(entity);
    }

    @Override
    public int delete(T entity) {
        return getMapper().delete(entity);
    }

    @Override
    public int deleteById(Serializable id) {
        return getMapper().deleteById(id);
    }

    @Override
    public int deleteByIds(Collection<? extends Serializable> ids) {
        return getMapper().deleteByIds(ids);
    }

    public void insertFill(T entity) {
        DataFillHandler dataFillHandler = FlowFactory.dataFillHandler();
        if (ObjectUtil.isNotNull(dataFillHandler)) {
            dataFillHandler.idFill(entity);
            dataFillHandler.insertFill(entity);
        }
    }

    public void updateFill(T entity) {
        DataFillHandler dataFillHandler = FlowFactory.dataFillHandler();
        if (ObjectUtil.isNotNull(dataFillHandler)) {
            dataFillHandler.updateFill(entity);
        }
    }
}
