package com.warm.flow.orm.dao;

import com.warm.flow.core.FlowFactory;
import com.warm.flow.core.dao.WarmDao;
import com.warm.flow.core.entity.RootEntity;
import com.warm.flow.core.handler.DataFillHandler;
import com.warm.flow.core.orm.agent.WarmQuery;
import com.warm.flow.core.utils.ObjectUtil;
import com.warm.flow.core.utils.StringUtils;
import com.warm.flow.core.utils.page.Page;
import com.warm.flow.orm.mapper.WarmMapper;
import com.warm.flow.orm.utils.TenantDeleteUtil;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * BaseMapper接口
 *
 * @author warm
 * @date 2023-03-17
 */
public abstract class WarmDaoImpl<T extends RootEntity> implements WarmDao<T> {

    public abstract WarmMapper<T> getMapper();

    public abstract T newEntity();

    /**
     * 根据id查询
     *
     * @param id 主键
     * @return 实体
     */
    @Override
    public T selectById(Serializable id) {
        return getMapper().selectById(id, TenantDeleteUtil.getEntity(newEntity()));
    }

    /**
     * 根据ids查询
     *
     * @param ids 主键
     * @return 实体
     */
    @Override
    public List<T> selectByIds(Collection<? extends Serializable> ids) {
        return getMapper().selectByIds(ids, TenantDeleteUtil.getEntity(newEntity()));
    }

    @Override
    public Page<T> selectPage(T entity, Page<T> page) {
        TenantDeleteUtil.getEntity(entity);
        String dataSourceType = FlowFactory.dataSourceType();
        page.ifNecessaryChangePage(dataSourceType);
        long total = getMapper().selectCount(entity);
        if (total > 0) {
            List<T> list = getMapper().selectList(entity, page, page.getOrderBy() + " " + page.getIsAsc(), dataSourceType);
            return new Page<>(list, total);
        }
        return Page.empty();
    }

    @Override
    public List<T> selectList(T entity, WarmQuery<T> query) {
        TenantDeleteUtil.getEntity(entity);
        String dataSourceType = FlowFactory.dataSourceType();
        if (ObjectUtil.isNull(query)) {
            return getMapper().selectList(entity, null, null, dataSourceType);
        }
        return getMapper().selectList(entity, null, query.getOrderBy() + " " + query.getIsAsc(), dataSourceType);
    }

    @Override
    public long selectCount(T entity) {
        TenantDeleteUtil.getEntity(entity);
        return getMapper().selectCount(entity);
    }

    @Override
    public int save(T entity) {
        insertFill(entity);
        return insert(entity);
    }

    public int insert(T entity) {
        TenantDeleteUtil.getEntity(entity);
        return getMapper().insert(entity);
    }

    @Override
    public int modifyById(T entity) {
        updateFill(entity);
        return updateById(entity);
    }

    public int updateById(T entity) {
        TenantDeleteUtil.getEntity(entity);
        return getMapper().updateById(entity);
    }

    @Override
    public int delete(T entity) {
        TenantDeleteUtil.getEntity(entity);
        if (StringUtils.isNotEmpty(entity.getDelFlag())) {
            return getMapper().updateLogic(entity, FlowFactory.getFlowConfig().getLogicDeleteValue(), entity.getDelFlag());
        }
        return getMapper().delete(entity);
    }

    @Override
    public int deleteById(Serializable id) {
        T entity = TenantDeleteUtil.getEntity(newEntity());
        if (StringUtils.isNotEmpty(entity.getDelFlag())) {
            return getMapper().updateByIdLogic(id, entity, FlowFactory.getFlowConfig().getLogicDeleteValue(), entity.getDelFlag());
        }
        return getMapper().deleteById(id, entity);
    }

    @Override
    public int deleteByIds(Collection<? extends Serializable> ids) {
        T entity = TenantDeleteUtil.getEntity(newEntity());
        if (StringUtils.isNotEmpty(entity.getDelFlag())) {
            return getMapper().updateByIdsLogic(ids, entity, FlowFactory.getFlowConfig().getLogicDeleteValue(), entity.getDelFlag());
        }
        return getMapper().deleteByIds(ids, entity);
    }

    @Override
    public void saveBatch(List<T> list) {
        for (T record : list) {
            insert(record);
        }
    }

    @Override
    public void updateBatch(List<T> list) {
        for (T record : list) {
            modifyById(record);
        }
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
