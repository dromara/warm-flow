package com.warm.flow.orm.utils;

import com.warm.flow.core.entity.RootEntity;

/**
 * @author vanlin
 * @className JPAUpdateMergeFunction
 * @description
 * @since 2024/5/20 17:48
 */
@FunctionalInterface
public interface JPAUpdateMergeFunction<E extends RootEntity> {
    void merge(E source);
}
