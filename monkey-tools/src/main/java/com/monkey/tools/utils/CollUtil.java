package com.monkey.tools.utils;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @description:  集合工具类
 * @author minliuhua
 * @date: 2023/5/18 9:39
 */
public class CollUtil {
    /**
     * * 判断一个Collection是否为空， 包含List，Set，Queue
     *
     * @param coll 要判断的Collection
     * @return true：为空 false：非空
     */
    public static boolean isEmpty(Collection<?> coll)
    {
        return ObjectUtil.isNull(coll) || coll.isEmpty();
    }

    /**
     * * 判断一个Collection是否非空，包含List，Set，Queue
     *
     * @param coll 要判断的Collection
     * @return true：非空 false：空
     */
    public static boolean isNotEmpty(Collection<?> coll)
    {
        return !isEmpty(coll);
    }

    /**
     * 判断给定的collection列表中是否包含数组array 判断给定的数组array中是否包含给定的元素value
     *
     * @param collection 给定的集合
     * @param array 给定的数组
     * @return boolean 结果
     */
    public static boolean containsAny(Collection<String> collection, String... array)
    {
        if (isEmpty(collection) || ArrayUtil.isEmpty(array))
        {
            return false;
        }
        else
        {
            for (String str : array)
            {
                if (collection.contains(str))
                {
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
    public static boolean containsAny(Collection<String> collection1, Collection<String> collection2)
    {
        if (isEmpty(collection1) || isEmpty(collection2))
        {
            return false;
        }
        else
        {
            for (String str : collection2)
            {
                if (collection1.contains(str))
                {
                    return true;
                }
            }
            return false;
        }
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
}
