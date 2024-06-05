package com.warm.flow.core.dao;

import com.warm.flow.core.entity.HisTask;

import java.util.List;

/**
 * 历史任务记录Mapper接口
 *
 * @author warm
 * @date 2023-03-29
 */
public interface FlowHisTaskDao<T extends HisTask> extends WarmDao<T> {

    /**
     * 根据nodeCode获取未退回的历史记录
     *
     * @param nodeCode
     * @param targetNodeCode
     * @param instanceId
     * @return
     */
    List<T> getNoReject(String nodeCode,String targetNodeCode, Long instanceId);

    /**
     * 根据instanceIds删除
     *
     * @param instanceIds 主键
     * @return 结果
     */
    int deleteByInsIds(List<Long> instanceIds);

    /**
     * 根据任务id和协作类型查询
     * @param taskId
     * @param cooperateTypes
     * @return
     */
    List<T> listByTaskIdAndCooperateTypes(Long taskId, Integer[] cooperateTypes);
}
