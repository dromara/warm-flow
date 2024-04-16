package com.warm.flow.core.service;

import com.warm.flow.core.entity.Task;
import com.warm.flow.core.orm.service.IWarmService;
import com.warm.tools.utils.page.Page;

import java.util.List;

/**
 * 待办任务Service接口
 *
 * @author warm
 * @date 2023-03-29
 */
public interface TaskService extends IWarmService<Task> {

    /**
     * 分页查询待办任务
     *
     * @param task 条件实体
     * @param page
     * @return
     */
    @Deprecated
    Page<Task> toDoPage(Task task, Page<Task> page);

    /**
     * 根据instanceIds删除
     *
     * @param instanceIds
     * @return
     */
    boolean deleteByInsIds(List<Long> instanceIds);
}
