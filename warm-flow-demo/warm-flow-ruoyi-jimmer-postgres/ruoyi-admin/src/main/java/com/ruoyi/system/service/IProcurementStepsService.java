package com.ruoyi.system.service;

import com.ruoyi.system.domain.ProcurementSteps;

import java.util.List;

/**
 * 企业采购流程表Service接口
 *
 * @author ruoyi
 * @date 2025-05-08
 */
public interface IProcurementStepsService
{
    /**
     * 查询企业采购流程表
     *
     * @param id 企业采购流程表主键
     * @return 企业采购流程表
     */
    public ProcurementSteps selectProcurementStepsById(Long id);

    /**
     * 查询企业采购流程表列表
     *
     * @param procurementSteps 企业采购流程表
     * @return 企业采购流程表集合
     */
    public List<ProcurementSteps> selectProcurementStepsList(ProcurementSteps procurementSteps);

    /**
     * 新增企业采购流程表
     *
     * @param procurementSteps 企业采购流程表
     * @return 结果
     */
    public int insertProcurementSteps(ProcurementSteps procurementSteps);

    /**
     * 修改企业采购流程表
     *
     * @param procurementSteps 企业采购流程表
     * @return 结果
     */
    public int updateProcurementSteps(ProcurementSteps procurementSteps);

    /**
     * 批量删除企业采购流程表
     *
     * @param ids 需要删除的企业采购流程表主键集合
     * @return 结果
     */
    public int deleteProcurementStepsByIds(Long[] ids);

    /**
     * 删除企业采购流程表信息
     *
     * @param id 企业采购流程表主键
     * @return 结果
     */
    public int deleteProcurementStepsById(Long id);

    /**
     * 办理
     *
     * @param procurementSteps
     * @param taskId
     * @param skipType
     * @param message
     * @param nodeCode
     * @param flowStatus 自定义流程状态扩展
     */
    int handle(ProcurementSteps procurementSteps, Long taskId, String skipType, String message, String nodeCode, String flowStatus);

    /**
     * 驳回到上一个任务
     *
     * @param procurementSteps
     * @param taskId
     * @param message
     * @param flowStatus 自定义流程状态扩展
     */
    int rejectLast(ProcurementSteps procurementSteps, Long taskId, String message, String flowStatus);

    /**
     * 撤销
     * @param id 业务id
     */
    int revoke(String id);

    /**
     * 终止流程，提前结束
     * @param procurementSteps
     */
    int termination(ProcurementSteps procurementSteps);

    /**
     * 拿回到最近办理的任务
     * @param id 业务id
     */
    int taskBackByInsId(String id);

    /**
     * 拿回到最近办理的任务
     *
     * @param procurementSteps
     * @param taskId
     * @param message
     * @param flowStatus 自定义流程状态扩展
     */
    int taskBack(ProcurementSteps procurementSteps, Long taskId, String message, String flowStatus);
}
