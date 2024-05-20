package com.warm.flow.orm.utils;

import com.warm.flow.core.entity.RootEntity;
import com.warm.flow.orm.entity.JPARootEntity;

import javax.persistence.EntityManager;

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
