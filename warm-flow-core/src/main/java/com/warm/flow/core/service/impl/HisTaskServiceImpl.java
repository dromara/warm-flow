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

/**
 * 历史任务记录Service业务层处理
 *
 * @author warm
 * @date 2023-03-29
 */
public class HisTaskServiceImpl extends WarmServiceImpl<FlowHisTaskMapper, FlowHisTask> implements HisTaskService {

    @Override
    public Class<FlowHisTaskMapper> getMapperClass() {
        return FlowHisTaskMapper.class;
    }

    @Override
    public List<FlowHisTask> getByInsIds(Long instanceId) {
        return getMapper().getByInsId(instanceId);
    }

    @Override
    public List<FlowHisTask> getNoReject(String nodeCode, Long instanceId) {
        return getMapper().getNoReject(nodeCode, instanceId);
    }

    @Override
    public boolean deleteByInsIds(List<Long> instanceIds) {
        return SqlHelper.retBool(getMapper().deleteByInsIds(instanceIds));
    }

    @Override
    public Page<FlowHisTask> donePage(FlowHisTask flowHisTask, Page<FlowHisTask> page) {
        // 根据权限标识符过滤
        List<String> permissionFlagD = CollUtil.strToColl(flowHisTask.getPermissionFlag(), ",");
        if (ObjectUtil.isNull(permissionFlagD)) {
            flowHisTask.setPermissionList(flowHisTask.getPermissionList());
        }
        long count = getMapper().countDone(flowHisTask, page);
        if (count > 0) {
            List<FlowHisTask> list = getMapper().donePage(flowHisTask, page);
            return new Page<>(list, count);
        }
        return Page.empty();
    }
}
