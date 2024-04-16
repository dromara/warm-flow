package com.warm.flow.core.dao;

import com.warm.flow.core.entity.HisTask;
import com.warm.tools.utils.page.Page;

import java.util.List;

/**
 * 历史任务记录Mapper接口
 *
 * @author warm
 * @date 2023-03-29
 */
public interface FlowHisTaskDao extends WarmDao<HisTask> {

    /**
     * 根据nodeCode获取未驳回的历史记录
     *
     * @param nodeCode
     * @param instanceId
     * @return
     */
    List<HisTask> getNoReject(String nodeCode, Long instanceId);

    /**
     * 根据instanceIds删除
     *
     * @param instanceIds 主键
     * @return 结果
     */
    int deleteByInsIds(List<Long> instanceIds);

    /**
     * 获取已办任务数量
     *
     * @param hisTask
     * @param page
     * @return
     */
    long countDone(HisTask hisTask, Page<HisTask> page);

    /**
     * 获取最新的已办任务
     *
     * @param hisTask
     * @param page
     * @return
     */
    List<HisTask> donePage(HisTask hisTask, Page<HisTask> page);

    /**
     * 获取已办任务
     *
     * @param hisTask
     * @param page
     * @return
     */
    List<HisTask> doneAllPage(HisTask hisTask, Page<HisTask> page);

}
