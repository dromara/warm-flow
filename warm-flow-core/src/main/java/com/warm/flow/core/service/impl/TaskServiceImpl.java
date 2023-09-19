package com.warm.flow.core.service.impl;

import com.warm.flow.core.constant.ExceptionCons;
import com.warm.flow.core.domain.entity.FlowTask;
import com.warm.flow.core.mapper.FlowTaskMapper;
import com.warm.flow.core.service.TaskService;
import com.warm.flow.core.utils.AssertUtil;
import com.warm.mybatis.core.page.Page;
import com.warm.mybatis.core.service.impl.WarmServiceImpl;
import com.warm.mybatis.core.utils.SqlHelper;
import com.warm.tools.utils.CollUtil;
import com.warm.tools.utils.ObjectUtil;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 待办任务Service业务层处理
 *
 * @author warm
 * @date 2023-03-29
 */
public class TaskServiceImpl extends WarmServiceImpl<FlowTaskMapper, FlowTask> implements TaskService {

    @Override
    public List<FlowTask> getByInsId(Long instanceId) {
        AssertUtil.isTrue(ObjectUtil.isNull(instanceId), ExceptionCons.NOT_FOUNT_INSTANCE_ID);
        return getMapper().getByInsId(instanceId);
    }

    @Override
    public Page<FlowTask> toDoPage(FlowTask flowTask, Page<FlowTask> page) {
        long count = getMapper().countTodo(flowTask, page);
        if (count > 0) {
            List<FlowTask> list = getMapper().toDoPage(flowTask, page);
            // 根据权限标识符过滤
            List<String> permissionFlagD = CollUtil.strToColl(flowTask.getPermissionFlag(), ",");
            if (ObjectUtil.isNull(permissionFlagD)) {
                permissionFlagD = flowTask.getPermissionList();
            }
            if (CollUtil.isNotEmpty(permissionFlagD)) {
                List<String> finalPermissionFlagD = permissionFlagD;
                list = list.stream().filter(task -> {
                    List<String> permissionFlagO = CollUtil.strToColl(task.getPermissionFlag(), ",");
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

    @Override
    public boolean deleteByInsIds(List<Long> instanceIds) {
        return SqlHelper.retBool(getMapper().deleteByInsIds(instanceIds));
    }
}
