package com.warm.flow.orm.utils;

/**
 * @author vanlin
 * @className OrderByFunction
 * @description
 * @since 2024/5/12 11:11
 */
@FunctionalInterface
public interface JPAQueryFunction<CB, ROOT, LIST, CQ> {
    void process(CB cb, ROOT root, LIST list, CQ cq);
}
