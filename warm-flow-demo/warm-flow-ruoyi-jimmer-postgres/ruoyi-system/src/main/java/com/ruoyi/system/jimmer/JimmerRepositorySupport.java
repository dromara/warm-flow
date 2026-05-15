package com.ruoyi.system.jimmer;

import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.common.jimmer.JimmerPage;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import org.babyfish.jimmer.DraftObjects;
import org.babyfish.jimmer.ImmutableObjects;
import org.babyfish.jimmer.meta.ImmutableProp;
import org.babyfish.jimmer.meta.ImmutableType;
import org.babyfish.jimmer.runtime.Internal;
import org.babyfish.jimmer.sql.JSqlClient;
import org.babyfish.jimmer.sql.ast.Expression;
import org.babyfish.jimmer.sql.ast.Predicate;
import org.babyfish.jimmer.sql.ast.PropExpression;
import org.babyfish.jimmer.sql.ast.mutation.MutableUpdate;
import org.babyfish.jimmer.sql.ast.mutation.SaveMode;
import org.babyfish.jimmer.sql.ast.mutation.SimpleSaveResult;
import org.babyfish.jimmer.sql.ast.query.MutableRootQuery;
import org.babyfish.jimmer.sql.ast.query.Order;
import org.babyfish.jimmer.sql.ast.table.spi.TableProxy;
import org.babyfish.jimmer.sql.runtime.JSqlClientImplementor;
import org.springframework.beans.factory.annotation.Autowired;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Shared Jimmer repository support for the RuoYi compatibility layer.
 *
 * <p>Controllers and services keep their original mutable domain objects and
 * mapper method names. Persistence is backed by Jimmer immutable model
 * interfaces and generated table proxies.</p>
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public abstract class JimmerRepositorySupport<D, M>
{
    private static final Map<Class<?>, Map<String, PropertyDescriptor>> BEAN_PROPS = new ConcurrentHashMap<>();
    private static final Map<Class<?>, TableProxy<?>> TABLES = new ConcurrentHashMap<>();

    @Autowired
    protected JSqlClient sqlClient;

    private final Class<D> domainClass;
    private final Class<M> modelClass;
    private final String idProp;
    private final ImmutableType immutableType;

    protected JimmerRepositorySupport(Class<D> domainClass, Class<M> modelClass, String idProp)
    {
        this.domainClass = domainClass;
        this.modelClass = modelClass;
        this.idProp = idProp;
        this.immutableType = ImmutableType.get(modelClass);
    }

    protected final TableProxy<M> table()
    {
        return (TableProxy<M>) TABLES.computeIfAbsent(modelClass, type -> {
            try
            {
                Class<?> tableClass = Class.forName(type.getName() + "Table");
                Field field = tableClass.getField("$");
                return (TableProxy<?>) field.get(null);
            }
            catch (Exception e)
            {
                throw new IllegalStateException("Jimmer table proxy is not generated for " + type.getName()
                    + ". Check jimmer-apt configuration.", e);
            }
        });
    }

    protected final String idProp()
    {
        return idProp;
    }

    protected final boolean hasProp(String prop)
    {
        return immutableType.getProps().containsKey(prop);
    }

    protected final <X> PropExpression<X> prop(String prop)
    {
        return table().get(prop);
    }

    protected final PropExpression<Object> idExpression()
    {
        return table().get(idProp);
    }

    protected final D newDomain()
    {
        try
        {
            return domainClass.newInstance();
        }
        catch (InstantiationException | IllegalAccessException e)
        {
            throw new IllegalStateException("Cannot instantiate domain " + domainClass.getName(), e);
        }
    }

    protected final M toModel(D domain)
    {
        if (domain == null)
        {
            return null;
        }
        Map<String, PropertyDescriptor> domainProps = beanProps(domainClass);
        return (M) Internal.produce(immutableType, null, draft -> {
            for (ImmutableProp prop : immutableType.getProps().values())
            {
                if (!prop.hasStorage() || prop.isTransient())
                {
                    continue;
                }
                PropertyDescriptor descriptor = domainProps.get(domainPropertyName(prop.getName()));
                if (descriptor == null || descriptor.getReadMethod() == null)
                {
                    continue;
                }
                Object value = invoke(descriptor.getReadMethod(), domain);
                if (value != null)
                {
                    DraftObjects.set(draft, prop, convertValue(value, prop.getReturnClass()));
                }
            }
        });
    }

    public final D fromModel(M model)
    {
        if (model == null)
        {
            return null;
        }
        D domain = newDomain();
        copyToDomain(model, domain);
        return domain;
    }

    protected final List<D> fromModels(List<M> models)
    {
        List<D> domains = new ArrayList<>();
        if (models == null)
        {
            return domains;
        }
        for (M model : models)
        {
            domains.add(fromModel(model));
        }
        return domains;
    }

    protected final void copyToDomain(M model, D domain)
    {
        Map<String, PropertyDescriptor> domainProps = beanProps(domainClass);
        for (ImmutableProp prop : immutableType.getProps().values())
        {
            if (!prop.hasStorage() || prop.isTransient() || !ImmutableObjects.isLoaded(model, prop))
            {
                continue;
            }
            PropertyDescriptor descriptor = domainProps.get(domainPropertyName(prop.getName()));
            if (descriptor == null || descriptor.getWriteMethod() == null)
            {
                continue;
            }
            Object value = ImmutableObjects.get(model, prop);
            invoke(descriptor.getWriteMethod(), domain, convertValue(value, descriptor.getPropertyType()));
        }
    }

    protected final Object domainValue(D domain, String prop)
    {
        if (domain == null)
        {
            return null;
        }
        PropertyDescriptor descriptor = beanProps(domainClass).get(domainPropertyName(prop));
        if (descriptor == null || descriptor.getReadMethod() == null)
        {
            return null;
        }
        return invoke(descriptor.getReadMethod(), domain);
    }

    protected final void setDomainValue(D domain, String prop, Object value)
    {
        PropertyDescriptor descriptor = beanProps(domainClass).get(domainPropertyName(prop));
        if (descriptor != null && descriptor.getWriteMethod() != null)
        {
            invoke(descriptor.getWriteMethod(), domain, convertValue(value, descriptor.getPropertyType()));
        }
    }

    public D selectById(Object id)
    {
        return findOne(predicates(eq(idProp, id)), null);
    }

    protected D findOne(List<Predicate> predicates, List<Order> orders)
    {
        List<M> rows = selectModels(predicates, orders, 1, 0, false);
        return rows.isEmpty() ? null : fromModel(rows.get(0));
    }

    protected List<D> listAll()
    {
        return list(new ArrayList<>(), null);
    }

    protected List<D> list(List<Predicate> predicates)
    {
        return list(predicates, null);
    }

    protected List<D> list(List<Predicate> predicates, List<Order> defaultOrders)
    {
        return fromModels(selectModels(predicates, defaultOrders, -1, 0, true));
    }

    protected List<M> selectModels(List<Predicate> predicates, List<Order> defaultOrders, int fixedLimit, long fixedOffset, boolean applyPage)
    {
        List<Predicate> safePredicates = compact(predicates);
        JimmerPage.PageState page = applyPage ? JimmerPage.current() : null;
        if (page != null)
        {
            // Count SQL must not inherit page/default orders. PostgreSQL rejects
            // `select count(1) ... order by ... limit ?`, and count needs only
            // the same filtering predicates as the data query.
            Long total = createCountQuery(safePredicates).selectCount().fetchOne();
            JimmerPage.total(total == null ? 0L : total);
        }
        MutableRootQuery<TableProxy<M>> query = createQuery(safePredicates, defaultOrders);
        int limit = fixedLimit;
        long offset = fixedOffset;
        if (page != null)
        {
            limit = page.getPageSize();
            offset = page.getOffset();
        }
        if (limit > -1)
        {
            return query.select(table()).limit(limit, offset).execute();
        }
        return query.select(table()).execute();
    }

    protected MutableRootQuery<TableProxy<M>> createQuery(List<Predicate> predicates, List<Order> defaultOrders)
    {
        MutableRootQuery<TableProxy<M>> query = sqlClient.createQuery(table());
        List<Predicate> safePredicates = compact(predicates);
        if (!safePredicates.isEmpty())
        {
            query.where(safePredicates.toArray(new Predicate[0]));
        }
        List<Order> orders = pageOrders();
        if (orders.isEmpty() && defaultOrders != null)
        {
            orders = defaultOrders;
        }
        if (orders != null && !orders.isEmpty())
        {
            query.orderBy(orders);
        }
        return query;
    }

    protected MutableRootQuery<TableProxy<M>> createCountQuery(List<Predicate> predicates)
    {
        MutableRootQuery<TableProxy<M>> query = sqlClient.createQuery(table());
        List<Predicate> safePredicates = compact(predicates);
        if (!safePredicates.isEmpty())
        {
            query.where(safePredicates.toArray(new Predicate[0]));
        }
        return query;
    }

    protected long count(List<Predicate> predicates)
    {
        Long total = createCountQuery(predicates).selectCount().fetchOne();
        return total == null ? 0L : total;
    }

    protected boolean exists(List<Predicate> predicates)
    {
        return count(predicates) > 0;
    }

    public int insert(D domain)
    {
        SimpleSaveResult<M> result = sqlClient.saveCommand(toModel(domain))
            .setMode(SaveMode.INSERT_ONLY)
            .execute();
        if (domain != null && result.getModifiedEntity() != null && hasProp(idProp)
            && ImmutableObjects.isLoaded(result.getModifiedEntity(), idProp))
        {
            setDomainValue(domain, idProp, ImmutableObjects.get(result.getModifiedEntity(), idProp));
        }
        return result.getTotalAffectedRowCount();
    }

    public int updateById(D domain)
    {
        Object id = domainValue(domain, idProp);
        if (id == null)
        {
            return 0;
        }
        M model = toModel(domain);
        MutableUpdate update = sqlClient.createUpdate(table()).where(eq(idProp, id));
        int setCount = 0;
        for (ImmutableProp prop : immutableType.getProps().values())
        {
            if (prop.isId() || !prop.hasStorage() || prop.isTransient() || !ImmutableObjects.isLoaded(model, prop))
            {
                continue;
            }
            update.set(table().get(prop), ImmutableObjects.get(model, prop));
            setCount++;
        }
        return setCount == 0 ? 0 : update.execute();
    }

    public int updateFieldsById(Object id, Map<String, Object> values)
    {
        if (id == null || values == null || values.isEmpty())
        {
            return 0;
        }
        MutableUpdate update = sqlClient.createUpdate(table()).where(eq(idProp, id));
        int setCount = 0;
        for (Map.Entry<String, Object> entry : values.entrySet())
        {
            if (hasProp(entry.getKey()) && !idProp.equals(entry.getKey()))
            {
                update.set(table().get(entry.getKey()), entry.getValue());
                setCount++;
            }
        }
        return setCount == 0 ? 0 : update.execute();
    }

    public int deleteById(Object id)
    {
        if (id == null)
        {
            return 0;
        }
        return sqlClient.createDelete(table()).where(eq(idProp, id)).execute();
    }

    public int deleteByIds(Object[] ids)
    {
        if (ids == null || ids.length == 0)
        {
            return 0;
        }
        return deleteWhere(in(idProp, Arrays.asList(ids)));
    }

    protected int deleteWhere(Predicate... predicates)
    {
        List<Predicate> list = compact(Arrays.asList(predicates));
        if (list.isEmpty())
        {
            return 0;
        }
        return sqlClient.createDelete(table()).where(list.toArray(new Predicate[0])).execute();
    }

    protected int softDeleteById(Object id)
    {
        return softDeleteWhere(predicates(eq(idProp, id)));
    }

    protected int softDeleteByIds(Object[] ids)
    {
        return softDeleteWhere(predicates(in(idProp, Arrays.asList(ids))));
    }

    protected int softDeleteWhere(List<Predicate> predicates)
    {
        if (!hasProp("delFlag"))
        {
            return deleteWhere(predicates.toArray(new Predicate[0]));
        }
        return updateWhere(predicates, mapOf("delFlag", "2"));
    }

    protected int updateWhere(List<Predicate> predicates, Map<String, Object> values)
    {
        List<Predicate> safePredicates = compact(predicates);
        if (safePredicates.isEmpty() || values == null || values.isEmpty())
        {
            return 0;
        }
        MutableUpdate update = sqlClient.createUpdate(table()).where(safePredicates.toArray(new Predicate[0]));
        int setCount = 0;
        for (Map.Entry<String, Object> entry : values.entrySet())
        {
            if (hasProp(entry.getKey()))
            {
                update.set(table().get(entry.getKey()), entry.getValue());
                setCount++;
            }
        }
        return setCount == 0 ? 0 : update.execute();
    }

    protected int batchInsert(Collection<D> domains)
    {
        if (domains == null || domains.isEmpty())
        {
            return 0;
        }
        int rows = 0;
        for (D domain : domains)
        {
            rows += insert(domain);
        }
        return rows;
    }

    protected Predicate eq(String prop, Object value)
    {
        if (value == null || !hasProp(prop))
        {
            return null;
        }
        return table().get(prop).eq(convertValue(value, immutableType.getProp(prop).getReturnClass()));
    }

    protected Predicate ne(String prop, Object value)
    {
        if (value == null || !hasProp(prop))
        {
            return null;
        }
        return table().get(prop).ne(convertValue(value, immutableType.getProp(prop).getReturnClass()));
    }

    protected Predicate in(String prop, Collection<?> values)
    {
        if (values == null || values.isEmpty() || !hasProp(prop))
        {
            return null;
        }
        List<Object> converted = new ArrayList<>();
        Class<?> targetType = immutableType.getProp(prop).getReturnClass();
        for (Object value : values)
        {
            if (value != null)
            {
                converted.add(convertValue(value, targetType));
            }
        }
        return converted.isEmpty() ? null : table().get(prop).in(converted);
    }

    protected Predicate notIn(String prop, Collection<?> values)
    {
        if (values == null || values.isEmpty() || !hasProp(prop))
        {
            return null;
        }
        List<Object> converted = new ArrayList<>();
        Class<?> targetType = immutableType.getProp(prop).getReturnClass();
        for (Object value : values)
        {
            if (value != null)
            {
                converted.add(convertValue(value, targetType));
            }
        }
        return converted.isEmpty() ? null : table().get(prop).notIn(converted);
    }

    protected Predicate like(String prop, String value)
    {
        if (StringUtils.isEmpty(value) || !hasProp(prop))
        {
            return null;
        }
        return ((org.babyfish.jimmer.sql.ast.StringExpression) (Expression<?>) table().get(prop)).like(value);
    }

    protected Predicate ilike(String prop, String value)
    {
        if (StringUtils.isEmpty(value) || !hasProp(prop))
        {
            return null;
        }
        return ((org.babyfish.jimmer.sql.ast.StringExpression) (Expression<?>) table().get(prop)).ilike(value);
    }

    protected Predicate ge(String prop, Object value)
    {
        if (value == null || !hasProp(prop))
        {
            return null;
        }
        return ((org.babyfish.jimmer.sql.ast.ComparableExpression) table().get(prop)).ge((Comparable) convertValue(value, immutableType.getProp(prop).getReturnClass()));
    }

    protected Predicate le(String prop, Object value)
    {
        if (value == null || !hasProp(prop))
        {
            return null;
        }
        return ((org.babyfish.jimmer.sql.ast.ComparableExpression) table().get(prop)).le((Comparable) convertValue(value, immutableType.getProp(prop).getReturnClass()));
    }

    protected Predicate sql(String template, Object... values)
    {
        return Predicate.sql(jimmerNativeSql(template), context -> {
            if (values != null)
            {
                for (Object value : flatten(values))
                {
                    context.value(value);
                }
            }
        });
    }

    /**
     * Jimmer native SQL uses {@code %v} for value placeholders, while the
     * migrated RuoYi mappers are intentionally written with JDBC-style
     * {@code ?} placeholders to keep the PostgreSQL snippets readable and close
     * to their original SQL. Convert only placeholders outside quoted string
     * literals so existing mapper snippets bind values reliably.
     */
    private static String jimmerNativeSql(String template)
    {
        if (template == null || template.indexOf('?') < 0)
        {
            return template;
        }
        StringBuilder sql = new StringBuilder(template.length() + 8);
        boolean inQuote = false;
        for (int i = 0; i < template.length(); i++)
        {
            char c = template.charAt(i);
            if (c == '\'')
            {
                sql.append(c);
                if (inQuote && i + 1 < template.length() && template.charAt(i + 1) == '\'')
                {
                    sql.append(template.charAt(++i));
                }
                else
                {
                    inQuote = !inQuote;
                }
            }
            else if (c == '?' && !inQuote)
            {
                sql.append("%v");
            }
            else
            {
                sql.append(c);
            }
        }
        return sql.toString();
    }

    protected Predicate dataScope(D condition)
    {
        Object scope = param(condition, "dataScope");
        if (scope == null || StringUtils.isEmpty(String.valueOf(scope)))
        {
            return null;
        }
        String text = String.valueOf(scope).trim();
        if (text.startsWith("AND "))
        {
            text = text.substring(4).trim();
        }
        if (StringUtils.isEmpty(text))
        {
            return null;
        }
        return Predicate.sql(text);
    }

    protected List<Predicate> predicates(Predicate... predicates)
    {
        return compact(Arrays.asList(predicates));
    }

    protected void addEq(List<Predicate> predicates, D condition, String prop)
    {
        predicates.add(eq(prop, domainValue(condition, prop)));
    }

    protected void addLike(List<Predicate> predicates, D condition, String prop)
    {
        Object value = domainValue(condition, prop);
        if (value != null)
        {
            predicates.add(like(prop, String.valueOf(value)));
        }
    }

    protected void addDateRange(List<Predicate> predicates, D condition, String prop)
    {
        if (!(condition instanceof BaseEntity))
        {
            return;
        }
        Map<String, Object> params = ((BaseEntity) condition).getParams();
        Object begin = params.get("beginTime");
        Object end = params.get("endTime");
        if (begin != null && StringUtils.isNotBlank(String.valueOf(begin)))
        {
            predicates.add(ge(prop, parseDate(begin)));
        }
        if (end != null && StringUtils.isNotBlank(String.valueOf(end)))
        {
            Date endDate = parseDate(end);
            if (endDate != null)
            {
                predicates.add(le(prop, org.apache.commons.lang3.time.DateUtils.addDays(endDate, 1)));
            }
        }
    }

    protected Object param(D condition, String key)
    {
        if (condition instanceof BaseEntity)
        {
            return ((BaseEntity) condition).getParams().get(key);
        }
        return null;
    }

    protected List<Order> orders(String... props)
    {
        List<Order> orders = new ArrayList<>();
        if (props == null)
        {
            return orders;
        }
        for (String prop : props)
        {
            if (prop == null)
            {
                continue;
            }
            String value = prop.trim();
            boolean desc = value.toLowerCase().endsWith(" desc");
            value = value.replaceAll("(?i)\\s+asc$", "").replaceAll("(?i)\\s+desc$", "").trim();
            value = toPropertyName(value);
            if (hasProp(value))
            {
                Expression<?> expression = table().get(value);
                orders.add(desc ? expression.desc() : expression.asc());
            }
        }
        return orders;
    }

    protected Map<String, Object> mapOf(Object... args)
    {
        Map<String, Object> map = new LinkedHashMap<>();
        for (int i = 0; i + 1 < args.length; i += 2)
        {
            map.put(String.valueOf(args[i]), args[i + 1]);
        }
        return map;
    }

    protected final <R> R jdbc(JdbcCallback<R> callback)
    {
        return ((JSqlClientImplementor) sqlClient).getConnectionManager().execute(connection -> {
            try
            {
                return callback.doInConnection(connection);
            }
            catch (SQLException e)
            {
                throw new IllegalStateException("Jimmer JDBC operation failed", e);
            }
        });
    }

    protected final int executeSql(String sql, Object... params)
    {
        return jdbc(connection -> {
            try (PreparedStatement statement = connection.prepareStatement(sql))
            {
                bind(statement, flatten(params));
                return statement.executeUpdate();
            }
        });
    }

    protected final <R> List<R> querySql(String sql, RowMapper<R> mapper, Object... params)
    {
        return jdbc(connection -> {
            try (PreparedStatement statement = connection.prepareStatement(sql))
            {
                bind(statement, flatten(params));
                try (ResultSet rs = statement.executeQuery())
                {
                    List<R> rows = new ArrayList<>();
                    while (rs.next())
                    {
                        rows.add(mapper.map(rs));
                    }
                    return rows;
                }
            }
        });
    }

    protected final <R> List<R> queryPagedSql(String sql, RowMapper<R> mapper, Object... params)
    {
        JimmerPage.PageState page = JimmerPage.current();
        if (page == null)
        {
            return querySql(sql, mapper, params);
        }
        List<Object> flattened = flatten(params);
        Long total = queryScalar("select count(*) from (" + sql + ") jimmer_page_total", Long.class, flattened.toArray());
        JimmerPage.total(total == null ? 0L : total);
        List<Object> pagedParams = new ArrayList<>(flattened);
        pagedParams.add(page.getPageSize());
        pagedParams.add(page.getOffset());
        return querySql(sql + " limit ? offset ?", mapper, pagedParams.toArray());
    }

    protected final <R> R queryScalar(String sql, Class<R> type, Object... params)
    {
        return jdbc(connection -> {
            try (PreparedStatement statement = connection.prepareStatement(sql))
            {
                bind(statement, flatten(params));
                try (ResultSet rs = statement.executeQuery())
                {
                    if (!rs.next())
                    {
                        return null;
                    }
                    Object value = rs.getObject(1);
                    return (R) convertValue(value, type);
                }
            }
        });
    }

    protected final String placeholders(Collection<?> values)
    {
        if (values == null || values.isEmpty())
        {
            return "(null)";
        }
        StringBuilder builder = new StringBuilder("(");
        for (int i = 0; i < values.size(); i++)
        {
            if (i > 0)
            {
                builder.append(',');
            }
            builder.append('?');
        }
        return builder.append(')').toString();
    }

    protected List<Predicate> compact(Collection<Predicate> predicates)
    {
        List<Predicate> list = new ArrayList<>();
        if (predicates != null)
        {
            for (Predicate predicate : predicates)
            {
                if (predicate != null)
                {
                    list.add(predicate);
                }
            }
        }
        return list;
    }

    protected String toPropertyName(String columnOrProp)
    {
        if (StringUtils.isBlank(columnOrProp))
        {
            return columnOrProp;
        }
        String value = columnOrProp.trim();
        int dot = value.lastIndexOf('.');
        if (dot >= 0)
        {
            value = value.substring(dot + 1);
        }
        StringBuilder builder = new StringBuilder();
        boolean upperNext = false;
        for (int i = 0; i < value.length(); i++)
        {
            char ch = value.charAt(i);
            if (ch == '_' || ch == '-')
            {
                upperNext = true;
            }
            else if (upperNext)
            {
                builder.append(Character.toUpperCase(ch));
                upperNext = false;
            }
            else
            {
                builder.append(ch);
            }
        }
        return builder.toString();
    }

    private String domainPropertyName(String prop)
    {
        if ("frameFlag".equals(prop))
        {
            return "isFrame";
        }
        if ("cacheFlag".equals(prop))
        {
            return "isCache";
        }
        if ("defaultFlag".equals(prop))
        {
            return "isDefault";
        }
        if ("pkFlag".equals(prop))
        {
            return "isPk";
        }
        if ("incrementFlag".equals(prop))
        {
            return "isIncrement";
        }
        if ("requiredFlag".equals(prop))
        {
            return "isRequired";
        }
        if ("insertFlag".equals(prop))
        {
            return "isInsert";
        }
        if ("editFlag".equals(prop))
        {
            return "isEdit";
        }
        if ("listFlag".equals(prop))
        {
            return "isList";
        }
        if ("queryFlag".equals(prop))
        {
            return "isQuery";
        }
        return prop;
    }

    private List<Order> pageOrders()
    {
        JimmerPage.PageState page = JimmerPage.current();
        if (page == null || StringUtils.isEmpty(page.getOrderBy()))
        {
            return new ArrayList<>();
        }
        return orders(page.getOrderBy().split(","));
    }

    private static Map<String, PropertyDescriptor> beanProps(Class<?> type)
    {
        return BEAN_PROPS.computeIfAbsent(type, key -> {
            try
            {
                BeanInfo beanInfo = Introspector.getBeanInfo(key);
                Map<String, PropertyDescriptor> map = new HashMap<>();
                for (PropertyDescriptor descriptor : beanInfo.getPropertyDescriptors())
                {
                    map.put(descriptor.getName(), descriptor);
                }
                return map;
            }
            catch (IntrospectionException e)
            {
                throw new IllegalStateException("Cannot introspect " + key.getName(), e);
            }
        });
    }

    private static Object invoke(Method method, Object target, Object... args)
    {
        try
        {
            if (!method.isAccessible())
            {
                method.setAccessible(true);
            }
            return method.invoke(target, args);
        }
        catch (IllegalAccessException | InvocationTargetException e)
        {
            throw new IllegalStateException("Cannot invoke " + method, e);
        }
    }

    private static Object convertValue(Object value, Class<?> targetType)
    {
        if (value == null || targetType == null || targetType.isInstance(value))
        {
            return value;
        }
        if (targetType == Long.class || targetType == Long.TYPE)
        {
            return value instanceof Number ? ((Number) value).longValue() : Long.valueOf(String.valueOf(value));
        }
        if (targetType == Integer.class || targetType == Integer.TYPE)
        {
            return value instanceof Number ? ((Number) value).intValue() : Integer.valueOf(String.valueOf(value));
        }
        if (targetType == Short.class || targetType == Short.TYPE)
        {
            return value instanceof Number ? ((Number) value).shortValue() : Short.valueOf(String.valueOf(value));
        }
        if (targetType == Boolean.class || targetType == Boolean.TYPE)
        {
            if (value instanceof Boolean)
            {
                return value;
            }
            String string = String.valueOf(value);
            return "1".equals(string) || "true".equalsIgnoreCase(string);
        }
        if (targetType == String.class)
        {
            return String.valueOf(value);
        }
        if (Date.class.isAssignableFrom(targetType) && value instanceof String)
        {
            return parseDate(value);
        }
        return value;
    }

    private static Date parseDate(Object value)
    {
        if (value == null)
        {
            return null;
        }
        if (value instanceof Date)
        {
            return (Date) value;
        }
        return DateUtils.parseDate(String.valueOf(value));
    }

    private static void bind(PreparedStatement statement, List<Object> params) throws SQLException
    {
        for (int i = 0; i < params.size(); i++)
        {
            statement.setObject(i + 1, params.get(i));
        }
    }

    private static List<Object> flatten(Object... values)
    {
        List<Object> result = new ArrayList<>();
        if (values == null)
        {
            return result;
        }
        for (Object value : values)
        {
            if (value instanceof Collection)
            {
                result.addAll((Collection<?>) value);
            }
            else if (value != null && value.getClass().isArray())
            {
                if (value instanceof Object[])
                {
                    result.addAll(Arrays.asList((Object[]) value));
                }
                else
                {
                    int length = java.lang.reflect.Array.getLength(value);
                    for (int i = 0; i < length; i++)
                    {
                        result.add(java.lang.reflect.Array.get(value, i));
                    }
                }
            }
            else
            {
                result.add(value);
            }
        }
        return result;
    }

    @FunctionalInterface
    protected interface RowMapper<R>
    {
        R map(ResultSet rs) throws SQLException;
    }

    @FunctionalInterface
    protected interface JdbcCallback<R>
    {
        R doInConnection(Connection connection) throws SQLException;
    }
}
