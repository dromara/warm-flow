package com.ruoyi.system.service.impl;

import com.alibaba.fastjson2.JSON;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.flow.mapper.WarmFlowMapper;
import com.ruoyi.flow.utils.PermissionsUtil;
import com.ruoyi.system.domain.ProcurementSteps;
import com.ruoyi.system.mapper.ProcurementStepsMapper;
import com.ruoyi.system.service.IProcurementStepsService;
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
 * 企业采购流程表Service业务层处理
 *
 * @author ruoyi
 * @date 2025-05-08
 */
@Service
public class ProcurementStepsServiceImpl implements IProcurementStepsService
{
    @Autowired
    private ProcurementStepsMapper procurementStepsMapper;

    @Resource
    private InsService insService;

    @Resource
    private TaskService taskService;

    @Resource
    private WarmFlowMapper flowMapper;

    /**
     * 查询企业采购流程表
     *
     * @param id 企业采购流程表主键
     * @return 企业采购流程表
     */
    @Override
    public ProcurementSteps selectProcurementStepsById(Long id)
    {
        return procurementStepsMapper.selectProcurementStepsById(id);
    }

    /**
     * 查询企业采购流程表列表
     *
     * @param procurementSteps 企业采购流程表
     * @return 企业采购流程表
     */
    @Override
    public List<ProcurementSteps> selectProcurementStepsList(ProcurementSteps procurementSteps)
    {
        return procurementStepsMapper.selectProcurementStepsList(procurementSteps);
    }

    /**
     * 新增企业采购流程表
     *
     * @param procurementSteps 企业采购流程表
     * @return 结果
     */
    @Override
    public int insertProcurementSteps(ProcurementSteps procurementSteps)
    {
        // 设置流转参数
        Long id = IdUtils.nextId();
        procurementSteps.setId(id);
        LoginUser user = SecurityUtils.getLoginUser();
        // 传递流程编码，绑定流程定义 【必传】
        FlowParams flowParams = FlowParams.build().flowCode("procurement_steps");
        // 设置办理人唯一标识，保存为流程实例的创建人 【必传】
        flowParams.handler(user.getUser().getUserId().toString());
        Instance instance = insService.start(String.valueOf(id), flowParams);

        // 新增企业采购表
        procurementSteps.setInstanceId(instance.getId());
        procurementSteps.setNodeCode(instance.getNodeCode());
        procurementSteps.setNodeName(instance.getNodeName());
        procurementSteps.setNodeType(instance.getNodeType());
        procurementSteps.setFlowStatus(instance.getFlowStatus());
        procurementSteps.setCreateTime(DateUtils.getNowDate());

        return procurementStepsMapper.insertProcurementSteps(procurementSteps);
    }

    /**
     * 修改企业采购流程表
     *
     * @param procurementSteps 企业采购流程表
     * @return 结果
     */
    @Override
    public int updateProcurementSteps(ProcurementSteps procurementSteps)
    {
        procurementSteps.setUpdateTime(DateUtils.getNowDate());
        return procurementStepsMapper.updateProcurementSteps(procurementSteps);
    }

    /**
     * 批量删除企业采购流程表
     *
     * @param ids 需要删除的企业采购流程表主键
     * @return 结果
     */
    @Override
    public int deleteProcurementStepsByIds(Long[] ids)
    {
        List<ProcurementSteps> procurementStepsList = procurementStepsMapper.selectProcurementStepsByIds(ids);
        if (procurementStepsMapper.deleteProcurementStepsByIds(ids) > 0) {
            List<Long> instanceIds = procurementStepsList.stream().map(ProcurementSteps::getInstanceId).collect(Collectors.toList());
            return insService.remove(instanceIds) ? 1: 0;
        }
        return 0;
    }

    /**
     * 删除企业采购流程表信息
     *
     * @param id 企业采购流程表主键
     * @return 结果
     */
    @Override
    public int deleteProcurementStepsById(Long id)
    {
        return procurementStepsMapper.deleteProcurementStepsById(id);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public int handle(ProcurementSteps procurementSteps, Long taskId, String skipType, String message, String nodeCode, String flowStatus) {
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
        variable.put("urgent", procurementSteps.getUrgent());
        flowParams.variable(variable);

        // 企业采购信息存入flowParams,方便查看历史审批数据  【按需传】
        flowParams.hisTaskExt(JSON.toJSONString(procurementSteps));
        Instance instance = taskService.skip(taskId, flowParams);

        // 更新企业采购表
        procurementSteps.setNodeCode(instance.getNodeCode());
        procurementSteps.setNodeName(instance.getNodeName());
        procurementSteps.setNodeType(instance.getNodeType());
        procurementSteps.setFlowStatus(instance.getFlowStatus());
        return procurementStepsMapper.updateProcurementSteps(procurementSteps);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int rejectLast(ProcurementSteps procurementSteps, Long taskId, String message, String flowStatus) {
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

        // 企业采购信息存入flowParams,方便查看历史审批数据  【按需传】
        flowParams.hisTaskExt(JSON.toJSONString(procurementSteps));
        Instance instance = taskService.rejectLast(taskId, flowParams);

        // 更新企业采购表
        procurementSteps.setNodeCode(instance.getNodeCode());
        procurementSteps.setNodeName(instance.getNodeName());
        procurementSteps.setNodeType(instance.getNodeType());
        procurementSteps.setFlowStatus(instance.getFlowStatus());
        return procurementStepsMapper.updateProcurementSteps(procurementSteps);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int taskBack(ProcurementSteps procurementSteps, Long taskId, String message, String flowStatus) {
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

        // 企业采购信息存入flowParams,方便查看历史审批数据  【按需传】
        flowParams.hisTaskExt(JSON.toJSONString(procurementSteps));
        Instance instance = taskService.taskBack(taskId, flowParams);

        // 更新企业采购表
        procurementSteps.setNodeCode(instance.getNodeCode());
        procurementSteps.setNodeName(instance.getNodeName());
        procurementSteps.setNodeType(instance.getNodeType());
        procurementSteps.setFlowStatus(instance.getFlowStatus());
        return procurementStepsMapper.updateProcurementSteps(procurementSteps);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int revoke(String id) {
        ProcurementSteps procurementSteps = selectProcurementStepsById(Long.valueOf(id));
        // 设置流转参数
        LoginUser user = SecurityUtils.getLoginUser();
        FlowParams flowParams = FlowParams.build();
        // 作为办理人保存到实例和历史表 【按需传，如果办理人权限处理器实现了就不需要传】
        flowParams.handler(user.getUser().getUserId().toString());
        // 作为审批意见保存到历史记录表  【按需传】
        flowParams.message("撤销流程");

        // 企业采购信息存入flowParams,方便查看历史审批数据  【按需传】
        flowParams.hisTaskExt(JSON.toJSONString(procurementSteps));
        Instance instance = taskService.revoke(procurementSteps.getInstanceId(), flowParams);

        // 更新企业采购表
        procurementSteps.setNodeCode(instance.getNodeCode());
        procurementSteps.setNodeName(instance.getNodeName());
        procurementSteps.setNodeType(instance.getNodeType());
        procurementSteps.setFlowStatus(instance.getFlowStatus());
        return procurementStepsMapper.updateProcurementSteps(procurementSteps);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int taskBackByInsId(String id) {
        ProcurementSteps procurementSteps = selectProcurementStepsById(Long.valueOf(id));
        // 设置流转参数
        LoginUser user = SecurityUtils.getLoginUser();
        FlowParams flowParams = FlowParams.build();
        // 作为办理人保存到实例和历史表 【按需传，如果办理人权限处理器实现了就不需要传】
        flowParams.handler(user.getUser().getUserId().toString());
        // 作为审批意见保存到历史记录表  【按需传】
        flowParams.message("撤销流程");

        // 企业采购信息存入flowParams,方便查看历史审批数据  【按需传】
        flowParams.hisTaskExt(JSON.toJSONString(procurementSteps));
        Instance instance = taskService.taskBackByInsId(procurementSteps.getInstanceId(), flowParams);

        // 更新企业采购表
        procurementSteps.setNodeCode(instance.getNodeCode());
        procurementSteps.setNodeName(instance.getNodeName());
        procurementSteps.setNodeType(instance.getNodeType());
        procurementSteps.setFlowStatus(instance.getFlowStatus());
        return procurementStepsMapper.updateProcurementSteps(procurementSteps);
    }

    @Override
    public int termination(ProcurementSteps procurementSteps) {
        // 设置流转参数
        FlowParams flowParams = new FlowParams();
        LoginUser user = SecurityUtils.getLoginUser();
        // 作为审批意见保存到历史记录表  【按需传】
        flowParams.message("终止流程");
        // 作为办理人保存到实例和历史表 【按需传，如果办理人权限处理器实现了就不需要传】
        flowParams.handler(user.getUserId().toString());

        Instance instance = taskService.terminationByInsId(procurementSteps.getInstanceId(), flowParams);
        if (instance == null) {
            throw new ServiceException("流程实例不存在");
        }

        // 更新企业采购表
        procurementSteps.setNodeCode(instance.getNodeCode());
        procurementSteps.setNodeName(instance.getNodeName());
        procurementSteps.setNodeType(instance.getNodeType());
        procurementSteps.setFlowStatus(instance.getFlowStatus());
        return procurementStepsMapper.updateProcurementSteps(procurementSteps);
    }
}
