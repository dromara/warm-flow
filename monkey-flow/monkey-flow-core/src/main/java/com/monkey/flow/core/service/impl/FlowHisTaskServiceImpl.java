package com.monkey.flow.core.service.impl;

import com.monkey.flow.core.domain.entity.FlowHisTask;
import com.monkey.flow.core.mapper.FlowHisTaskMapper;
import com.monkey.flow.core.service.IFlowHisTaskService;
import com.monkey.mybatis.core.page.Page;
import com.monkey.mybatis.core.service.impl.FlowServiceImpl;
import com.monkey.mybatis.core.utils.SqlHelper;
import com.monkey.tools.utils.CollUtil;
import com.monkey.tools.utils.ObjectUtil;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 历史任务记录Service业务层处理
 *
 * @author hh
 * @date 2023-03-29
 */
public class FlowHisTaskServiceImpl extends FlowServiceImpl<FlowHisTask> implements IFlowHisTaskService {
    @Resource
    private FlowHisTaskMapper hisTaskMapper;

    @Override
    public FlowHisTaskMapper getBaseMapper() {
        return hisTaskMapper;
    }

    @Override
    public List<FlowHisTask> getByInsIds(Long instanceId) {
        return hisTaskMapper.getByInsId(instanceId);
    }

    @Override
    public boolean deleteByInsIds(List<Long> instanceIds) {
        return SqlHelper.retBool(hisTaskMapper.deleteByInsIds(instanceIds));
    }

    @Override
    public Page<FlowHisTask> donePage(FlowHisTask flowHisTask, Page<FlowHisTask> page) {
        long count = hisTaskMapper.countDone(flowHisTask, page);
        if (count > 0) {
            List<FlowHisTask> list = hisTaskMapper.donePage(flowHisTask, page);
            // 根据权限标识符过滤
            List<String> permissionFlagD = CollUtil.strToColl(flowHisTask.getPermissionFlag(), ",");
            if (ObjectUtil.isNull(permissionFlagD)) {
                permissionFlagD = flowHisTask.getPermissionList();
            }
            if (ObjectUtil.isNotNull(permissionFlagD)) {
                List<String> finalPermissionFlagD = permissionFlagD;
                list = list.stream().filter(hisTask -> {
                    List<String> permissionFlagO = CollUtil.strToColl(hisTask.getPermissionFlag(), ",");
                    if (ObjectUtil.isNull(permissionFlagO)) {
                        return false;
                    }
                    return CollUtil.containsAny(permissionFlagO, finalPermissionFlagD);
                }).collect(Collectors.toList());
            }
            return new Page<>(list, count);
        }
        return Page.empty();
    }
}
