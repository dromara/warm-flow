package com.warm.flow.orm.mapper;

import com.warm.flow.core.entity.Task;
import com.warm.flow.orm.entity.FlowTask;
import com.warm.tools.utils.page.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 待办任务Mapper接口
 *
 * @author warm
 * @date 2023-03-29
 */
public interface FlowTaskMapper extends WarmMapper<FlowTask> {

}
