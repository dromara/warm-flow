package com.warm.flow.orm.mapper;

import com.warm.flow.core.entity.HisTask;
import com.warm.tools.utils.page.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 历史任务记录Mapper接口
 *
 * @author warm
 * @date 2023-03-29
 */
public interface FlowHisTaskMapper extends WarmMapper<HisTask> {

    /**
     * 根据nodeCode获取未驳回的历史记录
     *
     * @param nodeCode
     * @param instanceId
     * @return
     */
    List<HisTask> getNoReject(@Param("nodeCode") String nodeCode, @Param("instanceId") Long instanceId);

    /**
     * 根据instanceIds删除
     *
     * @param instanceIds 主键
     * @return 结果
     */
    int deleteByInsIds(List<Long> instanceIds);

}