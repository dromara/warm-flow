package com.ruoyi.system.service.impl;

import com.alibaba.fastjson2.JSON;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.flow.mapper.WarmFlowMapper;
import com.ruoyi.flow.utils.PermissionsUtil;
import com.ruoyi.system.domain.ContractProcess;
import com.ruoyi.system.mapper.ContractProcessMapper;
import com.ruoyi.system.service.IContractProcessService;
import org.dromara.warm.flow.core.dto.FlowParams;
import org.dromara.warm.flow.core.entity.Instance;
import org.dromara.warm.flow.core.service.InsService;
import org.dromara.warm.flow.core.service.TaskService;
import org.dromara.warm.flow.core.utils.IdUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 合同流程Service业务层处理
 *
 * @author ruoyi
 * @date 2025-04-30
 */
@Service
public class ContractProcessServiceImpl implements IContractProcessService
{
    @Autowired
    private ContractProcessMapper contractProcessMapper;

    @Resource
    private InsService insService;

    @Resource
    private TaskService taskService;

    @Resource
    private WarmFlowMapper flowMapper;

    /**
     * 查询合同流程
     *
     * @param id 合同流程主键
     * @return 合同流程
     */
    @Override
    public ContractProcess selectContractProcessById(Long id)
    {
        return contractProcessMapper.selectContractProcessById(id);
    }

    /**
     * 查询合同流程列表
     *
     * @param contractProcess 合同流程
     * @return 合同流程
     */
    @Override
    public List<ContractProcess> selectContractProcessList(ContractProcess contractProcess)
    {
        return contractProcessMapper.selectContractProcessList(contractProcess);
    }

    /**
     * 新增合同流程
     *
     * @param contractProcess 合同流程
     * @return 结果
     */
    @Override
    public int insertContractProcess(ContractProcess contractProcess, String flowStatus)
    {
        // 设置流转参数
        Long id = IdUtils.nextId();
        contractProcess.setId(id);
        LoginUser user = SecurityUtils.getLoginUser();
        // 传递流程编码，绑定流程定义 【必传】
        FlowParams flowParams = FlowParams.build().flowCode("contract_process");
        // 设置办理人唯一标识，保存为流程实例的创建人 【必传】
        flowParams.handler(user.getUser().getUserId().toString());
        // 流程变量
        Map<String, Object> variable = new HashMap<>();
        // 找到发起人部门领导，设置到流程变量，合同签订节点办理人表达式，会自动替换为该部门领导
        List<SysUser> sysUsers = flowMapper.queryLeaderByDeptId(user.getDeptId());
        if (StringUtils.isNotEmpty(sysUsers)) {
            variable.put("leader", sysUsers.get(0).getUserId());
            flowParams.variable(variable);
        }

        Instance instance = insService.start(String.valueOf(id), flowParams);

        // 新增合同签订表
        contractProcess.setInstanceId(instance.getId());
        contractProcess.setNodeCode(instance.getNodeCode());
        contractProcess.setNodeName(instance.getNodeName());
        contractProcess.setNodeType(instance.getNodeType());
        contractProcess.setFlowStatus(instance.getFlowStatus());
        contractProcess.setCreateTime(DateUtils.getNowDate());

        return contractProcessMapper.insertContractProcess(contractProcess);
    }

    /**
     * 修改合同流程
     *
     * @param contractProcess 合同流程
     * @return 结果
     */
    @Override
    public int updateContractProcess(ContractProcess contractProcess)
    {
        contractProcess.setUpdateTime(DateUtils.getNowDate());
        return contractProcessMapper.updateContractProcess(contractProcess);
    }

    /**
     * 批量删除合同流程
     *
     * @param ids 需要删除的合同流程主键
     * @return 结果
     */
    @Override
    public int deleteContractProcessByIds(Long[] ids)
    {
        List<ContractProcess> contractProcessList = contractProcessMapper.selectContractProcessByIds(ids);
        if (contractProcessMapper.deleteContractProcessByIds(ids) > 0) {
            List<Long> instanceIds = contractProcessList.stream().map(ContractProcess::getInstanceId).collect(Collectors.toList());
            return insService.remove(instanceIds) ? 1: 0;
        }
        return 0;
    }

    /**
     * 删除合同流程信息
     *
     * @param id 合同流程主键
     * @return 结果
     */
    @Override
    public int deleteContractProcessById(Long id)
    {
        return contractProcessMapper.deleteContractProcessById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int handle(ContractProcess contractProcess, Long taskId, String skipType, String message, String nodeCode, String flowStatus) {
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

        // 合同签订信息存入flowParams,方便查看历史审批数据  【按需传】
        flowParams.hisTaskExt(JSON.toJSONString(contractProcess));
        Instance instance = taskService.skip(taskId, flowParams);

        // 更新合同签订表
        contractProcess.setNodeCode(instance.getNodeCode());
        contractProcess.setNodeName(instance.getNodeName());
        contractProcess.setNodeType(instance.getNodeType());
        contractProcess.setFlowStatus(instance.getFlowStatus());
        return contractProcessMapper.updateContractProcess(contractProcess);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int rejectLast(ContractProcess contractProcess, Long taskId, String message, String flowStatus) {
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

        // 合同签订信息存入flowParams,方便查看历史审批数据  【按需传】
        flowParams.hisTaskExt(JSON.toJSONString(contractProcess));
        Instance instance = taskService.rejectLast(taskId, flowParams);

        // 更新合同签订表
        contractProcess.setNodeCode(instance.getNodeCode());
        contractProcess.setNodeName(instance.getNodeName());
        contractProcess.setNodeType(instance.getNodeType());
        contractProcess.setFlowStatus(instance.getFlowStatus());
        return contractProcessMapper.updateContractProcess(contractProcess);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int taskBack(ContractProcess contractProcess, Long taskId, String message, String flowStatus) {
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

        // 合同签订信息存入flowParams,方便查看历史审批数据  【按需传】
        flowParams.hisTaskExt(JSON.toJSONString(contractProcess));
        Instance instance = taskService.taskBack(taskId, flowParams);

        // 更新合同签订表
        contractProcess.setNodeCode(instance.getNodeCode());
        contractProcess.setNodeName(instance.getNodeName());
        contractProcess.setNodeType(instance.getNodeType());
        contractProcess.setFlowStatus(instance.getFlowStatus());
        return contractProcessMapper.updateContractProcess(contractProcess);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int revoke(String id) {
        ContractProcess contractProcess = selectContractProcessById(Long.valueOf(id));
        // 设置流转参数
        LoginUser user = SecurityUtils.getLoginUser();
        FlowParams flowParams = FlowParams.build();
        // 作为办理人保存到实例和历史表 【按需传，如果办理人权限处理器实现了就不需要传】
        flowParams.handler(user.getUser().getUserId().toString());
        // 作为审批意见保存到历史记录表  【按需传】
        flowParams.message("撤销流程");

        // 合同签订信息存入flowParams,方便查看历史审批数据  【按需传】
        flowParams.hisTaskExt(JSON.toJSONString(contractProcess));
        Instance instance = taskService.revoke(contractProcess.getInstanceId(), flowParams);

        // 更新合同签订表
        contractProcess.setNodeCode(instance.getNodeCode());
        contractProcess.setNodeName(instance.getNodeName());
        contractProcess.setNodeType(instance.getNodeType());
        contractProcess.setFlowStatus(instance.getFlowStatus());
        return contractProcessMapper.updateContractProcess(contractProcess);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int taskBackByInsId(String id) {
        ContractProcess contractProcess = selectContractProcessById(Long.valueOf(id));
        // 设置流转参数
        LoginUser user = SecurityUtils.getLoginUser();
        FlowParams flowParams = FlowParams.build();
        // 作为办理人保存到实例和历史表 【按需传，如果办理人权限处理器实现了就不需要传】
        flowParams.handler(user.getUser().getUserId().toString());
        // 作为审批意见保存到历史记录表  【按需传】
        flowParams.message("撤销流程");

        // 合同签订信息存入flowParams,方便查看历史审批数据  【按需传】
        flowParams.hisTaskExt(JSON.toJSONString(contractProcess));
        Instance instance = taskService.taskBackByInsId(contractProcess.getInstanceId(), flowParams);

        // 更新合同签订表
        contractProcess.setNodeCode(instance.getNodeCode());
        contractProcess.setNodeName(instance.getNodeName());
        contractProcess.setNodeType(instance.getNodeType());
        contractProcess.setFlowStatus(instance.getFlowStatus());
        return contractProcessMapper.updateContractProcess(contractProcess);
    }

    @Override
    public int termination(ContractProcess contractProcess) {
        // 设置流转参数
        FlowParams flowParams = new FlowParams();
        LoginUser user = SecurityUtils.getLoginUser();
        // 作为审批意见保存到历史记录表  【按需传】
        flowParams.message("终止流程");
        // 作为办理人保存到实例和历史表 【按需传，如果办理人权限处理器实现了就不需要传】
        flowParams.handler(user.getUserId().toString());

        Instance instance = taskService.terminationByInsId(contractProcess.getInstanceId(), flowParams);
        if (instance == null) {
            throw new ServiceException("流程实例不存在");
        }

        // 更新合同签订表
        contractProcess.setNodeCode(instance.getNodeCode());
        contractProcess.setNodeName(instance.getNodeName());
        contractProcess.setNodeType(instance.getNodeType());
        contractProcess.setFlowStatus(instance.getFlowStatus());
        return contractProcessMapper.updateContractProcess(contractProcess);
    }
}
