package com.warm.flow.core.service.impl;

import com.warm.flow.core.entity.HisTask;
import com.warm.flow.core.dao.FlowHisTaskDao;
import com.warm.flow.core.orm.service.impl.WarmServiceImpl;
import com.warm.flow.core.service.HisTaskService;
import com.warm.flow.core.utils.SqlHelper;
import com.warm.tools.utils.CollUtil;
import com.warm.tools.utils.ObjectUtil;
import com.warm.tools.utils.page.Page;

import java.util.List;

/**
 * 历史任务记录Service业务层处理
 *
 * @author warm
 * @date 2023-03-29
 */
public class HisTaskServiceImpl extends WarmServiceImpl<FlowHisTaskDao, HisTask> implements HisTaskService {

    @Override
    public HisTaskService setDao(FlowHisTaskDao warmDao) {
        this.warmDao = warmDao;
        return this;
    }

    @Override
    public List<HisTask> getByInsIds(Long instanceId) {
        return getDao().getByInsId(instanceId);
    }

    @Override
    public List<HisTask> getNoReject(String nodeCode, Long instanceId) {
        return getDao().getNoReject(nodeCode, instanceId);
    }

    @Override
    public boolean deleteByInsIds(List<Long> instanceIds) {
        return SqlHelper.retBool(getDao().deleteByInsIds(instanceIds));
    }

    @Override
    public Page<HisTask> donePage(HisTask hisTask, Page<HisTask> page) {
        // 根据权限标识符过滤
        List<String> permissionFlagD = CollUtil.strToColl(hisTask.getPermissionFlag(), ",");
        if (ObjectUtil.isNull(permissionFlagD)) {
            hisTask.setPermissionList(hisTask.getPermissionList());
        }
        long count = getDao().countDone(hisTask, page);
        if (count > 0) {
            List<HisTask> list = getDao().donePage(hisTask, page);
            return new Page<>(list, count);
        }
        return Page.empty();
    }

}
