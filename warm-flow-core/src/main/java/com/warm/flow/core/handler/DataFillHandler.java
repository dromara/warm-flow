package com.warm.flow.core.handler;

/**
 * @author minliuhua
 * @description: 数据填充handler，以下三个接口按照实际情况实现
 * @date: 2023/4/1 15:37
 */
public interface DataFillHandler {

    /**
     * id填充
     * @param object
     */
    void idFill(Object object);

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
