package com.warm.flow.core.service;

import com.warm.flow.core.dto.FlowParams;
import com.warm.flow.core.entity.HisTask;
import com.warm.flow.core.entity.Node;
import com.warm.flow.core.entity.Task;
import com.warm.flow.core.entity.User;
import com.warm.flow.core.orm.service.IWarmService;

import java.math.BigDecimal;
import java.util.List;

/**
 * 历史任务记录Service接口
 *
 * @author warm
 * @date 2023-03-29
 */
public interface HisTaskService extends IWarmService<HisTask> {

    /**
     * 根据任务id和协作类型查询
     * @param taskId
     * @param cooperateTypes
     * @return
     */
    List<HisTask> listByTaskIdAndCooperateTypes(Long taskId, Integer... cooperateTypes);

    /**
     * 根据nodeCode获取未退回的历史记录
     *
     * @param nodeCode
     * @param instanceId
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
     * 设置流程历史任务信息
     *
     * @param task  当前任务
     * @param nextNodes 后续任务
     * @param flowParams 参数
     */
    List<HisTask> setSkipInsHis(Task task, List<Node> nextNodes, FlowParams flowParams);

    /**
     * 委派历史任务
     * @param task
     * @param flowParams
     * @param entrustedUser
     * @return
     */
    HisTask setDeputeHisTask(Task task, FlowParams flowParams, User entrustedUser);

    /**
     * 设置会签票签历史任务
     * @param task
     * @param flowParams
     * @param nodeRatio
     * @param isPass
     * @return
     */
    HisTask setSignHisTask(Task task, FlowParams flowParams, BigDecimal nodeRatio, boolean isPass);

    /**
     * 自动完成历史任务
     * @param flowStatus
     * @param task
     * @param userList
     */
    List<HisTask> autoHisTask(Integer flowStatus, Task task, List<User> userList, Integer cooperateType);

}
