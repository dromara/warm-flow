package org.dromara.warm.flow.ui.dto;

/**
 * @author vanlin
 * @className FormQuery
 * @description
 * @since 2024-11-11 15:42
 */
public class FormQuery {
    /**
     * 表单名称
     */
    private String formName;

    /**
     * 当前页码
     */
    private Integer pageNum;

    /**
     * 每页显示条数
     */
    private Integer pageSize;

    public String getFormName() {
        return formName;
    }

    public void setFormName(String formName) {
        this.formName = formName;
    }

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}
