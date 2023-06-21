package com.monkey.mybatis.core.utils;

import com.monkey.mybatis.core.page.Page;

/**
 * @description:  分页工具类
 * @author minliuhua
 * @date: 2023/5/17 2:26
 */
public class PageUtil {

    /**
     * 计算分页起始页
     * @param current
     * @param size
     * @return
     */
    public static <T> Page<T> getPage(Integer current, Integer size) {
        return new Page<>((current - 1) * size, size);
    }

}
