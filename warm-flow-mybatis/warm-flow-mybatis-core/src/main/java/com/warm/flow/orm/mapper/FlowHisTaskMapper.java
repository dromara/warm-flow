package com.warm.flow.orm.mapper;

import com.warm.flow.orm.entity.FlowHisTask;
import org.apache.ibatis.annotations.Param;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * 历史任务记录Mapper接口
 *
 * @author warm
 * @date 2023-03-29
 */
public interface FlowHisTaskMapper extends WarmMapper<FlowHisTask> {

    /**
     * 根据nodeCode获取未退回的历史记录
     *
     * @param nodeCode
     * @param instanceId
     * @param entity
     * @return
     */
    List<FlowHisTask> getNoReject(@Param("nodeCode") String nodeCode, @Param("instanceId") Long instanceId
            , @Param("entity") FlowHisTask entity);

    /**
     * 根据instanceIds删除
     *
     * @param instanceIds 主键
     * @param entity
     * @return 结果
     */
    int deleteByInsIds(@Param("instanceIds") List<Long> instanceIds, @Param("entity") FlowHisTask entity);

    /**
     * 逻辑删除
     *
     * @param instanceIds 需要删除的数据主键集合
     * @return 结果
     */
    int updateByInsIdsLogic(@Param("instanceIds") Collection<? extends Serializable> instanceIds
            , @Param("entity") FlowHisTask entity, @Param("logicDeleteValue") String logicDeleteValue
            , @Param("logicNotDeleteValue") String logicNotDeleteValue);

    List<FlowHisTask> listByTaskIdAndCooperateTypes(@Param("cooperateTypes") Integer[] cooperateTypes
            , @Param("entity") FlowHisTask entity);
}
