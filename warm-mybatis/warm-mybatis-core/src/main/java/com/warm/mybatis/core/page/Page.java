package com.warm.mybatis.core.page;

import java.util.ArrayList;
import java.util.List;

/**
 * @description: 分页
 * @author minliuhua
 * @date: 2023/5/17 1:28
 */
public class Page<T> {

    /** 当前记录起始索引 */
    private int pageNum;

    /** 每页显示记录数 */
    private int pageSize;

    /**
     * 数据
     */
    private List<T> list;

    /**
     * 总量
     */
    private long total;

    /** 排序列 */
    private String orderBy;

    /** 排序的方向desc或者asc */
    private String isAsc = "desc";

    public Page() {
        this.pageNum = 1;
        this.pageSize = 10;
    }

    public Page(int pageNum, int pageSize) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
    }

    public Page(List<T> list, long total) {
        this.list = list;
        this.total = total;
    }

    public Page(long total) {
        this.list = new ArrayList<>();
        this.total = total;
    }

    public static <T> Page<T> empty() {
        return new Page<>(0);
    }


    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public String getIsAsc() {
        return isAsc;
    }

    public void setIsAsc(String isAsc) {
        this.isAsc = isAsc;
    }
}
