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
     * 排序字段
     */
    private String orderBy;

    /**
     * 排序的方向desc或者asc
     */
    private String isAsc = "desc";

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
            page = new Page<>(1, 10, orderBy, isAsc);
        }
        return warmService.page(entity, page.setOrderBy(orderBy).setIsAsc(isAsc));
    }

    /**
     * 查询列表
     *
     * @param entity 实体列表
     * @return 集合
     */
    public List<T> list(T entity) {
        return warmService.list(entity, this);
    }

    /**
     * 查询列表
     *
     * @param entity 实体列表
     * @return 集合
     */
    public T getOne(T entity) {
        List<T> list = warmService.list(entity, this);
        return CollUtil.getOne(list);
    }

    /**
     * id设置正序排列
     *
     * @return 集合
     */
    public WarmQuery<T> orderById() {
        this.orderBy = "id";
        return this;
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
        this.isAsc = "desc";
        return this;
    }

    /**
     * 设置正序排列
     *
     * @param orderByField 排序字段
     * @return 集合
     */
    public WarmQuery<T> orderByAsc(String orderByField) {
        this.orderBy = orderByField;
        this.isAsc = "asc";
        return this;
    }

    /**
     * 设置倒序排列
     *
     * @param orderByField 排序字段
     * @return 集合
     */
    public WarmQuery<T> orderByDesc(String orderByField) {
        this.orderBy = orderByField;
        this.isAsc = "desc";
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

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public String getIsAsc() {
        return isAsc;
    }

    public WarmQuery<T> setIsAsc(String isAsc) {
        this.isAsc = isAsc;
        return this;
    }

    public IWarmService<T> getWarmService() {
        return warmService;
    }

    public void setWarmService(IWarmService<T> warmService) {
        this.warmService = warmService;
    }
}
