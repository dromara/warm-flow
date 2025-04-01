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
package org.dromara.warm.flow.core.utils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 集合工具类
 *
 * @author warm
 * @since 2023/5/18 9:39
 */
public class CollUtil {
    /**
     * * 判断一个Collection是否为空， 包含List，Set，Queue
     *
     * @param coll 要判断的Collection
     * @return true：为空 false：非空
     */
    public static boolean isEmpty(Collection<?> coll) {
        return ObjectUtil.isNull(coll) || coll.isEmpty();
    }

    /**
     * 获取集合的第一个
     *
     * @param list
     */
    public static <T> T getOne(List<T> list) {
        return CollUtil.isEmpty(list) ? null : list.get(0);
    }

    /**
     * * 判断一个Collection是否非空，包含List，Set，Queue
     *
     * @param coll 要判断的Collection
     * @return true：非空 false：空
     */
    public static boolean isNotEmpty(Collection<?> coll) {
        return !isEmpty(coll);
    }

    /**
     * 如果集合是空，则返回空集合
     *
     * @param list        集合
     * @return 结果
     */
    public static <T> List<T> emptyDefault(List<T> list) {
        return isEmpty(list) ? Collections.emptyList() : list;
    }

    /**
     * 如果集合是空，则返回默认值
     *
     * @param list        集合
     * @param defaultList 默认值
     * @return 结果
     */
    public static <T> List<T> emptyDefault(List<T> list, List<T> defaultList) {
        return isEmpty(list) ? defaultList : list;
    }

    /**
     * 判断给定的collection列表中是否包含数组array 判断给定的数组array中是否包含给定的元素value
     *
     * @param collection 给定的集合
     * @param array      给定的数组
     * @return boolean 结果
     */
    public static boolean containsAny(Collection<String> collection, String... array) {
        if (isEmpty(collection) || ArrayUtil.isEmpty(array)) {
            return false;
        } else {
            for (String str : array) {
                if (collection.contains(str)) {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * 判断给定的collection1列表中是否包含collection2 判断给定的collection2中是否包含给定的元素value
     *
     * @param collection1 给定的集合1
     * @param collection2 给定的集合2
     * @return boolean 结果
     */
    public static boolean containsAny(Collection<String> collection1, Collection<String> collection2) {
        if (isEmpty(collection1) || isEmpty(collection2)) {
            return false;
        } else {
            for (String str : collection2) {
                if (collection1.contains(str)) {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * 判断给定的collection1列表中是否包含collection2 判断给定的collection2中是否完全不包含给定的元素value
     *
     * @param collection1 给定的集合1
     * @param collection2 给定的集合2
     * @return boolean 结果
     */
    public static boolean notContainsAny(Collection<String> collection1, Collection<String> collection2) {
        return !containsAny(collection1, collection2);
    }

    /**
     * 字符串转数组
     *
     * @param str 字符串
     * @param sep 分隔符
     * @return
     */
    public static List<String> strToColl(String str, String sep) {
        return StringUtils.isEmpty(str) ? null : Arrays.asList(str.split(sep));
    }

    /**
     * 集合add新的对象，返回新的集合
     *
     * @param list 集合
     * @param t    对象
     * @return
     */
    public static <T> List<T> listAddToNew(List<T> list, T t) {
        return listAddToNew(list, Collections.singletonList(t));
    }

    /**
     * 集合add新的对象，返回新的集合
     *
     * @param list  集合
     * @param listA 对象
     * @return
     */
    public static <T> List<T> listAddToNew(List<T> list, List<T> listA) {
        List<T> newList = new ArrayList<>();
        newList.addAll(listA);
        newList.addAll(list);
        return newList;
    }

    /**
     * 几个元素生成一个集合
     *
     * @param paramArr 对象数组
     * @param <T>      泛型
     * @author xiar
     * @since 2024/5/10 15:45
     */
    @SafeVarargs
    public static <T> List<T> toList(T... paramArr) {
        if (ArrayUtil.isEmpty(paramArr)) {
            return new ArrayList<>();
        }
        return Arrays.stream(paramArr).collect(Collectors.toList());
    }

    /**
     * 将collection转化为List集合，其中一个List集合中包含多个集合<br>
     * <B>{@code Collection<T>和Collection<Collection<T>> ------>  List<T> } </B>
     *
     * @param list  需要合并得集合
     * @param lists 需要合并得包含多个集合得集合
     * @param <T>   List中的泛型
     * @return List<T>
     * @author xiarg
     * @since 2024/5/10 15:45
     */
    public static <T> List<T> listAddListsToNew(List<T> list, List<List<T>> lists) {
        List<T> newList = new ArrayList<>();
        if (isNotEmpty(lists)) {
            for (List<T> ts : lists) {
                if (isNotEmpty(ts)) {
                    newList.addAll(ts);
                }
            }
        }
        if (isNotEmpty(list)) {
            newList.addAll(list);
        }
        return newList;
    }

    /**
     * 字符串集合拼接字符串
     *
     * @param list 字符串集合
     * @param sep  分隔符
     * @return String
     * @author xiar
     * @since 2024/5/10 15:45
     */
    public static String strListToStr(List<String> list, String sep) {
        StringBuilder sb = new StringBuilder();
        if (isNotEmpty(list)) {
            for (String str : list) {
                sb.append(str).append(sep);
            }
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    /**
     * 按照<p>batchSize</p>分割源集合,微批概念
     *
     * @param list      源集合
     * @param batchSize 分批大小
     * @param <T>       obj
     * @return 指定<p>batchSize</p>的全部list
     */
    public static <T> List<List<T>> split(List<T> list, int batchSize) {
        final int N = (int) Math.ceil((double) list.size() / batchSize);
        return IntStream.range(0, N).boxed()
                .map(i -> list.subList(i * batchSize, Math.min((i + 1) * batchSize, list.size())))
                .collect(Collectors.toList());
    }
}
