package com.warm.flow.orm.mapper;

import com.warm.flow.orm.entity.FlowSkip;
import com.warm.flow.orm.entity.FlowTask;
import org.apache.ibatis.annotations.Param;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * 待办任务Mapper接口
 *
 * @author warm
 * @date 2023-03-29
 */
public interface FlowTaskMapper extends WarmMapper<FlowTask> {

    /**
     * 根据instanceIds删除
     *
     * @param instanceIds 主键
     * @return 结果
     */
    int deleteByInsIds(@Param("instanceIds") Collection<? extends Serializable> instanceIds
            , @Param("entity") FlowTask entity);

    /**
     * 逻辑删除
     *
     * @param instanceIds 需要删除的数据主键集合
     * @return 结果
     */
    int updateByInsIdsLogic(@Param("instanceIds") Collection<? extends Serializable> instanceIds
            , @Param("entity") FlowTask entity
            , @Param("logicDeleteValue") String logicDeleteValue
            , @Param("logicNotDeleteValue") String logicNotDeleteValue);
}
