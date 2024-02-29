package com.warm.flow.orm.dao;

import com.warm.flow.core.dao.FlowTaskDao;
import com.warm.flow.core.entity.Task;
import com.warm.flow.orm.invoker.MapperInvoker;
import com.warm.flow.orm.mapper.FlowTaskMapper;
import com.warm.tools.utils.page.Page;

import java.util.List;

/**
 * 待办任务Mapper接口
 *
 * @author warm
 * @date 2023-03-29
 */
public class FlowTaskDaoImpl extends WarmDaoImpl<Task> implements FlowTaskDao {

    @Override
    public FlowTaskMapper getMapper() {
        return MapperInvoker.getMapper(FlowTaskMapper.class);
    }

    @Override
    public List<Task> getByInsId(Long instanceId) {
        return getMapper().getByInsId(instanceId);
    }

    /**
     * 分页查询待办任务数量
     *
     * @param task 条件实体
     * @param page
     * @return
     */
    @Override
    public long countTodo(Task task, Page<Task> page) {
        return getMapper().countTodo(task, page);
    }

    /**
     * 分页查询待办任务
     *
     * @param task 条件实体
     * @param page
     */
    @Override
    public List<Task> toDoPage(Task task, Page<Task> page) {
        return getMapper().toDoPage(task, page);
    }

    /**
     * 根据instanceIds删除
     *
     * @param instanceIds 主键
     * @return 结果
     */
    @Override
    public int deleteByInsIds(List<Long> instanceIds) {
        return getMapper().deleteByInsIds(instanceIds);
    }
}
