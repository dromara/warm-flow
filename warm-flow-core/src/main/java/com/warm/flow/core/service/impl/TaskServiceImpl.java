package com.warm.flow.core.service.impl;

import com.warm.flow.core.constant.ExceptionCons;
import com.warm.flow.core.dao.FlowTaskDao;
import com.warm.flow.core.entity.Task;
import com.warm.flow.core.orm.service.impl.WarmServiceImpl;
import com.warm.flow.core.service.TaskService;
import com.warm.flow.core.utils.AssertUtil;
import com.warm.flow.core.utils.SqlHelper;
import com.warm.tools.utils.CollUtil;
import com.warm.tools.utils.ObjectUtil;
import com.warm.tools.utils.page.Page;

import java.util.List;

/**
 * 待办任务Service业务层处理
 *
 * @author warm
 * @date 2023-03-29
 */
public class TaskServiceImpl extends WarmServiceImpl<FlowTaskDao, Task> implements TaskService {

    @Override
    public TaskService setDao(FlowTaskDao warmDao) {
        this.warmDao = warmDao;
        return this;
    }

    @Override
    public List<Task> getByInsId(Long instanceId) {
        AssertUtil.isTrue(ObjectUtil.isNull(instanceId), ExceptionCons.NOT_FOUNT_INSTANCE_ID);
        return getDao().getByInsId(instanceId);
    }

    @Override
    public Page<Task> toDoPage(Task task, Page<Task> page) {
        // 根据权限标识符过滤
        List<String> permissionFlagD = CollUtil.strToColl(task.getPermissionFlag(), ",");
        if (ObjectUtil.isNull(permissionFlagD)) {
            task.setPermissionList(task.getPermissionList());
        }
        long count = getDao().countTodo(task, page);
        if (count > 0) {
            List<Task> list = getDao().toDoPage(task, page);
            return new Page<>(list, count);
        }
        return Page.empty();
    }

    @Override
    public boolean deleteByInsIds(List<Long> instanceIds) {
        return SqlHelper.retBool(getDao().deleteByInsIds(instanceIds));
    }
}
