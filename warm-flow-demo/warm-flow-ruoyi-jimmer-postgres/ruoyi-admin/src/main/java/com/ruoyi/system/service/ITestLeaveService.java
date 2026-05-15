package com.ruoyi.system.service;

import com.ruoyi.system.domain.TestLeave;

import java.util.List;

/**
 * OA 请假申请Service接口
 *
 * @author ruoyi
 * @date 2024-03-07
 */
public interface ITestLeaveService
{
    /**
     * 查询OA 请假申请
     *
     * @param id OA 请假申请主键
     * @return OA 请假申请
     */
    public TestLeave selectTestLeaveById(String id);

    /**
     * 查询OA 请假申请列表
     *
     * @param testLeave OA 请假申请
     * @return OA 请假申请集合
     */
    public List<TestLeave> selectTestLeaveList(TestLeave testLeave);

    /**
     * 新增OA 请假申请
     *
     * @param testLeave OA 请假申请
     * @param flowStatus 自定义流程状态扩展
     * @return 结果
     */
    public int insertTestLeave(TestLeave testLeave, String flowStatus);

    /**
     * 修改OA 请假申请
     *
     * @param testLeave OA 请假申请
     * @return 结果
     */
    public int updateTestLeave(TestLeave testLeave);

    /**
     * 批量删除OA 请假申请
     *
     * @param ids 需要删除的OA 请假申请主键集合
     * @return 结果
     */
    public int deleteTestLeaveByIds(String[] ids);

    /**
     * 删除OA 请假申请信息
     *
     * @param id OA 请假申请主键
     * @return 结果
     */
    public int deleteTestLeaveById(String id);

    /**
     * 提交审批
     *
     * @param id
     * @param flowStatus 自定义流程状态扩展
     */
    public int submit(String id, String flowStatus);

    /**
     * 办理
     *
     * @param testLeave
     * @param taskId
     * @param skipType
     * @param message
     * @param nodeCode
     * @param flowStatus 自定义流程状态扩展
     */
    int handle(TestLeave testLeave, Long taskId, String skipType, String message, String nodeCode, String flowStatus);

    /**
     * 驳回到上一个任务
     *
     * @param testLeave
     * @param taskId
     * @param message
     * @param flowStatus 自定义流程状态扩展
     */
    int rejectLast(TestLeave testLeave, Long taskId, String message, String flowStatus);

    /**
     * 撤销
     * @param id 业务id
     */
    int revoke(String id);

    /**
     * 终止流程，提前结束
     * @param testLeave
     */
    int termination(TestLeave testLeave);

    /**
     * 拿回到最近办理的任务
     * @param id 业务id
     */
    int taskBackByInsId(String id);

    /**
     * 拿回到最近办理的任务
     *
     * @param testLeave
     * @param taskId
     * @param message
     * @param flowStatus 自定义流程状态扩展
     */
    int taskBack(TestLeave testLeave, Long taskId, String message, String flowStatus);

    /**
     * 暂存审批
     * @param id 业务id
     * @param flowStatus 自定义流程状态扩展
     */
    int pending(String id, String flowStatus);
}
