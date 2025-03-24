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
package org.dromara.warm.flow.core.utils.page;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 分页
 *
 * @author warm
 * @since 2023/5/17 1:28
 */
public class Page<T> implements OrderBy, Serializable {
    private static final long serialVersionUID = -1615974051898019272L;

    /**
     * 当前记录起始索引
     */
    private int pageNum = 1;

    /**
     * 每页显示记录数
     */
    private int pageSize = 10;

    /**
     * 数据
     */
    private List<T> list;

    /**
     * 总量
     */
    private long total;

    /**
     * 排序列
     */
    private String orderBy;

    /**
     * 排序的方向desc或者asc
     */
    private String isAsc = "ASC";

    public Page() {
        this.pageNum = 1;
        this.pageSize = 10;
    }

    public Page(int pageNum, int pageSize) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
    }

    public Page(int pageNum, int pageSize, String orderBy, String isAsc) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.orderBy = orderBy;
        this.isAsc = isAsc;
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

    /**
     * 计算分页起始页
     *
     * @param pageNum  当前页码
     * @param size     每页显示记录数
     * @return 分页结果
     */
    public static <T> Page<T> pageOf(Integer pageNum, Integer size) {
        return new Page<>(pageNum, size);
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

    @Override
    public String getOrderBy() {
        return orderBy;
    }

    public Page<T> setOrderBy(String orderBy) {
        this.orderBy = orderBy;
        return this;
    }

    @Override
    public String getIsAsc() {
        return isAsc;
    }

    public Page<T> setIsAsc(String isAsc) {
        this.isAsc = isAsc;
        return this;
    }

}
