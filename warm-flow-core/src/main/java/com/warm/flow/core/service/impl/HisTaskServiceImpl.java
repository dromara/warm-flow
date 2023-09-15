package com.warm.flow.core.service.impl;

import com.warm.flow.core.domain.entity.FlowHisTask;
import com.warm.flow.core.mapper.FlowHisTaskMapper;
import com.warm.flow.core.service.HisTaskService;
import com.warm.mybatis.core.page.Page;
import com.warm.mybatis.core.service.impl.WarmServiceImpl;
import com.warm.mybatis.core.utils.SqlHelper;
import com.warm.tools.utils.CollUtil;
import com.warm.tools.utils.ObjectUtil;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 历史任务记录Service业务层处理
 *
 * @author warm
 * @date 2023-03-29
 */
public class HisTaskServiceImpl extends WarmServiceImpl<FlowHisTaskMapper, FlowHisTask> implements HisTaskService {

    @Override
    public List<FlowHisTask> getByInsIds(Long instanceId) {
        return getMapper().getByInsId(instanceId);
    }

    @Override
    public boolean deleteByInsIds(List<Long> instanceIds) {
        return SqlHelper.retBool(getMapper().deleteByInsIds(instanceIds));
    }

    @Override
    public Page<FlowHisTask> donePage(FlowHisTask flowHisTask, Page<FlowHisTask> page) {
        long count = getMapper().countDone(flowHisTask, page);

        if (count > 0) {
            List<FlowHisTask> list = getMapper().donePage(flowHisTask, page);
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
