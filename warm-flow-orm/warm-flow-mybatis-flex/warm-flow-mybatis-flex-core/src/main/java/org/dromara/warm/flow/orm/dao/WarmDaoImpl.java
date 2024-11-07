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

import com.mybatisflex.core.query.QueryWrapper;
import org.dromara.warm.flow.core.dao.WarmDao;
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
import java.util.function.Consumer;

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
        QueryWrapper queryWrapper = TenantDeleteUtil.getDefaultWrapper(newEntity());
        queryWrapper.eq("id", id);
        return getMapper().selectOneByQuery(queryWrapper);
    }

    /**
     * 根据ids查询
     *
     * @param ids 主键
     * @return 实体
     */
    @Override
    public List<T> selectByIds(Collection<? extends Serializable> ids) {
        QueryWrapper queryWrapper = TenantDeleteUtil.getDefaultWrapper(newEntity());
        queryWrapper.in(T::getId, ids);
        return getMapper().selectListByQuery(queryWrapper);
    }

    @Override
    public Page<T> selectPage(T entity, Page<T> page) {
        com.mybatisflex.core.paginate.Page<T> pageFlex =
                new com.mybatisflex.core.paginate.Page<>(
                        (page.getPageNum()/page.getPageSize())+1,
                        page.getPageSize()
                );
        QueryWrapper queryWrapper = TenantDeleteUtil.getDefaultWrapper(entity);
        if(StringUtils.isNotEmpty(page.getOrderBy())){
            queryWrapper.orderBy(page.getOrderBy(), "asc".equals(page.getIsAsc()));
        }
        pageFlex = getMapper().paginate(pageFlex, queryWrapper);
        if (ObjectUtil.isNotNull(pageFlex)) {
            return new Page<>(pageFlex.getRecords(), pageFlex.getTotalRow());
        }
        return Page.empty();
    }

    @Override
    public List<T> selectList(T entity, WarmQuery<T> query) {
        QueryWrapper queryWrapper = TenantDeleteUtil.getDefaultWrapper(entity);
        if (ObjectUtil.isNotNull(query)) {
            queryWrapper.orderBy(query.getOrderBy(), "asc".equals(query.getIsAsc()));
        }
        return getMapper().selectListByQuery(queryWrapper);
    }

    @Override
    public long selectCount(T entity) {
        QueryWrapper queryWrapper = TenantDeleteUtil.getDefaultWrapper(entity);
        return getMapper().selectCountByQuery(queryWrapper);
    }

    @Override
    public int save(T entity) {
        TenantDeleteUtil.fillEntity(entity);
        return getMapper().insert(entity);
    }

    @Override
    public int updateById(T entity) {
        QueryWrapper queryWrapper = TenantDeleteUtil.getIdWrapper(entity);
        return getMapper().updateByQuery(entity, true, queryWrapper);
    }

    @Override
    public int delete(T entity) {
        return delete(entity, null, null);
    }

    @Override
    public int deleteById(Serializable id) {
        return delete(newEntity(), (uq) -> uq.eq("id", id), (qw) -> qw.eq("id", id));
    }

    @Override
    public int deleteByIds(Collection<? extends Serializable> ids) {
        return delete(newEntity(), (uq) -> uq.in("id", ids), (qw) -> qw.in("id", ids));
    }

    @Override
    public void saveBatch(List<T> list) {
        for (T record : list) {
            save(record);
        }
    }

    @Override
    public void updateBatch(List<T> list) {
        for (T record : list) {
            updateById(record);
        }
    }

    public int delete(T entity, Consumer<QueryWrapper> uwConsumer, Consumer<QueryWrapper> qwConsumer) {
        QueryWrapper queryWrapper = TenantDeleteUtil.getDelWrapper(entity);
        T logicDelEntity = TenantDeleteUtil.delFillEntity(newEntity());
        if (StringUtils.isNotEmpty(logicDelEntity.getDelFlag())) {
            if (ObjectUtil.isNotNull(uwConsumer)) {
                uwConsumer.accept(queryWrapper);
            }
            return getMapper().updateByQuery(logicDelEntity, queryWrapper);
        }
        if (ObjectUtil.isNotNull(qwConsumer)) {
            qwConsumer.accept(queryWrapper);
        }
        return getMapper().deleteByQuery(queryWrapper);
    }

}
