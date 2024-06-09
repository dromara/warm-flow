package com.warm.flow.orm.utils;

/**
 * @author vanlin
 * @className UpdateFunction
 * @description
 * @since 2024/5/12 11:11
 */
@FunctionalInterface
public interface JPAUpdateFunction<CB, ROOT, LIST, CU> {
    void process(CB cb, ROOT root, LIST list, CU cu);
}
