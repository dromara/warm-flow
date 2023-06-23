package com.monkey.flow.core.service;

import com.monkey.flow.core.domain.entity.FlowHisTask;
import com.monkey.mybatis.core.page.Page;
import com.monkey.mybatis.core.service.IFlowService;

import java.util.List;

/**
 * 历史任务记录Service接口
 *
 * @author hh
 * @date 2023-03-29
 */
public interface IFlowHisTaskService extends IFlowService<FlowHisTask> {
    /**
     * 根据instanceIds查询出流程相关的记录(根据时间逆序)
     *
     * @param instanceIds
     * @return
     */
    @Deprecated
    List<FlowHisTask> getByNewInsIds(List<Long> instanceIds);

    /**
     * 根据instanceIds查询出流程相关的记录(根据时间逆序)
     *
     * @param instanceIds
     * @return
     */
    List<FlowHisTask> getByInsIds(List<Long> instanceIds);

    /**
     * 根据instanceIds删除
     * @param instanceIds
     * @return
     */
    boolean deleteByInsIds(List<Long> instanceIds);

    /**
     * 获取已办任务
     * @param flowHisTask
     * @param page
     * @return
     */
    Page<FlowHisTask> donePage(FlowHisTask flowHisTask, Page<FlowHisTask> page);

}
