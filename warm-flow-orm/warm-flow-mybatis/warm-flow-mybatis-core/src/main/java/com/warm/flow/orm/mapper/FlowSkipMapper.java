package com.warm.flow.orm.mapper;

import com.warm.flow.orm.entity.FlowSkip;
import org.apache.ibatis.annotations.Param;

import java.io.Serializable;
import java.util.Collection;

/**
 * 节点跳转关联Mapper接口
 *
 * @author warm
 * @date 2023-03-29
 */
public interface FlowSkipMapper extends WarmMapper<FlowSkip> {

    /**
     * 批量删除节点跳转关联
     *
     * @param defIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSkipByDefIds(@Param("defIds") Collection<? extends Serializable> defIds, @Param("entity") FlowSkip entity);

    /**
     * 逻辑删除
     *
     * @param defIds 需要删除的数据主键集合
     * @return 结果
     */
    int updateSkipByDefIdsLogic(@Param("defIds") Collection<? extends Serializable> defIds, @Param("entity") FlowSkip entity
            , @Param("logicDeleteValue") String logicDeleteValue
            , @Param("logicNotDeleteValue") String logicNotDeleteValue);
}
