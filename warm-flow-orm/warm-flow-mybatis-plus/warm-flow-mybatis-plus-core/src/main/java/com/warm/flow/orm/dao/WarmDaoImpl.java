package com.warm.flow.orm.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.enums.SqlKeyword;
import com.warm.flow.core.dao.WarmDao;
import com.warm.flow.core.entity.RootEntity;
import com.warm.flow.core.orm.agent.WarmQuery;
import com.warm.flow.core.utils.ObjectUtil;
import com.warm.flow.core.utils.StringUtils;
import com.warm.flow.core.utils.page.Page;
import com.warm.flow.orm.mapper.WarmMapper;
import com.warm.flow.orm.utils.TenantDeleteUtil;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

/**
 * BaseMapper接口
 *
 * @author warm
 * @date 2023-03-17
 */
public abstract class WarmDaoImpl<T extends RootEntity> implements WarmDao<T> {

    public abstract WarmMapper<T> getMapper();

    /**
     * 根据id查询
     *
     * @param id 主键
     * @return 实体
     */
    @Override
    public T selectById(Serializable id) {
        LambdaQueryWrapper<T> queryWrapper = TenantDeleteUtil.getLambdaWrapper(newEntity());
        queryWrapper.eq(T::getId, id);
        return getMapper().selectOne(queryWrapper);
    }

    /**
     * 根据ids查询
     *
     * @param ids 主键
     * @return 实体
     */
    @Override
    public List<T> selectByIds(Collection<? extends Serializable> ids) {
        LambdaQueryWrapper<T> queryWrapper = TenantDeleteUtil.getLambdaWrapper(newEntity());
        queryWrapper.in(T::getId, ids);
        queryWrapper.orderBy(true, false, T::getId);
        return getMapper().selectList(queryWrapper);
    }

    @Override
    public Page<T> selectPage(T entity, Page<T> page) {
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<T> pagePlus =
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page.getPageNum(), page.getPageSize());

        QueryWrapper<T> queryWrapper = TenantDeleteUtil.getQueryWrapperDefault(entity);

        queryWrapper.orderBy(StringUtils.isNotEmpty(page.getOrderBy())
                , page.getIsAsc().equals(SqlKeyword.ASC.getSqlSegment()), page.getOrderBy());

        com.baomidou.mybatisplus.extension.plugins.pagination.Page<T> tPage
                = getMapper().selectPage(pagePlus, queryWrapper);

        if (ObjectUtil.isNotNull(tPage)) {
            return new Page<>(tPage.getRecords(), tPage.getTotal());
        }
        return Page.empty();
    }

    @Override
    public List<T> selectList(T entity, WarmQuery<T> query) {
        QueryWrapper<T> queryWrapper = TenantDeleteUtil.getQueryWrapperDefault(entity);
        if (ObjectUtil.isNotNull(query)) {
            queryWrapper.orderBy(StringUtils.isNotEmpty(query.getOrderBy())
                    , query.getIsAsc().equals(SqlKeyword.ASC.getSqlSegment()), query.getOrderBy());
        }
        return getMapper().selectList(queryWrapper);
    }

    @Override
    public long selectCount(T entity) {
        LambdaQueryWrapper<T> queryWrapper = TenantDeleteUtil.getLambdaWrapperDefault(entity);
        return getMapper().selectCount(queryWrapper);
    }

    @Override
    public int save(T entity) {
        TenantDeleteUtil.fillEntity(entity);
        return getMapper().insert(entity);
    }

    @Override
    public int updateById(T entity) {
        LambdaQueryWrapper<T> queryWrapper = TenantDeleteUtil.getLambdaWrapperDefault(newEntity());
        queryWrapper.eq(T::getId, entity.getId());
        return getMapper().update(entity, queryWrapper);
    }

    @Override
    public int delete(T entity) {
        return delete(entity, null, null);
    }

    @Override
    public int deleteById(Serializable id) {
        return delete(newEntity(), (luw) -> luw.eq(T::getId, id), (lqw) -> lqw.eq(T::getId, id));
    }

    @Override
    public int deleteByIds(Collection<? extends Serializable> ids) {
        return delete(newEntity(), (luw) -> luw.in(T::getId, ids), (lqw) -> lqw.in(T::getId, ids));
    }

    @Override
    public void saveBatch(List<T> list) {
        for (T record : list) {
            save(record);
        }
    }

    @Override
    public void updateBatch(List<T> list) {
        for (T record : list) {
            updateById(record);
        }
    }

    public int delete(T newEntity, Consumer<LambdaUpdateWrapper<T>> uwConsumer, Consumer<LambdaQueryWrapper<T>> qwConsumer) {
        LambdaUpdateWrapper<T> lambdaUpdateWrapper = TenantDeleteUtil.deleteWrapper(newEntity);
        if (ObjectUtil.isNotNull(lambdaUpdateWrapper)) {
            if (ObjectUtil.isNotNull(uwConsumer)) {
                uwConsumer.accept(lambdaUpdateWrapper);
            }
            return getMapper().update(null, lambdaUpdateWrapper);
        }
        LambdaQueryWrapper<T> lqw = new LambdaQueryWrapper<>(newEntity);
        if (ObjectUtil.isNotNull(qwConsumer)) {
            qwConsumer.accept(lqw);
        }
        return getMapper().delete(lqw);
    }

}
