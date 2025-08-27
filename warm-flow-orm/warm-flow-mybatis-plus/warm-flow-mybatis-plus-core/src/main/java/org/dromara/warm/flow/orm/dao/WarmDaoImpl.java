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
package org.dromara.warm.flow.orm.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.enums.SqlKeyword;
import org.dromara.warm.flow.core.entity.RootEntity;
import org.dromara.warm.flow.core.orm.agent.WarmQuery;
import org.dromara.warm.flow.core.orm.dao.WarmDao;
import org.dromara.warm.flow.core.utils.ObjectUtil;
import org.dromara.warm.flow.core.utils.StringUtils;
import org.dromara.warm.flow.core.utils.page.Page;
import org.dromara.warm.flow.orm.mapper.WarmMapper;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * BaseMapper接口
 *
 * @author warm
 * @since 2023-03-17
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
        return getMapper().selectById(id);
    }

    /**
     * 根据ids查询
     *
     * @param ids 主键
     * @return 实体
     */
    @Override
    public List<T> selectByIds(Collection<? extends Serializable> ids) {
        return getMapper().selectBatchIds(ids);
    }

    @Override
    public Page<T> selectPage(T entity, Page<T> page) {
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<T> pagePlus =
            new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page.getPageNum(), page.getPageSize());

        QueryWrapper<T> queryWrapper = new QueryWrapper<>(entity);
        queryWrapper.orderBy(StringUtils.isNotEmpty(page.getOrderBy())
            , page.getIsAsc().equals(SqlKeyword.ASC.getSqlSegment()), page.getOrderBy());

        com.baomidou.mybatisplus.extension.plugins.pagination.Page<T> tPage = getMapper().selectPage(pagePlus, queryWrapper);

        if (ObjectUtil.isNotNull(tPage)) {
            Page<T> rPage = new Page<>(tPage.getRecords(), tPage.getTotal());
            rPage.setPageNum(page.getPageNum());
            rPage.setPageSize(page.getPageSize());
            return rPage;
        }
        return Page.empty();
    }

    @Override
    public List<T> selectList(T entity, WarmQuery<T> query) {
        QueryWrapper<T> queryWrapper = new QueryWrapper<>(entity);
        if (ObjectUtil.isNotNull(query)) {
            queryWrapper.orderBy(StringUtils.isNotEmpty(query.getOrderBy())
                , query.getIsAsc().equals(SqlKeyword.ASC.getSqlSegment()), query.getOrderBy());
        }
        return getMapper().selectList(queryWrapper);
    }

    @Override
    public long selectCount(T entity) {
        LambdaQueryWrapper<T> queryWrapper = new LambdaQueryWrapper<>(entity);
        queryWrapper.setEntityClass((Class<T>) entity.getClass());
        return getMapper().selectCount(queryWrapper);
    }

    @Override
    public int save(T entity) {
        return getMapper().insert(entity);
    }

    @Override
    public int updateById(T entity) {
        LambdaQueryWrapper<T> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.setEntityClass((Class<T>) entity.getClass());
        queryWrapper.eq(T::getId, entity.getId());
        return getMapper().update(entity, queryWrapper);
    }

    @Override
    public int delete(T entity) {
        LambdaQueryWrapper<T> queryWrapper = new LambdaQueryWrapper<>(entity);
        return getMapper().delete(queryWrapper);
    }

    @Override
    public int deleteById(Serializable id) {
        return getMapper().deleteById(id);
    }

    @Override
    public int deleteByIds(Collection<? extends Serializable> ids) {
        return getMapper().deleteBatchIds(ids);
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

}
