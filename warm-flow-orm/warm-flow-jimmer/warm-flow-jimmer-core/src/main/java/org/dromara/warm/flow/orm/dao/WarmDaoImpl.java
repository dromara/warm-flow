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

import org.babyfish.jimmer.ImmutableObjects;
import org.babyfish.jimmer.meta.ImmutableProp;
import org.babyfish.jimmer.meta.ImmutableType;
import org.babyfish.jimmer.sql.JSqlClient;
import org.babyfish.jimmer.sql.ast.Expression;
import org.babyfish.jimmer.sql.ast.Predicate;
import org.babyfish.jimmer.sql.ast.PropExpression;
import org.babyfish.jimmer.sql.ast.Selection;
import org.babyfish.jimmer.sql.ast.mutation.SaveMode;
import org.babyfish.jimmer.sql.ast.query.MutableRootQuery;
import org.babyfish.jimmer.sql.ast.query.Order;
import org.babyfish.jimmer.sql.ast.table.Table;
import org.babyfish.jimmer.sql.ast.table.spi.TableProxy;
import org.dromara.warm.flow.core.entity.RootEntity;
import org.dromara.warm.flow.core.orm.agent.WarmQuery;
import org.dromara.warm.flow.core.orm.dao.WarmDao;
import org.dromara.warm.flow.core.utils.CollUtil;
import org.dromara.warm.flow.core.utils.ObjectUtil;
import org.dromara.warm.flow.core.utils.StringUtils;
import org.dromara.warm.flow.core.utils.page.Page;
import org.dromara.warm.flow.orm.convert.JimmerConverter;
import org.dromara.warm.flow.orm.utils.JimmerClients;
import org.dromara.warm.flow.orm.utils.TenantDeleteUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Base DAO implementation backed by Jimmer immutable models.
 */
public abstract class WarmDaoImpl<T extends RootEntity, M> implements WarmDao<T> {

    private final Class<M> modelClass;

    protected WarmDaoImpl(Class<M> modelClass) {
        this.modelClass = modelClass;
    }

    protected JSqlClient sqlClient() {
        return JimmerClients.sqlClient();
    }

    protected abstract TableProxy<M> table();

    protected abstract List<Predicate> predicates(T entity, Table<M> table);

    protected List<String> orderableProps() {
        return Collections.emptyList();
    }

    @Override
    public T selectById(Serializable id) {
        if (id == null) {
            return null;
        }
        T entity = TenantDeleteUtil.getEntity(newEntity());
        List<M> list = query(entity)
            .where(idProp(table()).eq(toLong(id)))
            .select(table())
            .limit(1)
            .execute();
        return list.isEmpty() ? null : fromModel(list.get(0));
    }

    @Override
    public List<T> selectByIds(Collection<? extends Serializable> ids) {
        if (CollUtil.isEmpty(ids)) {
            return new ArrayList<>();
        }
        T entity = TenantDeleteUtil.getEntity(newEntity());
        List<Long> idList = new ArrayList<>();
        for (Serializable id : ids) {
            idList.add(toLong(id));
        }
        List<M> rows = query(entity)
            .where(idProp(table()).in(idList))
            .select(table())
            .execute();
        return fromModels(rows);
    }

    @Override
    public Page<T> selectPage(T entity, Page<T> page) {
        TenantDeleteUtil.getEntity(entity);
        MutableRootQuery<TableProxy<M>> base = query(entity);
        long total = base.selectCount().fetchOne();
        if (total <= 0) {
            return Page.empty();
        }
        int pageNum = page.getPageNum() <= 0 ? 1 : page.getPageNum();
        int pageSize = page.getPageSize() <= 0 ? 10 : page.getPageSize();
        List<M> rows = applyOrder(query(entity), page)
            .select(table())
            .limit(pageSize, (long) (pageNum - 1) * pageSize)
            .execute();
        Page<T> result = new Page<>(fromModels(rows), total);
        result.setPageNum(page.getPageNum());
        result.setPageSize(page.getPageSize());
        result.setOrderBy(page.getOrderBy());
        result.setIsAsc(page.getIsAsc());
        return result;
    }

    @Override
    public List<T> selectList(T entity, WarmQuery<T> query) {
        TenantDeleteUtil.getEntity(entity);
        List<M> rows = applyOrder(query(entity), query)
            .select(table())
            .execute();
        return fromModels(rows);
    }

    @Override
    public long selectCount(T entity) {
        TenantDeleteUtil.getEntity(entity);
        return query(entity).selectCount().fetchOne();
    }

    @Override
    public int save(T entity) {
        TenantDeleteUtil.getEntity(entity);
        return sqlClient().saveCommand(model(entity))
            .setMode(SaveMode.INSERT_ONLY)
            .execute()
            .getTotalAffectedRowCount();
    }

    @Override
    public int updateById(T entity) {
        TenantDeleteUtil.getEntity(entity);
        Object id = entity.getId();
        if (id == null) {
            return 0;
        }
        return updateByLoadedProps(model(entity), scopedPredicates(entity, idProp(table()).eq(toLong((Serializable) id))));
    }

    @Override
    public int delete(T entity) {
        TenantDeleteUtil.getEntity(entity);
        if (TenantDeleteUtil.logicDeleteEnabled() && StringUtils.isNotEmpty(entity.getDelFlag())) {
            return logicalDelete(predicates(entity, table()));
        }
        return physicalDelete(predicates(entity, table()));
    }

    @Override
    public int deleteById(Serializable id) {
        T entity = TenantDeleteUtil.getEntity(newEntity());
        List<Predicate> predicates = scopedPredicates(idProp(table()).eq(toLong(id)));
        if (TenantDeleteUtil.logicDeleteEnabled() && StringUtils.isNotEmpty(entity.getDelFlag())) {
            return logicalDelete(predicates);
        }
        return physicalDelete(predicates);
    }

    @Override
    public int deleteByIds(Collection<? extends Serializable> ids) {
        if (CollUtil.isEmpty(ids)) {
            return 0;
        }
        List<Long> idList = new ArrayList<>();
        for (Serializable id : ids) {
            idList.add(toLong(id));
        }
        T entity = TenantDeleteUtil.getEntity(newEntity());
        List<Predicate> predicates = scopedPredicates(idProp(table()).in(idList));
        if (TenantDeleteUtil.logicDeleteEnabled() && StringUtils.isNotEmpty(entity.getDelFlag())) {
            return logicalDelete(predicates);
        }
        return physicalDelete(predicates);
    }

    @Override
    public void saveBatch(List<T> list) {
        if (CollUtil.isEmpty(list)) {
            return;
        }
        List<M> models = new ArrayList<>(list.size());
        for (T entity : list) {
            TenantDeleteUtil.getEntity(entity);
            models.add(model(entity));
        }
        sqlClient().saveEntitiesCommand(models)
            .setMode(SaveMode.INSERT_ONLY)
            .execute();
    }

    @Override
    public void updateBatch(List<T> list) {
        if (CollUtil.isEmpty(list)) {
            return;
        }
        for (T entity : list) {
            updateById(entity);
        }
    }

    protected List<Predicate> scopedPredicates() {
        return scopedPredicates(TenantDeleteUtil.getEntity(newEntity()));
    }

    protected List<Predicate> scopedPredicates(T entity) {
        List<Predicate> predicates = new ArrayList<>();
        eqIfNotEmpty(predicates, table(), "tenantId", entity.getTenantId());
        eqIfNotEmpty(predicates, table(), "delFlag", entity.getDelFlag());
        return predicates;
    }

    protected List<Predicate> scopedPredicates(Predicate predicate) {
        return scopedPredicates(TenantDeleteUtil.getEntity(newEntity()), predicate);
    }

    protected List<Predicate> scopedPredicates(T entity, Predicate predicate) {
        List<Predicate> predicates = scopedPredicates(entity);
        predicates.add(predicate);
        return predicates;
    }

    protected int deleteWhere(Predicate... predicates) {
        List<Predicate> scopedPredicates = scopedPredicates();
        Collections.addAll(scopedPredicates, predicates);
        if (TenantDeleteUtil.logicDeleteEnabled()) {
            return logicalDelete(scopedPredicates);
        }
        return physicalDelete(scopedPredicates);
    }

    protected int physicalDelete(List<Predicate> predicates) {
        return sqlClient().createDelete(table())
            .where(predicates.toArray(new Predicate[0]))
            .execute();
    }

    protected int updateWhere(List<Predicate> predicates, String prop, Object value) {
        return sqlClient().createUpdate(table())
            .where(predicates.toArray(new Predicate[0]))
            .set(table().get(prop), value)
            .execute();
    }

    protected int updateByLoadedProps(M model, List<Predicate> predicates) {
        org.babyfish.jimmer.sql.ast.mutation.MutableUpdate update = sqlClient().createUpdate(table())
            .where(predicates.toArray(new Predicate[0]));
        int setCount = 0;
        for (ImmutableProp prop : ImmutableType.get(modelClass).getProps().values()) {
            if (prop.isId() || !prop.hasStorage() || isScopeProp(prop.getName()) || !ImmutableObjects.isLoaded(model, prop)) {
                continue;
            }
            update.set(table().get(prop), ImmutableObjects.get(model, prop));
            setCount++;
        }
        return setCount == 0 ? 0 : update.execute();
    }

    protected boolean isScopeProp(String prop) {
        return "tenantId".equals(prop) || "delFlag".equals(prop);
    }

    protected int logicalDelete(List<Predicate> predicates) {
        return updateWhere(predicates, "delFlag", TenantDeleteUtil.logicDeleteValue());
    }

    protected MutableRootQuery<TableProxy<M>> query(T entity) {
        MutableRootQuery<TableProxy<M>> query = sqlClient().createQuery(table());
        List<Predicate> predicates = predicates(entity, table());
        if (!predicates.isEmpty()) {
            query.where(predicates.toArray(new Predicate[0]));
        }
        return query;
    }

    protected MutableRootQuery<TableProxy<M>> applyOrder(MutableRootQuery<TableProxy<M>> query, org.dromara.warm.flow.core.utils.page.OrderBy orderBy) {
        if (orderBy == null || StringUtils.isEmpty(orderBy.getOrderBy())) {
            return query;
        }
        String prop = toPropertyName(orderBy.getOrderBy());
        if (!isOrderable(prop)) {
            throw new IllegalArgumentException("Unsupported orderBy field for Jimmer adapter: " + orderBy.getOrderBy());
        }
        Expression<?> expression = table().get(prop);
        Order order = "DESC".equalsIgnoreCase(orderBy.getIsAsc()) ? expression.desc() : expression.asc();
        return query.orderBy(order);
    }

    protected boolean isOrderable(String prop) {
        if ("id".equals(prop) || "createTime".equals(prop) || "updateTime".equals(prop)) {
            return true;
        }
        return orderableProps().contains(prop);
    }

    protected String toPropertyName(String columnOrProp) {
        if (StringUtils.isEmpty(columnOrProp)) {
            return columnOrProp;
        }
        String value = columnOrProp.trim();
        if (!value.matches("[A-Za-z][A-Za-z0-9_-]*")) {
            throw new IllegalArgumentException("Unsafe orderBy field for Jimmer adapter: " + columnOrProp);
        }
        StringBuilder builder = new StringBuilder();
        boolean upperNext = false;
        for (int i = 0; i < value.length(); i++) {
            char ch = value.charAt(i);
            if (ch == '_' || ch == '-') {
                upperNext = true;
            } else if (upperNext) {
                builder.append(Character.toUpperCase(ch));
                upperNext = false;
            } else {
                builder.append(ch);
            }
        }
        return builder.toString();
    }

    protected List<Predicate> basePredicates(T entity, Table<M> table) {
        List<Predicate> predicates = new ArrayList<>();
        eq(predicates, table, "id", entity.getId());
        eqIfNotEmpty(predicates, table, "tenantId", entity.getTenantId());
        eqIfNotEmpty(predicates, table, "delFlag", entity.getDelFlag());
        eq(predicates, table, "createTime", entity.getCreateTime());
        eq(predicates, table, "updateTime", entity.getUpdateTime());
        eqIfNotEmpty(predicates, table, "createBy", entity.getCreateBy());
        eqIfNotEmpty(predicates, table, "updateBy", entity.getUpdateBy());
        return predicates;
    }

    protected void eqIfNotEmpty(List<Predicate> predicates, Table<M> table, String prop, String value) {
        if (StringUtils.isNotEmpty(value) && hasProp(prop)) {
            predicates.add(table.<String>get(prop).eq(value));
        }
    }

    protected void eq(List<Predicate> predicates, Table<M> table, String prop, Object value) {
        if (ObjectUtil.isNotNull(value) && hasProp(prop)) {
            predicates.add(table.get(prop).eq(value));
        }
    }

    protected void inIfNotEmpty(List<Predicate> predicates, Table<M> table, String prop, Collection<?> values) {
        if (CollUtil.isNotEmpty(values) && hasProp(prop)) {
            predicates.add(table.get(prop).in((Collection) values));
        }
    }

    protected boolean hasProp(String prop) {
        return ImmutableType.get(modelClass).getProps().containsKey(prop);
    }

    protected PropExpression<Long> idProp(Table<M> table) {
        return table.get("id");
    }

    protected M model(T entity) {
        return (M) JimmerConverter.toModel(entity);
    }

    protected T fromModel(M model) {
        return JimmerConverter.fromModel(model, this::newEntity);
    }

    protected List<T> fromModels(List<M> models) {
        return JimmerConverter.fromModels(models, this::newEntity);
    }

    private Long toLong(Serializable id) {
        if (id instanceof Number) {
            return ((Number) id).longValue();
        }
        return Long.valueOf(String.valueOf(id));
    }
}
