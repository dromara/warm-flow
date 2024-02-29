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
     * 根据实例id获取历史记录集合
     *
     * @param instanceId
     * @return
     */
    List<HisTask> getByInsId(Long instanceId);

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

    /**
     * 获取已办任务数量
     *
     * @param hisTask
     * @param page
     * @return
     */
    long countDone(@Param("hisTask") HisTask hisTask
            , @Param("page") Page<HisTask> page);

    /**
     * 获取最新的已办任务
     *
     * @param hisTask
     * @param page
     * @return
     */
    List<HisTask> donePage(@Param("hisTask") HisTask hisTask
            , @Param("page") Page<HisTask> page);

    /**
     * 获取已办任务
     *
     * @param hisTask
     * @param page
     * @return
     */
    List<HisTask> doneAllPage(@Param("hisTask") HisTask hisTask
            , @Param("page") Page<HisTask> page);

}
