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

import com.easy.query.api.proxy.client.EasyEntityQuery;
import com.easy.query.api.proxy.entity.delete.ExpressionDeletable;
import com.easy.query.api.proxy.entity.insert.EntityInsertable;
import com.easy.query.api.proxy.entity.select.EntityQueryable;
import com.easy.query.api.proxy.entity.update.EntityUpdatable;
import com.easy.query.api.proxy.entity.update.ExpressionUpdatable;
import com.easy.query.core.api.pagination.EasyPageResult;
import com.easy.query.core.enums.SQLExecuteStrategyEnum;
import com.easy.query.core.expression.builder.core.NotNullOrEmptyValueFilter;
import com.easy.query.core.expression.lambda.SQLActionExpression1;
import com.easy.query.core.proxy.ProxyEntity;
import com.easy.query.core.proxy.ProxyEntityAvailable;
import org.dromara.warm.flow.core.entity.RootEntity;
import org.dromara.warm.flow.core.invoker.FrameInvoker;
import org.dromara.warm.flow.core.orm.agent.WarmQuery;
import org.dromara.warm.flow.core.orm.dao.WarmDao;
import org.dromara.warm.flow.core.utils.page.Page;
import org.dromara.warm.flow.orm.utils.UISort;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * BaseMapper接口
 *
 * @author link2fun
 */
public abstract class WarmDaoImpl<T extends RootEntity & ProxyEntityAvailable<T, P>, P extends ProxyEntity<P, T>>
    implements WarmDao<T> {

    private EasyEntityQuery entityQuery;

    private final Class<T> entityClass;

    public EasyEntityQuery entityQuery(){
        if (entityQuery == null) {
            entityQuery = FrameInvoker.getBean(EasyEntityQuery.class);
        }
        return entityQuery;
    }

    {
        entityClass = getGenericClass();
    }

    @SuppressWarnings("unchecked")
    private Class<T> getGenericClass() {
        return (Class<T>) ((java.lang.reflect.ParameterizedType) getClass()
                .getGenericSuperclass())
                .getActualTypeArguments()[0];
    }

    /**
     * 获取查询对象，然后可以自行编写easy-query查询语句
     *
     * @return 查询对象
     */
    public EntityQueryable<P, T> queryable() {
        return entityQuery().queryable(entityClass);
    }

    /**
     * 获取删除对象，然后可以自行编写easy-query删除语句
     *
     * @return 删除对象
     */
    public ExpressionDeletable<P, T> deletable() {
        return entityQuery().deletable(entityClass);
    }

    /**
     * 获取更新对象，然后可以自行编写easy-query更新语句
     *
     * @return 更新对象
     */
    public EntityUpdatable<P, T> updatable(T entity) {
        return entityQuery().updatable(entity);
    }

    /**
     * 获取更新对象，然后可以自行编写easy-query更新语句
     *
     * @return 更新对象
     */
    public ExpressionUpdatable<P, T> updatable() {
        return entityQuery().updatable(entityClass);
    }

    /**
     * 获取更新对象，然后可以自行编写easy-query更新语句
     *
     * @return 更新对象
     */
    public EntityInsertable<P, T> insertable(T entity) {
        return entityQuery().insertable(entity);
    }

    @Override
    public T selectById(Serializable id) {
        return queryable()
            .whereById(id)
            .singleOrNull();
    }

    @Override
    public List<T> selectByIds(Collection<? extends Serializable> ids) {
        return queryable()
            .whereByIds(ids)
            .toList();
    }

    @Override
    public Page<T> selectPage(T entity, Page<T> page) {
        UISort objectSort = UISort.of(page);
        EasyPageResult<T> pageResult = queryable()
                .filterConfigure(NotNullOrEmptyValueFilter.DEFAULT)
                .where(buildWhereCondition(entity))
                .orderByObject(Objects.nonNull(objectSort), objectSort)
                .toPageResult(page.getPageNum(), page.getPageSize());

        Page<T> rPage = new Page<>(pageResult.getData(), pageResult.getTotal());
        rPage.setPageNum(page.getPageNum());
        rPage.setPageSize(page.getPageSize());
        return rPage;
    }

    @Override
    public List<T> selectList(T entity, WarmQuery<T> query) {
        UISort uiSort = UISort.of(query);
        return queryable()
            .filterConfigure(NotNullOrEmptyValueFilter.DEFAULT)
            .where(buildWhereCondition(entity))
            .orderByObject(Objects.nonNull(uiSort),uiSort)
            .toList();
    }

    @Override
    public long selectCount(T entity) {
        return queryable()
            .filterConfigure(NotNullOrEmptyValueFilter.DEFAULT)
            .where(buildWhereCondition(entity))
            .count();
    }

    @Override
    public int save(T entity) {
        return (int) insertable(entity)
                .setSQLStrategy(SQLExecuteStrategyEnum.ONLY_NOT_NULL_COLUMNS)
                .executeRows();
    }

    @Override
    public int updateById(T entity) {
        return (int) updatable(entity)
            .setSQLStrategy(SQLExecuteStrategyEnum.ONLY_NOT_NULL_COLUMNS)
            .executeRows();
    }


    @Override
    public int deleteById(Serializable id) {
        return (int) deletable()
            .whereById(id)
            .executeRows();
    }

    @Override
    public int deleteByIds(Collection<? extends Serializable> ids) {
        return (int) deletable()
            .whereByIds(ids)
            .executeRows();
    }

    @Override
    public void saveBatch(List<T> list) {
        entityQuery().insertable(list)
                .setSQLStrategy(SQLExecuteStrategyEnum.ONLY_NOT_NULL_COLUMNS)
                .batch()
                .executeRows();

    }

    @Override
    public void updateBatch(List<T> list) {
        entityQuery().updatable(list)
                .setSQLStrategy(SQLExecuteStrategyEnum.ONLY_NOT_NULL_COLUMNS)
                .batch()
                .executeRows();

    }

    public abstract SQLActionExpression1<P> buildWhereCondition(T entity);
}
