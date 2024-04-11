package com.warm.flow.core.orm.agent;

import com.warm.flow.core.orm.service.IWarmService;
import com.warm.tools.utils.CollUtil;
import com.warm.tools.utils.ObjectUtil;
import com.warm.tools.utils.page.Page;

import java.util.List;

/**
 * 查询代理层处理
 *
 * @author warm
 * @date 2023-03-17
 */
public class WarmQuery<T> {

    /**
     * 当前记录起始索引
     */
    private int pageNum = 0;

    /**
     * 每页显示记录数
     */
    private int pageSize = 10;

    private String orderBy;

    private IWarmService<T> warmService;

    public WarmQuery(IWarmService<T> warmService) {
        this.warmService = warmService;
    }

    /**
     * 查询列表
     *
     * @param entity 实体列表
     * @return 集合
     */
    public Page<T> page(T entity, Page<T> page) {
        if (ObjectUtil.isNull(page)) {
            page = Page.pageOf(pageNum, pageSize);
        }
        return warmService.page(entity, page, orderBy);
    }

    /**
     * 查询列表
     *
     * @param entity 实体列表
     * @return 集合
     */
    public List<T> list(T entity) {
        return warmService.list(entity, orderBy);
    }

    /**
     * 查询列表
     *
     * @param entity 实体列表
     * @return 集合
     */
    public T getOne(T entity) {
        List<T> list = warmService.list(entity, orderBy);
        return CollUtil.getOne(list);
    }

    /**
     * 创建时间设置正序排列
     *
     * @return 集合
     */
    public WarmQuery<T> orderByCreateTime() {
        this.orderBy = "create_time";
        return this;
    }

    /**
     * 更新时间设置正序排列
     *
     * @return 集合
     */
    public WarmQuery<T> orderByUpdateTime() {
        this.orderBy = "update_time";
        return this;
    }

    /**
     * 设置正序排列
     *
     * @return 集合
     */
    public WarmQuery<T> desc() {
        this.orderBy = this.orderBy + " desc";
        return this;
    }

    /**
     * 设置正序排列
     *
     * @param orderByField 排序字段
     * @return 集合
     */
    public WarmQuery<T> orderByAsc(String orderByField) {
        this.orderBy = orderByField + " asc";
        return this;
    }

    /**
     * 设置倒序排列
     *
     * @param orderByField 排序字段
     * @return 集合
     */
    public WarmQuery<T> orderByDesc(String orderByField) {
        this.orderBy = orderByField + " desc";
        return this;
    }

    /**
     * 用户自定义排序方案
     *
     * @param orderByField 排序字段
     * @return 集合
     */
    public WarmQuery<T> orderBy(String orderByField) {
        this.orderBy = orderByField;
        return this;
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

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public IWarmService<T> getWarmService() {
        return warmService;
    }

    public void setWarmService(IWarmService<T> warmService) {
        this.warmService = warmService;
    }
}
