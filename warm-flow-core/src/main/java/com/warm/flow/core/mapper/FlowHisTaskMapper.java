package com.warm.flow.core.mapper;

import com.warm.flow.core.domain.entity.FlowHisTask;
import com.warm.mybatis.core.mapper.WarmMapper;
import com.warm.mybatis.core.page.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 历史任务记录Mapper接口
 *
 * @author warm
 * @date 2023-03-29
 */
public interface FlowHisTaskMapper extends WarmMapper<FlowHisTask> {

    /**
     * 根据实例id获取历史记录集合
     *
     * @param instanceId
     * @return
     */
    List<FlowHisTask> getByInsId(Long instanceId);

    /**
     * 根据nodeCode获取未驳回的历史记录
     *
     * @param nodeCode
     * @return
     */
    List<FlowHisTask> getNoReject(String nodeCode);

    /**
     * 根据instanceIds删除
     *
     * @param instanceIds 主键
     * @return 结果
     */
    int deleteByInsIds(List<Long> instanceIds);

    /**
     * 获取已办任务数量
     *
     * @param flowHisTask
     * @param page
     * @return
     */
    long countDone(@Param("flowHisTask") FlowHisTask flowHisTask
            , @Param("page") Page<FlowHisTask> page);

    /**
     * 获取最新的已办任务
     *
     * @param flowHisTask
     * @param page
     * @return
     */
    List<FlowHisTask> donePage(@Param("flowHisTask") FlowHisTask flowHisTask
            , @Param("page") Page<FlowHisTask> page);

    /**
     * 获取已办任务
     *
     * @param flowHisTask
     * @param page
     * @return
     */
    List<FlowHisTask> doneAllPage(@Param("flowHisTask") FlowHisTask flowHisTask
            , @Param("page") Page<FlowHisTask> page);

}
