package com.warm.flow.orm.dao;

import com.warm.flow.core.FlowFactory;
import com.warm.flow.core.dao.WarmDao;
import com.warm.flow.core.entity.RootEntity;
import com.warm.flow.core.handler.DataFillHandler;
import com.warm.flow.core.orm.agent.WarmQuery;
import com.warm.flow.orm.utils.TenantDeleteUtil;
import com.warm.tools.utils.ObjectUtil;
import com.warm.tools.utils.StringUtils;
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
public abstract class WarmDaoImpl<T extends RootEntity> implements WarmDao<T> {

    public abstract T newEntity();

    /**
     * 根据id查询
     *
     * @param id 主键
     * @return 实体
     */
    @Override
    public T selectById(Serializable id) {
        //return getMapper().selectById(id, TenantDeleteUtil.getEntity(newEntity()));
        return null;
    }

    /**
     * 根据ids查询
     *
     * @param ids 主键
     * @return 实体
     */
    @Override
    public List<T> selectByIds(Collection<? extends Serializable> ids) {
        //return getMapper().selectByIds(ids, TenantDeleteUtil.getEntity(newEntity()));
        return null;
    }

    @Override
    public Page<T> selectPage(T entity, Page<T> page) {
        /*TenantDeleteUtil.getEntity(entity);
        long total = getMapper().selectCount(entity);
        if (total > 0) {
            List<T> list = getMapper().selectList(entity, page, page.getOrderBy() + " " + page.getIsAsc());
            return new Page<>(list, total);
        }
        return Page.empty();*/
        return null;
    }

    @Override
    public List<T> selectList(T entity, WarmQuery<T> query) {
        /*TenantDeleteUtil.getEntity(entity);
        if (ObjectUtil.isNull(query)) {
            return getMapper().selectList(entity, null, null);
        }
        return getMapper().selectList(entity, null, query.getOrderBy() + " " + query.getIsAsc());*/
        return null;
    }

    @Override
    public long selectCount(T entity) {
        /*TenantDeleteUtil.getEntity(entity);
        return getMapper().selectCount(entity);*/
        return 0;
    }

    @Override
    public int save(T entity) {
       /* insertFill(entity);
        return insert(entity);*/
        return 0;
    }

    public int insert(T entity) {
     /*   TenantDeleteUtil.getEntity(entity);
        return getMapper().insert(entity);*/

        return 0;
    }

    @Override
    public int modifyById(T entity) {
       /* updateFill(entity);
        return updateById(entity);*/

        return 0;
    }

    public int updateById(T entity) {
        /*TenantDeleteUtil.getEntity(entity);
        return getMapper().updateById(entity);*/
        return 0;
    }

    @Override
    public int delete(T entity) {
        /*TenantDeleteUtil.getEntity(entity);
        if (StringUtils.isNotEmpty(entity.getDelFlag())) {
            getMapper().updateLogic(entity, FlowFactory.getFlowConfig().getLogicDeleteValue(), entity.getDelFlag());
        }
        return getMapper().delete(entity);*/
        return 0;
    }

    @Override
    public int deleteById(Serializable id) {
       /* T entity = TenantDeleteUtil.getEntity(newEntity());
        if (StringUtils.isNotEmpty(entity.getDelFlag())) {
            getMapper().updateByIdLogic(id, entity, FlowFactory.getFlowConfig().getLogicDeleteValue(), entity.getDelFlag());
        }
        return getMapper().deleteById(id, entity);*/
        return 0;
    }

    @Override
    public int deleteByIds(Collection<? extends Serializable> ids) {
       /* T entity = TenantDeleteUtil.getEntity(newEntity());
        if (StringUtils.isNotEmpty(entity.getDelFlag())) {
            getMapper().updateByIdsLogic(ids, entity, FlowFactory.getFlowConfig().getLogicDeleteValue(), entity.getDelFlag());
        }
        return getMapper().deleteByIds(ids, entity);*/
        return 0;
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
