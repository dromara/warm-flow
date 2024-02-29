package com.warm.flow.orm.handler;

/**
 * @author minliuhua
 * @description: 数据填充handler
 * @date: 2023/4/1 15:37
 */
public interface DataFillHandler {

    /**
     * 新增填充
     *
     * @param object
     */
    void insertFill(Object object);

    /**
     * 设置更新常用参数
     *
     * @param object
     */
    void updateFill(Object object);
}
