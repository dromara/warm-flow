package com.warm.flow.core.service.impl;

import com.warm.flow.core.domain.entity.FlowHisTask;
import com.warm.flow.core.mapper.FlowHisTaskMapper;
import com.warm.flow.core.service.IFlowHisTaskService;
import com.warm.mybatis.core.invoker.MapperInvoker;
import com.warm.mybatis.core.page.Page;
import com.warm.mybatis.core.service.impl.FlowServiceImpl;
import com.warm.mybatis.core.utils.SqlHelper;
import com.warm.tools.utils.CollUtil;
import com.warm.tools.utils.ObjectUtil;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 历史任务记录Service业务层处理
 *
 * @author hh
 * @date 2023-03-29
 */
public class FlowHisTaskServiceImpl extends FlowServiceImpl<FlowHisTaskMapper, FlowHisTask> implements IFlowHisTaskService {

    @Override
    public List<FlowHisTask> getByInsIds(Long instanceId) {
        return MapperInvoker.have(baseMapper -> baseMapper.getByInsId(instanceId), mapperClass());
    }

    @Override
    public boolean deleteByInsIds(List<Long> instanceIds) {
        Integer result = MapperInvoker.have(baseMapper -> baseMapper.deleteByInsIds(instanceIds), mapperClass());
        return SqlHelper.retBool(result);
    }

    @Override
    public Page<FlowHisTask> donePage(FlowHisTask flowHisTask, Page<FlowHisTask> page) {
        long count = MapperInvoker.have(baseMapper -> baseMapper.countDone(flowHisTask, page), mapperClass());

        if (count > 0) {
            List<FlowHisTask> list = MapperInvoker.have(baseMapper -> baseMapper.donePage(flowHisTask, page), mapperClass());
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
