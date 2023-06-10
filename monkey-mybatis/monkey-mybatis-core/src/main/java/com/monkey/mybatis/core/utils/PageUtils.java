package com.monkey.mybatis.core.utils;

import com.monkey.mybatis.core.page.PageDto;

/**
 * @description:  分页工具类
 * @author minliuhua
 * @date: 2023/5/17 2:26
 */
public class PageUtils {

    /**
     * 计算分页起始页
     * @param pageDto
     * @return
     */
    public static void setStart(PageDto pageDto) {
        pageDto.setPageNum((pageDto.getPageNum() - 1) * pageDto.getPageSize());
    }

}
