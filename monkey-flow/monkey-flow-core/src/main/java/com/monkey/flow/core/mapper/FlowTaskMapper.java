package com.monkey.flow.core.mapper;

import com.monkey.flow.core.domain.entity.FlowTask;
import com.monkey.mybatis.core.mapper.FlowMapper;
import com.monkey.mybatis.core.page.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 待办任务Mapper接口
 *
 * @author hh
 * @date 2023-03-29
 */
public interface FlowTaskMapper extends FlowMapper<FlowTask> {

    List<FlowTask> getByInsIds(List<Long> instanceIds);

    /**
     * 分页查询待办任务数量
     * @param flowTask 条件实体
     * @param page
     * @return
     */
    long countTodo(@Param("flowTask") FlowTask flowTask, @Param("page") Page<FlowTask> page);

    /**
     * 分页查询待办任务
     * @param flowTask 条件实体
     * @param page
     * @return
     */
    List<FlowTask> toDoPage(@Param("flowTask") FlowTask flowTask
            , @Param("page") Page<FlowTask> page);

    /**
     * 根据instanceIds删除
     *
     * @param instanceIds 主键
     * @return 结果
     */
    int deleteByInsIds(List<Long> instanceIds);
}
