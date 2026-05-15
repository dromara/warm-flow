package com.ruoyi.system.service;

import com.ruoyi.system.domain.ContractProcess;

import java.util.List;

/**
 * 合同流程Service接口
 *
 * @author ruoyi
 * @date 2025-04-30
 */
public interface IContractProcessService
{
    /**
     * 查询合同流程
     *
     * @param id 合同流程主键
     * @return 合同流程
     */
    public ContractProcess selectContractProcessById(Long id);

    /**
     * 查询合同流程列表
     *
     * @param contractProcess 合同流程
     * @return 合同流程集合
     */
    public List<ContractProcess> selectContractProcessList(ContractProcess contractProcess);

    /**
     * 新增合同流程
     *
     * @param contractProcess 合同流程
     * @return 结果
     */
    public int insertContractProcess(ContractProcess contractProcess, String flowStatus);

    /**
     * 修改合同流程
     *
     * @param contractProcess 合同流程
     * @return 结果
     */
    public int updateContractProcess(ContractProcess contractProcess);

    /**
     * 批量删除合同流程
     *
     * @param ids 需要删除的合同流程主键集合
     * @return 结果
     */
    public int deleteContractProcessByIds(Long[] ids);

    /**
     * 删除合同流程信息
     *
     * @param id 合同流程主键
     * @return 结果
     */
    public int deleteContractProcessById(Long id);

    /**
     * 办理
     *
     * @param contractProcess
     * @param taskId
     * @param skipType
     * @param message
     * @param nodeCode
     * @param flowStatus 自定义流程状态扩展
     */
    int handle(ContractProcess contractProcess, Long taskId, String skipType, String message, String nodeCode, String flowStatus);

    /**
     * 驳回到上一个任务
     *
     * @param contractProcess
     * @param taskId
     * @param message
     * @param flowStatus 自定义流程状态扩展
     */
    int rejectLast(ContractProcess contractProcess, Long taskId, String message, String flowStatus);

    /**
     * 撤销
     * @param id 业务id
     */
    int revoke(String id);

    /**
     * 终止流程，提前结束
     * @param contractProcess
     */
    int termination(ContractProcess contractProcess);

    /**
     * 拿回到最近办理的任务
     * @param id 业务id
     */
    int taskBackByInsId(String id);

    /**
     * 拿回到最近办理的任务
     *
     * @param contractProcess
     * @param taskId
     * @param message
     * @param flowStatus 自定义流程状态扩展
     */
    int taskBack(ContractProcess contractProcess, Long taskId, String message, String flowStatus);
}
