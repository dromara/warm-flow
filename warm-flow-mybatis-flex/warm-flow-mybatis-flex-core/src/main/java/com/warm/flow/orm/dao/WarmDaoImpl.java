package com.warm.flow.orm.dao;

import com.mybatisflex.core.query.QueryWrapper;
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
        QueryWrapper queryWrapper = TenantDeleteUtil.getDefaultWrapper(newEntity());
        queryWrapper.eq("id", id);
        return getMapper().selectOneByQuery(queryWrapper);
    }

    /**
     * 根据ids查询
     *
     * @param ids 主键
     * @return 实体
     */
    @Override
    public List<T> selectByIds(Collection<? extends Serializable> ids) {
        QueryWrapper queryWrapper = TenantDeleteUtil.getDefaultWrapper(newEntity());
        queryWrapper.in(T::getId, ids);
        return getMapper().selectListByQuery(queryWrapper);
    }

    @Override
    public Page<T> selectPage(T entity, Page<T> page) {
        com.mybatisflex.core.paginate.Page<T> pageFlex =
                new com.mybatisflex.core.paginate.Page<>(
                        (page.getPageNum()/page.getPageSize())+1,
                        page.getPageSize()
                );
        QueryWrapper queryWrapper = TenantDeleteUtil.getDefaultWrapper(entity);
        if(StringUtils.isNotEmpty(page.getOrderBy())){
            queryWrapper.orderBy(page.getOrderBy(), "asc".equals(page.getIsAsc()));
        }
        pageFlex = getMapper().paginate(pageFlex, queryWrapper);
        if (ObjectUtil.isNotNull(pageFlex)) {
            return new Page<>(pageFlex.getRecords(), pageFlex.getTotalPage());
        }
        return Page.empty();
    }

    @Override
    public List<T> selectList(T entity, WarmQuery<T> query) {
        QueryWrapper queryWrapper = TenantDeleteUtil.getDefaultWrapper(entity);
        if (ObjectUtil.isNotNull(query)) {
            queryWrapper.orderBy(query.getOrderBy(), "asc".equals(query.getIsAsc()));
        }
        return getMapper().selectListByQuery(queryWrapper);
    }

    @Override
    public long selectCount(T entity) {
        QueryWrapper queryWrapper = TenantDeleteUtil.getDefaultWrapper(entity);
        return getMapper().selectCountByQuery(queryWrapper);
    }

    @Override
    public int save(T entity) {
        TenantDeleteUtil.fillEntity(entity);
        return getMapper().insert(entity);
    }

    @Override
    public int updateById(T entity) {
        QueryWrapper queryWrapper = TenantDeleteUtil.getIdWrapper(entity);
        return getMapper().updateByQuery(entity, true, queryWrapper);
    }

    @Override
    public int delete(T entity) {
        return delete(entity, null, null);
    }

    @Override
    public int deleteById(Serializable id) {
        return delete(newEntity(), (uq) -> uq.eq("id", id), (qw) -> qw.eq("id", id));
    }

    @Override
    public int deleteByIds(Collection<? extends Serializable> ids) {
        return delete(newEntity(), (uq) -> uq.in("id", ids), (qw) -> qw.in("id", ids));
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

    public int delete(T entry, Consumer<QueryWrapper> uq, Consumer<QueryWrapper> qw) {
        TenantDeleteUtil.delFillEntity(entry);
        QueryWrapper queryWrapper = TenantDeleteUtil.getDelWrapper(entry);
        if (StringUtils.isNotEmpty(entry.getDelFlag())) {
            if (ObjectUtil.isNotNull(uq)) {
                uq.accept(queryWrapper);
            }
            return getMapper().updateByQuery(entry, queryWrapper);
        }
        if (ObjectUtil.isNotNull(qw)) {
            qw.accept(queryWrapper);
        }
        return getMapper().deleteByQuery(queryWrapper);
    }

}
