/*
 *    Copyright 2024-2025, Warm-Flow (290631660@qq.com).
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.dromara.warm.flow.orm.dao;

import org.dromara.warm.flow.core.FlowEngine;
import org.dromara.warm.flow.core.orm.dao.WarmDao;
import org.dromara.warm.flow.core.entity.RootEntity;
import org.dromara.warm.flow.core.orm.agent.WarmQuery;
import org.dromara.warm.flow.core.utils.ObjectUtil;
import org.dromara.warm.flow.core.utils.StringUtils;
import org.dromara.warm.flow.core.utils.page.Page;
import org.dromara.warm.flow.orm.mapper.WarmMapper;
import org.dromara.warm.flow.orm.utils.TenantDeleteUtil;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * BaseMapper接口
 *
 * @author warm
 * @since 2023-03-17
 */
public abstract class WarmDaoImpl<T extends RootEntity> implements WarmDao<T> {

    public abstract WarmMapper<T> getMapper();

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
        String dataSourceType = FlowEngine.dataSourceType();
        ifNecessaryChangePage(page, dataSourceType);
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
        String dataSourceType = FlowEngine.dataSourceType();
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
        TenantDeleteUtil.getEntity(entity);
        return getMapper().insert(entity);
    }

    @Override
    public int updateById(T entity) {
        TenantDeleteUtil.getEntity(entity);
        return getMapper().updateById(entity);
    }

    @Override
    public int delete(T entity) {
        TenantDeleteUtil.getEntity(entity);
        if (StringUtils.isNotEmpty(entity.getDelFlag())) {
            return getMapper().updateLogic(entity, FlowEngine.getFlowConfig().getLogicDeleteValue(), entity.getDelFlag());
        }
        return getMapper().delete(entity);
    }

    @Override
    public int deleteById(Serializable id) {
        T entity = TenantDeleteUtil.getEntity(newEntity());
        if (StringUtils.isNotEmpty(entity.getDelFlag())) {
            return getMapper().updateByIdLogic(id, entity, FlowEngine.getFlowConfig().getLogicDeleteValue(), entity.getDelFlag());
        }
        return getMapper().deleteById(id, entity);
    }

    @Override
    public int deleteByIds(Collection<? extends Serializable> ids) {
        T entity = TenantDeleteUtil.getEntity(newEntity());
        if (StringUtils.isNotEmpty(entity.getDelFlag())) {
            return getMapper().updateByIdsLogic(ids, entity, FlowEngine.getFlowConfig().getLogicDeleteValue(), entity.getDelFlag());
        }
        return getMapper().deleteByIds(ids, entity);
    }

    @Override
    public void saveBatch(List<T> list) {
        for (T record : list) {
            TenantDeleteUtil.getEntity(record);
        }
        String dataSourceType = FlowEngine.dataSourceType();
        getMapper().saveBatch(list, dataSourceType);
    }

    @Override
    public void updateBatch(List<T> list) {
        for (T record : list) {
            updateById(record);
        }
    }

    private void ifNecessaryChangePage(Page<T> page, String dataSourceType) {
        page.setPageNum((page.getPageNum() - 1) * page.getPageSize());
        if ("oracle".equals(dataSourceType)) {
            page.setPageSize(page.getPageSize() + page.getPageNum());
        }
    }
}
