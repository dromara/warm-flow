package com.monkey.flow.core.mapper;

import com.monkey.flow.core.domain.entity.FlowHisTask;
import com.monkey.mybatis.core.mapper.FlowBaseMapper;

import java.util.List;

/**
 * 历史任务记录Mapper接口
 *
 * @author hh
 * @date 2023-03-29
 */
public interface FlowHisTaskMapper extends FlowBaseMapper<FlowHisTask> {
    List<FlowHisTask> queryByNewInstanceIds(List<Long> instanceIds);

    List<FlowHisTask> queryByInstanceIds(List<Long> instanceIds);

    /**
     * 根据instanceIds删除
     *
     * @param instanceIds 主键
     * @return 结果
     */
    int deleteByInstanceIds(List<Long> instanceIds);

}
