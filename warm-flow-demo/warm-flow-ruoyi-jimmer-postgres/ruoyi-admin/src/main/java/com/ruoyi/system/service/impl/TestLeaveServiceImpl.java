package com.ruoyi.system.service.impl;

import com.alibaba.fastjson2.JSON;
import com.ruoyi.common.core.domain.entity.SysDictData;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.flow.utils.PermissionsUtil;
import com.ruoyi.system.domain.TestLeave;
import com.ruoyi.system.mapper.TestLeaveMapper;
import com.ruoyi.system.service.ISysDictTypeService;
import com.ruoyi.system.service.ITestLeaveService;
import org.dromara.warm.flow.core.FlowEngine;
import org.dromara.warm.flow.core.dto.FlowParams;
import org.dromara.warm.flow.core.entity.Instance;
import org.dromara.warm.flow.core.entity.Task;
import org.dromara.warm.flow.core.entity.User;
import org.dromara.warm.flow.core.enums.SkipType;
import org.dromara.warm.flow.core.service.InsService;
import org.dromara.warm.flow.core.service.TaskService;
import org.dromara.warm.flow.core.utils.CollUtil;
import org.dromara.warm.flow.core.utils.IdUtils;
import org.dromara.warm.flow.core.utils.StreamUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * OA 请假申请Service业务层处理
 *
 * @author ruoyi
 * @date 2024-03-07
 */
@Service
public class TestLeaveServiceImpl implements ITestLeaveService
{
    @Resource
    private TestLeaveMapper testLeaveMapper;

    @Resource
    private InsService insService;

    @Resource
    private TaskService taskService;

    @Resource
    private ISysDictTypeService sysDictTypeService;

    /**
     * 查询OA 请假申请
     *
     * @param id OA 请假申请主键
     * @return OA 请假申请
     */
    @Override
    public TestLeave selectTestLeaveById(String id)
    {
        TestLeave testLeave = testLeaveMapper.selectTestLeaveById(id);
        List<String> permission = FlowEngine.userService().getPermission(testLeave.getInstanceId(), "4");
        if (CollUtil.isNotEmpty(permission)) {
            testLeave.setAdditionalHandler(permission);
        }else {
            testLeave.setAdditionalHandler(new ArrayList<>());
        }
        return testLeave;
    }

    /**
     * 查询OA 请假申请列表
     *
     * @param testLeave OA 请假申请
     * @return OA 请假申请
     */
    @Override
    public List<TestLeave> selectTestLeaveList(TestLeave testLeave)
    {
        return testLeaveMapper.selectTestLeaveList(testLeave);
    }

    /**
     * 新增OA 请假申请
     *
     * @param testLeave OA 请假申请
     * @return 结果
     */
    @Override
    public int insertTestLeave(TestLeave testLeave, String flowStatus)
    {
        // 设置流转参数
        String id = IdUtils.nextIdStr();
        testLeave.setId(id);
        LoginUser user = SecurityUtils.getLoginUser();
        // 从字典表中获取流程编码
        String flowCode = getFlowType(testLeave);
        // 传递流程编码，绑定流程定义 【必传】
        FlowParams flowParams = FlowParams.build().flowCode(flowCode);
        // 设置办理人唯一标识，保存为流程实例的创建人 【必传】
        flowParams.handler(user.getUser().getUserId().toString());
        // 流程变量
        Map<String, Object> variable = new HashMap<>();
        // 流程变量传递业务数据，按实际业务需求传递 【按需传】
        variable.put("businessData", testLeave);
        variable.put("businessType", "testLeave");
        // 条件表达式替换，判断是否满足某个任务的跳转条件  【按需传】
        variable.put("flag", testLeave.getDay());
        // 办理人表达式替换  【按需传】
        variable.put("handler1", Arrays.asList(4, "5", 100L));
        variable.put("handler2", 12L);
        variable.put("handler3", new Object[] {9, "10", 102L});
        variable.put("handler4", "15");
        Task task = FlowEngine.newTask().setId(55L);
        variable.put("handler5", task);
        variable.put("handler6", 77L);

        flowParams.variable(variable);
        // 自定义流程状态扩展
        if (StringUtils.isNotEmpty(flowStatus)) {
            flowParams.flowStatus(flowStatus).hisStatus(flowStatus);
        }

        // 新增请假表
        Instance instance = insService.start(id, flowParams);
        testLeave.setInstanceId(instance.getId());
        testLeave.setNodeCode(instance.getNodeCode());
        testLeave.setNodeName(instance.getNodeName());
        testLeave.setNodeType(instance.getNodeType());
        testLeave.setFlowStatus(instance.getFlowStatus());
        testLeave.setCreateTime(DateUtils.getNowDate());
        // 新增抄送人方法  【按需】
        if (StringUtils.isNotNull(testLeave.getAdditionalHandler())) {
            List<User> users = FlowEngine.userService().structureUser(instance.getId()
                    , testLeave.getAdditionalHandler(), "4");
            FlowEngine.userService().saveBatch(users);
        }
        // 此处可以发送消息通知，比如短信通知，邮件通知等，代码自己实现

        return testLeaveMapper.insertTestLeave(testLeave);
    }

    /**
     * 修改OA 请假申请
     *
     * @param testLeave OA 请假申请
     * @return 结果
     */
    @Override
    public int updateTestLeave(TestLeave testLeave)
    {
        testLeave.setUpdateTime(DateUtils.getNowDate());
        return testLeaveMapper.updateTestLeave(testLeave);
    }

    /**
     * 批量删除OA 请假申请
     *
     * @param ids 需要删除的OA 请假申请主键
     * @return 结果
     */
    @Override
    public int deleteTestLeaveByIds(String[] ids)
    {
        List<TestLeave> testLeaveList = testLeaveMapper.selectTestLeaveByIds(ids);
        if (testLeaveMapper.deleteTestLeaveByIds(ids) > 0) {
            List<Long> instanceIds = testLeaveList.stream().map(TestLeave::getInstanceId).collect(Collectors.toList());
            return insService.remove(instanceIds) ? 1: 0;
        }
        return 0;
    }

    /**
     * 删除OA 请假申请信息
     *
     * @param id OA 请假申请主键
     * @return 结果
     */
    @Override
    public int deleteTestLeaveById(String id)
    {
        return testLeaveMapper.deleteTestLeaveById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int submit(String id, String flowStatus) {
        // 设置流转参数
        TestLeave testLeave = testLeaveMapper.selectTestLeaveById(id);
        LoginUser user = SecurityUtils.getLoginUser();
        // 是通过流程还是退回流程  【必传】
        FlowParams flowParams = FlowParams.build().skipType(SkipType.PASS.getKey());
        // 作为办理人保存到实例和历史表 【按需传，如果办理人权限处理器实现了就不需要传】
        flowParams.handler(user.getUser().getUserId().toString());
        // 设置办理人拥有的权限，办理中需要校验是否有权限办理 【按需传，如果节点没有设置办理人，办理人权限处理器实现了，就不需要传】
        List<String> permissionList = PermissionsUtil.getPermissions();
        flowParams.permissionFlag(permissionList);
        // 自定义流程状态扩展  【按需传】
        if (StringUtils.isNotEmpty(flowStatus)) {
            flowParams.flowStatus(flowStatus).hisStatus(flowStatus);
        }
        // 流程变量
        Map<String, Object> variable = new HashMap<>();
        // 流程变量传递业务数据，按实际业务需求传递  【按需传】
        variable.put("businessType", "testLeave");
        // 办理人表达式替换  【按需传】
        variable.put("flag", testLeave.getDay());
        flowParams.variable(variable);

        // 更新请假表
        Instance instance = taskService.skipByInsId(testLeave.getInstanceId(), flowParams);
        testLeave.setNodeCode(instance.getNodeCode());
        testLeave.setNodeName(instance.getNodeName());
        testLeave.setNodeType(instance.getNodeType());
        testLeave.setFlowStatus(instance.getFlowStatus());
        testLeave.setUpdateTime(DateUtils.getNowDate());
        return testLeaveMapper.updateTestLeave(testLeave);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int handle(TestLeave testLeave, Long taskId, String skipType, String message, String nodeCode, String flowStatus) {
        // 设置流转参数
        LoginUser user = SecurityUtils.getLoginUser();
        // 是通过流程还是退回流程 【必传】
        FlowParams flowParams = FlowParams.build().skipType(skipType);
        // 作为办理人保存到实例和历史表 【按需传，如果办理人权限处理器实现了就不需要传】
        flowParams.handler(user.getUser().getUserId().toString());
        // 如果需要任意跳转流程，传入此参数  【按需传】
        flowParams.nodeCode(nodeCode);
        // 作为审批意见保存到历史记录表  【按需传】
        flowParams.message(message);

        // 设置办理人拥有的权限，办理中需要校验是否有权限办理 【按需传，如果节点没有设置办理人，办理人权限处理器实现了，就不需要传】
        List<String> permissionList = PermissionsUtil.getPermissions();
        flowParams.permissionFlag(permissionList);

        // 流程变量
        Map<String, Object> variable = new HashMap<>();
        // 流程变量传递业务数据，按实际业务需求传递  【按需传】
        variable.put("businessType", "testLeave");
        // 办理人表达式替换  【按需传】
        variable.put("flag", testLeave.getDay());
        flowParams.variable(variable);
        // 自定义流程状态扩展  【按需传】
        if (StringUtils.isNotEmpty(flowStatus)) {
            flowParams.flowStatus(flowStatus).hisStatus(flowStatus);
        }
        // 请假信息存入flowParams,方便查看历史审批数据  【按需传】
        flowParams.hisTaskExt(JSON.toJSONString(testLeave));
        Instance instance = taskService.skip(taskId, flowParams);

        // 更新请假表
        testLeave.setNodeCode(instance.getNodeCode());
        testLeave.setNodeName(instance.getNodeName());
        testLeave.setNodeType(instance.getNodeType());
        testLeave.setFlowStatus(instance.getFlowStatus());
        return testLeaveMapper.updateTestLeave(testLeave);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int rejectLast(TestLeave testLeave, Long taskId, String message, String flowStatus) {
        // 设置流转参数
        LoginUser user = SecurityUtils.getLoginUser();
        FlowParams flowParams = FlowParams.build();
        // 作为办理人保存到实例和历史表 【按需传，如果办理人权限处理器实现了就不需要传】
        flowParams.handler(user.getUser().getUserId().toString());
        // 作为审批意见保存到历史记录表  【按需传】
        flowParams.message(message);

        // 设置办理人拥有的权限，办理中需要校验是否有权限办理 【按需传，如果节点没有设置办理人，办理人权限处理器实现了，就不需要传】
        List<String> permissionList = PermissionsUtil.getPermissions();
        flowParams.permissionFlag(permissionList);

        // 流程变量
        Map<String, Object> variable = new HashMap<>();
        // 流程变量传递业务数据，按实际业务需求传递  【按需传】
        variable.put("businessType", "testLeave");
        flowParams.variable(variable);
        // 自定义流程状态扩展  【按需传】
        if (StringUtils.isNotEmpty(flowStatus)) {
            flowParams.flowStatus(flowStatus).hisStatus(flowStatus);
        }
        // 请假信息存入flowParams,方便查看历史审批数据  【按需传】
        flowParams.hisTaskExt(JSON.toJSONString(testLeave));
        Instance instance = taskService.rejectLast(taskId, flowParams);

        // 更新请假表
        testLeave.setNodeCode(instance.getNodeCode());
        testLeave.setNodeName(instance.getNodeName());
        testLeave.setNodeType(instance.getNodeType());
        testLeave.setFlowStatus(instance.getFlowStatus());
        return testLeaveMapper.updateTestLeave(testLeave);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int taskBack(TestLeave testLeave, Long taskId, String message, String flowStatus) {
        // 设置流转参数
        LoginUser user = SecurityUtils.getLoginUser();
        FlowParams flowParams = FlowParams.build();
        // 作为办理人保存到实例和历史表 【按需传，如果办理人权限处理器实现了就不需要传】
        flowParams.handler(user.getUser().getUserId().toString());
        // 作为审批意见保存到历史记录表  【按需传】
        flowParams.message(message);

        // 设置办理人拥有的权限，办理中需要校验是否有权限办理 【按需传，如果节点没有设置办理人，办理人权限处理器实现了，就不需要传】
        List<String> permissionList = PermissionsUtil.getPermissions();
        flowParams.permissionFlag(permissionList);

        // 流程变量
        Map<String, Object> variable = new HashMap<>();
        // 流程变量传递业务数据，按实际业务需求传递  【按需传】
        variable.put("businessType", "testLeave");
        flowParams.variable(variable);
        // 自定义流程状态扩展  【按需传】
        if (StringUtils.isNotEmpty(flowStatus)) {
            flowParams.flowStatus(flowStatus).hisStatus(flowStatus);
        }
        // 请假信息存入flowParams,方便查看历史审批数据  【按需传】
        flowParams.hisTaskExt(JSON.toJSONString(testLeave));
        Instance instance = taskService.taskBack(taskId, flowParams);

        // 更新请假表
        testLeave.setNodeCode(instance.getNodeCode());
        testLeave.setNodeName(instance.getNodeName());
        testLeave.setNodeType(instance.getNodeType());
        testLeave.setFlowStatus(instance.getFlowStatus());
        return testLeaveMapper.updateTestLeave(testLeave);
    }

    @Override
    public int pending(String id, String flowStatus) {
        // 设置流转参数
        TestLeave testLeave = testLeaveMapper.selectTestLeaveById(id);
        LoginUser user = SecurityUtils.getLoginUser();
        // 作为办理人保存到实例和历史表 【按需传，如果办理人权限处理器实现了就不需要传】
        FlowParams flowParams = FlowParams.build().handler(user.getUser().getUserId().toString());
        // 设置办理人拥有的权限，办理中需要校验是否有权限办理 【按需传，如果节点没有设置办理人，办理人权限处理器实现了，就不需要传】
        List<String> permissionList = PermissionsUtil.getPermissions();
        flowParams.permissionFlag(permissionList);
        // 自定义流程状态扩展  【按需传】
        if (StringUtils.isNotEmpty(flowStatus)) {
            flowParams.flowStatus(flowStatus).hisStatus(flowStatus);
        }
        // 流程变量
        Map<String, Object> variable = new HashMap<>();
        // 流程变量传递业务数据，按实际业务需求传递  【按需传】
        variable.put("businessType", "testLeave");
        flowParams.variable(variable);

        // 更新请假表
        Instance instance = taskService.pendingByInsId(testLeave.getInstanceId(), flowParams);
        testLeave.setNodeCode(instance.getNodeCode());
        testLeave.setNodeName(instance.getNodeName());
        testLeave.setNodeType(instance.getNodeType());
        testLeave.setFlowStatus(instance.getFlowStatus());
        testLeave.setUpdateTime(DateUtils.getNowDate());
        return testLeaveMapper.updateTestLeave(testLeave);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int revoke(String id) {
        TestLeave testLeave = selectTestLeaveById(id);
        // 设置流转参数
        LoginUser user = SecurityUtils.getLoginUser();
        FlowParams flowParams = FlowParams.build();
        // 作为办理人保存到实例和历史表 【按需传，如果办理人权限处理器实现了就不需要传】
        flowParams.handler(user.getUser().getUserId().toString());
        // 作为审批意见保存到历史记录表  【按需传】
        flowParams.message("撤销流程");

        // 流程变量
        Map<String, Object> variable = new HashMap<>();
        // 流程变量传递业务数据，按实际业务需求传递  【按需传】
        variable.put("businessType", "testLeave");
        flowParams.variable(variable);
        // 请假信息存入flowParams,方便查看历史审批数据  【按需传】
        flowParams.hisTaskExt(JSON.toJSONString(testLeave));
        Instance instance = taskService.revoke(testLeave.getInstanceId(), flowParams);

        // 更新请假表
        testLeave.setNodeCode(instance.getNodeCode());
        testLeave.setNodeName(instance.getNodeName());
        testLeave.setNodeType(instance.getNodeType());
        testLeave.setFlowStatus(instance.getFlowStatus());
        return testLeaveMapper.updateTestLeave(testLeave);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int taskBackByInsId(String id) {
        TestLeave testLeave = selectTestLeaveById(id);
        // 设置流转参数
        LoginUser user = SecurityUtils.getLoginUser();
        FlowParams flowParams = FlowParams.build();
        // 作为办理人保存到实例和历史表 【按需传，如果办理人权限处理器实现了就不需要传】
        flowParams.handler(user.getUser().getUserId().toString());
        // 作为审批意见保存到历史记录表  【按需传】
        flowParams.message("撤销流程");

        // 流程变量
        Map<String, Object> variable = new HashMap<>();
        // 流程变量传递业务数据，按实际业务需求传递  【按需传】
        variable.put("businessType", "testLeave");
        flowParams.variable(variable);
        // 请假信息存入flowParams,方便查看历史审批数据  【按需传】
        flowParams.hisTaskExt(JSON.toJSONString(testLeave));
        Instance instance = taskService.taskBackByInsId(testLeave.getInstanceId(), flowParams);

        // 更新请假表
        testLeave.setNodeCode(instance.getNodeCode());
        testLeave.setNodeName(instance.getNodeName());
        testLeave.setNodeType(instance.getNodeType());
        testLeave.setFlowStatus(instance.getFlowStatus());
        return testLeaveMapper.updateTestLeave(testLeave);
    }

    @Override
    public int termination(TestLeave testLeave) {
        // 设置流转参数
        FlowParams flowParams = new FlowParams();
        LoginUser user = SecurityUtils.getLoginUser();
        // 作为审批意见保存到历史记录表  【按需传】
        flowParams.message("终止流程");
        // 作为办理人保存到实例和历史表 【按需传，如果办理人权限处理器实现了就不需要传】
        flowParams.handler(user.getUserId().toString());

        Map<String, Object> variable = new HashMap<>();
        // 流程变量传递业务数据，按实际业务需求传递  【按需传】
        variable.put("businessType", "testLeave");
        flowParams.variable(variable);

        Instance instance = taskService.terminationByInsId(testLeave.getInstanceId(), flowParams);
        if (instance == null) {
            throw new ServiceException("流程实例不存在");
        }

        // 更新请假表
        testLeave.setNodeCode(instance.getNodeCode());
        testLeave.setNodeName(instance.getNodeName());
        testLeave.setNodeType(instance.getNodeType());
        testLeave.setFlowStatus(instance.getFlowStatus());
        return testLeaveMapper.updateTestLeave(testLeave);
    }

    /**
     * 从字典表中获取流程编码
     * @param testLeave 请假信息
     * @return 流程编码
     */
    private String  getFlowType(TestLeave testLeave) {
        List<SysDictData> leaveType = sysDictTypeService.selectDictDataByType("leave_type");
        Map<String, String> map = StreamUtils.toMap(leaveType, SysDictData::getDictValue, SysDictData::getRemark);
        return map.get(testLeave.getType().toString());
    }

}
