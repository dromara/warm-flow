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
    List<FlowHisTask> getByNewInsIds(List<Long> instanceIds);

    List<FlowHisTask> getByInsIds(List<Long> instanceIds);

    /**
     * 根据instanceIds删除
     *
     * @param instanceIds 主键
     * @return 结果
     */
    int deleteByInsIds(List<Long> instanceIds);

    /**
     * 获取已办任务
     * @param flowHisTask
     * @return
     */
    List<FlowHisTask> doneList(FlowHisTask flowHisTask);

}
