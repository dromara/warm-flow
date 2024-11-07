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
package org.dromara.warm.flow.solon.jpa;

import org.noear.solon.data.tran.TranListener;
import org.noear.solon.data.tran.TranUtils;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.metamodel.Metamodel;
import java.util.List;
import java.util.Map;

/**
 * @author vanlin
 * @className EntityManagerProxy
 * @description
 * @since 2024/7/5 15:16
 */
public class EntityManagerProxy implements EntityManager {
    private final EntityManager entityManager;

    public EntityManagerProxy(final EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    private <T extends EntityManager> T tranTry(T entityManager){
        if(TranUtils.inTrans()){
            EntityTransaction transaction = entityManager.getTransaction();

            if(!transaction.isActive()) {
                transaction.begin();

                TranUtils.listen(new TranListener() {
                    @Override
                    public void beforeCommit(boolean readOnly) throws Throwable {
                        if (readOnly) {
                            transaction.setRollbackOnly();
                        }
                        transaction.commit();
                    }

                    @Override
                    public void afterCompletion(int status) {
                        if (status == TranListener.STATUS_ROLLED_BACK) {
                            transaction.rollback();
                        }
                    }
                });
            }
        }

        return entityManager;
    }

    @Override
    public void persist(Object entity) {
        tranTry(entityManager);
        entityManager.persist(entity);
    }

    @Override
    public <T> T merge(T entity) {
        tranTry(entityManager);
        return entityManager.merge(entity);
    }

    @Override
    public void remove(Object entity) {
        tranTry(entityManager);
        entityManager.remove(entity);
    }

    @Override
    public <T> T find(Class<T> entityClass, Object primaryKey) {
        tranTry(entityManager);
        return entityManager.find(entityClass, primaryKey);
    }

    @Override
    public <T> T find(Class<T> entityClass, Object primaryKey, Map<String, Object> properties) {
        tranTry(entityManager);
        return entityManager.find(entityClass, primaryKey, properties);
    }

    @Override
    public <T> T find(Class<T> entityClass, Object primaryKey, LockModeType lockMode) {
        tranTry(entityManager);
        return entityManager.find(entityClass, primaryKey, lockMode);
    }

    @Override
    public <T> T find(Class<T> entityClass, Object primaryKey, LockModeType lockMode, Map<String, Object> properties) {
        tranTry(entityManager);
        return entityManager.find(entityClass, primaryKey, lockMode, properties);
    }

    @Override
    public <T> T getReference(Class<T> entityClass, Object primaryKey) {
        tranTry(entityManager);
        return entityManager.getReference(entityClass, primaryKey);
    }

    @Override
    public void flush() {
        tranTry(entityManager);
        entityManager.flush();
    }

    @Override
    public void setFlushMode(FlushModeType flushMode) {
        entityManager.setFlushMode(flushMode);
    }

    @Override
    public FlushModeType getFlushMode() {
        return entityManager.getFlushMode();
    }

    @Override
    public void lock(Object entity, LockModeType lockMode) {
        tranTry(entityManager);
        entityManager.lock(entity, lockMode);
    }

    @Override
    public void lock(Object entity, LockModeType lockMode, Map<String, Object> properties) {
        tranTry(entityManager);
        entityManager.lock(entity, lockMode, properties);
    }

    @Override
    public void refresh(Object entity) {
        tranTry(entityManager);
        entityManager.refresh(entity);
    }

    @Override
    public void refresh(Object entity, Map<String, Object> properties) {
        tranTry(entityManager);
        entityManager.refresh(entity, properties);
    }

    @Override
    public void refresh(Object entity, LockModeType lockMode) {
        tranTry(entityManager);
        entityManager.refresh(entity, lockMode);
    }

    @Override
    public void refresh(Object entity, LockModeType lockMode, Map<String, Object> properties) {
        tranTry(entityManager);
        entityManager.refresh(entity, lockMode, properties);
    }

    @Override
    public void clear() {
        tranTry(entityManager);
        entityManager.clear();
    }

    @Override
    public void detach(Object entity) {
        tranTry(entityManager);
        entityManager.detach(entity);
    }

    @Override
    public boolean contains(Object entity) {
        tranTry(entityManager);
        return entityManager.contains(entity);
    }

    @Override
    public LockModeType getLockMode(Object entity) {
        tranTry(entityManager);
        return entityManager.getLockMode(entity);
    }

    @Override
    public void setProperty(String propertyName, Object value) {
        tranTry(entityManager);
        entityManager.setProperty(propertyName, value);
    }

    @Override
    public Map<String, Object> getProperties() {
        tranTry(entityManager);
        return entityManager.getProperties();
    }

    @Override
    public Query createQuery(String qlString) {
        tranTry(entityManager);
        return entityManager.createQuery(qlString);
    }

    @Override
    public <T> TypedQuery<T> createQuery(CriteriaQuery<T> criteriaQuery) {
        tranTry(entityManager);
        return entityManager.createQuery(criteriaQuery);
    }

    @Override
    public Query createQuery(CriteriaUpdate updateQuery) {
        tranTry(entityManager);
        return entityManager.createQuery(updateQuery);
    }

    @Override
    public Query createQuery(CriteriaDelete deleteQuery) {
        tranTry(entityManager);
        return entityManager.createQuery(deleteQuery);
    }

    @Override
    public <T> TypedQuery<T> createQuery(String qlString, Class<T> resultClass) {
        tranTry(entityManager);
        return entityManager.createQuery(qlString, resultClass);
    }

    @Override
    public Query createNamedQuery(String name) {
        tranTry(entityManager);
        return entityManager.createNamedQuery(name);
    }

    @Override
    public <T> TypedQuery<T> createNamedQuery(String name, Class<T> resultClass) {
        tranTry(entityManager);
        return entityManager.createNamedQuery(name, resultClass);
    }

    @Override
    public Query createNativeQuery(String sqlString) {
        tranTry(entityManager);
        return entityManager.createNativeQuery(sqlString);
    }

    @Override
    public Query createNativeQuery(String sqlString, Class resultClass) {
        tranTry(entityManager);
        return entityManager.createNativeQuery(sqlString, resultClass);
    }

    @Override
    public Query createNativeQuery(String sqlString, String resultSetMapping) {
        tranTry(entityManager);
        return entityManager.createNativeQuery(sqlString, resultSetMapping);
    }

    @Override
    public StoredProcedureQuery createNamedStoredProcedureQuery(String name) {
        tranTry(entityManager);
        return entityManager.createNamedStoredProcedureQuery(name);
    }

    @Override
    public StoredProcedureQuery createStoredProcedureQuery(String procedureName) {
        tranTry(entityManager);
        return entityManager.createStoredProcedureQuery(procedureName);
    }

    @Override
    public StoredProcedureQuery createStoredProcedureQuery(String procedureName, Class... resultClasses) {
        tranTry(entityManager);
        return entityManager.createStoredProcedureQuery(procedureName, resultClasses);
    }

    @Override
    public StoredProcedureQuery createStoredProcedureQuery(String procedureName, String... resultSetMappings) {
        tranTry(entityManager);
        return entityManager.createStoredProcedureQuery(procedureName, resultSetMappings);
    }

    @Override
    public void joinTransaction() {
        entityManager.joinTransaction();
    }

    @Override
    public boolean isJoinedToTransaction() {
        return entityManager.isJoinedToTransaction();
    }

    @Override
    public <T> T unwrap(Class<T> cls) {
        return entityManager.unwrap(cls);
    }

    @Override
    public Object getDelegate() {
        return entityManager.getDelegate();
    }

    @Override
    public void close() {
        entityManager.close();
    }

    @Override
    public boolean isOpen() {
        return entityManager.isOpen();
    }

    @Override
    public EntityTransaction getTransaction() {
        return entityManager.getTransaction();
    }

    @Override
    public EntityManagerFactory getEntityManagerFactory() {
        return entityManager.getEntityManagerFactory();
    }

    @Override
    public CriteriaBuilder getCriteriaBuilder() {
        return entityManager.getCriteriaBuilder();
    }

    @Override
    public Metamodel getMetamodel() {
        return entityManager.getMetamodel();
    }

    @Override
    public <T> EntityGraph<T> createEntityGraph(Class<T> rootType) {
        return entityManager.createEntityGraph(rootType);
    }

    @Override
    public EntityGraph<?> createEntityGraph(String graphName) {
        return entityManager.createEntityGraph(graphName);
    }

    @Override
    public EntityGraph<?> getEntityGraph(String graphName) {
        return entityManager.getEntityGraph(graphName);
    }

    @Override
    public <T> List<EntityGraph<? super T>> getEntityGraphs(Class<T> entityClass) {
        return entityManager.getEntityGraphs(entityClass);
    }
}
