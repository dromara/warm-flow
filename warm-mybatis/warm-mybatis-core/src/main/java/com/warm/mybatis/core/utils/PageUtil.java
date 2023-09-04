package com.warm.mybatis.core.utils;

import com.warm.mybatis.core.page.Page;

/**
 * @author minliuhua
 * @description: 分页工具类
 * @date: 2023/5/17 2:26
 */
public class PageUtil {

    /**
     * 计算分页起始页
     *
     * @param current
     * @param size
     * @return
     */
    public static <T> Page<T> getPage(Integer current, Integer size) {
        return new Page<>((current - 1) * size, size);
    }

}
