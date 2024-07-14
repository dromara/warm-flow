package com.warm.flow.orm.request;

import com.easy.query.core.api.dynamic.sort.ObjectSort;
import com.easy.query.core.api.dynamic.sort.ObjectSortBuilder;
import com.easy.query.core.expression.lambda.SQLExpression1;
import com.easy.query.core.proxy.ProxyEntity;
import com.easy.query.core.proxy.ProxyEntityAvailable;
import com.warm.flow.core.entity.RootEntity;
import com.warm.flow.core.orm.agent.WarmQuery;
import com.warm.flow.core.utils.StringUtils;
import com.warm.flow.core.utils.page.Page;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * UI排序
 * <a href="http://www.easy-query.com/easy-query-doc/guide/query/dynamic-sort.html#join%E5%8A%A8%E6%80%81%E6%8E%92%E5%BA%8F">动态排序</a>
 */
public class UISort implements ObjectSort {

    private final Map<String, Boolean> sort;

    public UISort(Map<String,Boolean> sort){

        this.sort = sort;
    }

    public static <T> UISort of(Page<T> page) {

        String orderBy = page.getOrderBy();
        // orderBy 要转为驼峰模式， 不然无法适配eq属性
        orderBy = StringUtils.toCamelCase(orderBy);
        String isAsc = page.getIsAsc();
        Map<String, Boolean> queryMap = new HashMap<>();
        queryMap.put(orderBy, "asc".equals(isAsc));
        return new UISort(queryMap);
    }

    public static <T> UISort of(WarmQuery<T> query) {
        if (Objects.isNull(query) || StringUtils.isEmpty(query.getOrderBy())) {
            return null;
        }
        Map<String, Boolean> queryMap = new HashMap<>();
        String orderBy = query.getOrderBy();
        // orderBy 要转为驼峰模式， 不然无法适配eq属性
        orderBy = StringUtils.toCamelCase(orderBy);
        queryMap.put(orderBy, "asc".equals(query.getIsAsc()));
        return new UISort(queryMap);
    }

    @Override
    public void configure(ObjectSortBuilder builder) {
        for (Map.Entry<String, Boolean> s : sort.entrySet()) {
            //自行判断key和value是否为null 因为是包装类型可能会出现npe
            // key为需要排序的属性,value表示需要排序是不是asc
            builder.orderBy(s.getKey(),s.getValue());
        }
    }
}
