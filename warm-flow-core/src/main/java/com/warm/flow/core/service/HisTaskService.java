package com.warm.flow.core.service;

import com.warm.flow.core.dto.FlowParams;
import com.warm.flow.core.entity.HisTask;
import com.warm.flow.core.entity.Node;
import com.warm.flow.core.entity.Task;
import com.warm.flow.core.orm.service.IWarmService;
import com.warm.tools.utils.page.Page;

import java.util.List;

/**
 * 历史任务记录Service接口
 *
 * @author warm
 * @date 2023-03-29
 */
public interface HisTaskService extends IWarmService<HisTask> {

    /**
     * 设置流程历史任务信息
     *
     * @param task
     * @param nextNodes
     * @return
     */
    HisTask setSkipInsHis(Task task, List<Node> nextNodes, FlowParams flowParams);

    /**
     * 根据nodeCode获取未驳回的历史记录
     *
     * @param nodeCode
     * @param instanceId
     * @return
     */
    List<HisTask> getNoReject(String nodeCode, Long instanceId);

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
     * @param hisTask
     * @param page
     * @return
     */
    @Deprecated
    Page<HisTask> donePage(HisTask hisTask, Page<HisTask> page);

}
