package com.warm.flow.core.service;

import com.warm.flow.core.domain.entity.FlowHisTask;
import com.warm.mybatis.core.page.Page;
import com.warm.mybatis.core.service.IFlowService;

import java.util.List;

/**
 * 历史任务记录Service接口
 *
 * @author hh
 * @date 2023-03-29
 */
public interface IFlowHisTaskService extends IFlowService<FlowHisTask> {

    /**
     * 根据instanceId查询出流程相关的记录(根据时间逆序)
     *
     * @param instanceId
     * @return
     */
    List<FlowHisTask> getByInsIds(Long instanceId);

    /**
     * 根据instanceIds删除
     *
     * @param instanceIds
     * @return
     */
    boolean deleteByInsIds(List<Long> instanceIds);

    /**
     * 获取已办任务
     *
     * @param flowHisTask
     * @param page
     * @return
     */
    Page<FlowHisTask> donePage(FlowHisTask flowHisTask, Page<FlowHisTask> page);

}
