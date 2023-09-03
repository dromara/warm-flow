package com.warm.mybatis.core.agent;

import com.warm.mybatis.core.entity.FlowEntity;
import com.warm.mybatis.core.page.Page;
import com.warm.mybatis.core.service.IFlowService;
import com.warm.mybatis.core.utils.PageUtil;
import com.warm.tools.utils.ObjectUtil;

import java.util.List;

/**
 * 查询代理层处理
 *
 * @author hh
 * @date 2023-03-17
 */
public class FlowQuery<T extends FlowEntity> {

    /** 当前记录起始索引 */
    private int pageNum = 0;

    /** 每页显示记录数 */
    private int pageSize = 10;

    private String orderBy;

    private IFlowService flowService;

    public FlowQuery(IFlowService flowService) {
        this.flowService = flowService ;
    }

    /**
     * 查询列表
     *
     * @param entity 实体列表
     * @return 集合
     */
    public Page<T> page(T entity, Page<T> page) {
        if (ObjectUtil.isNull(page)) {
            page = PageUtil.getPage(pageNum, pageSize);
        }
        return flowService.page(entity, page, orderBy);
    }

    /**
     * 查询列表
     *
     * @param entity 实体列表
     * @return 集合
     */
    public List<T> list(T entity) {
        return flowService.list(entity, orderBy);
    }

    /**
     * 创建时间设置正序排列
     *
     * @return 集合
     */
    public FlowQuery orderByCreateTime() {
        this.orderBy = "create_time";
        return this;
    }

    /**
     * 更新时间设置正序排列
     *
     * @return 集合
     */
    public FlowQuery orderByUpdateTime() {
        this.orderBy = "update_time";
        return this;
    }

    /**
     * 设置正序排列
     *
     * @return 集合
     */
    public FlowQuery desc() {
        this.orderBy = orderBy + " desc";
        return this;
    }

    /**
     * 设置正序排列
     *
     * @param orderByField 排序字段
     * @return 集合
     */
    public FlowQuery orderByAsc(String orderByField) {
        this.orderBy = orderBy + " asc";
        return this;
    }

    /**
     * 设置倒序排列
     *
     * @param orderByField 排序字段
     * @return 集合
     */
    public FlowQuery orderByDesc(String orderByField) {
        this.orderBy = orderBy + " desc";
        return this;
    }

    /**
     * 用户自定义排序方案
     *
     * @param orderByField 排序字段
     * @return 集合
     */
    public FlowQuery orderBy(String orderByField) {
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

    public IFlowService getFlowService() {
        return flowService;
    }

    public void setFlowService(IFlowService flowService) {
        this.flowService = flowService;
    }
}
