package com.warm.flow.orm.mapper;

import com.warm.flow.core.entity.Task;
import com.warm.tools.utils.page.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 待办任务Mapper接口
 *
 * @author warm
 * @date 2023-03-29
 */
public interface FlowTaskMapper extends WarmMapper<Task> {

    List<Task> getByInsId(Long instanceId);

    /**
     * 分页查询待办任务数量
     *
     * @param task 条件实体
     * @param page
     * @return
     */
    long countTodo(@Param("task") Task task, @Param("page") Page<Task> page);

    /**
     * 分页查询待办任务
     *
     * @param task 条件实体
     * @param page
     */
    List<Task> toDoPage(@Param("task") Task task
            , @Param("page") Page<Task> page);

    /**
     * 根据instanceIds删除
     *
     * @param instanceIds 主键
     * @return 结果
     */
    int deleteByInsIds(List<Long> instanceIds);
}
