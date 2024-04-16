package com.warm.flow.orm.dao;

import com.warm.flow.core.dao.FlowHisTaskDao;
import com.warm.flow.core.entity.HisTask;
import com.warm.flow.core.invoker.FrameInvoker;
import com.warm.flow.orm.mapper.FlowHisTaskMapper;
import com.warm.tools.utils.page.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 历史任务记录Mapper接口
 *
 * @author warm
 * @date 2023-03-29
 */
public class FlowHisTaskDaoImpl extends WarmDaoImpl<HisTask> implements FlowHisTaskDao {

    @Override
    public FlowHisTaskMapper getMapper() {
        return FrameInvoker.getBean(FlowHisTaskMapper.class);
    }

    /**
     * 根据nodeCode获取未驳回的历史记录
     *
     * @param nodeCode
     * @param instanceId
     * @return
     */
    @Override
    public List<HisTask> getNoReject(String nodeCode, Long instanceId) {
        return getMapper().getNoReject(nodeCode, instanceId);
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

    /**
     * 获取已办任务数量
     *
     * @param hisTask
     * @param page
     * @return
     */
    @Override
    public long countDone(HisTask hisTask, Page<HisTask> page) {
        return getMapper().countDone(hisTask, page);
    }

    /**
     * 获取最新的已办任务
     *
     * @param hisTask
     * @param page
     * @return
     */
    @Override
    public List<HisTask> donePage(@Param("flowHisTask") HisTask hisTask
            , @Param("page") Page<HisTask> page) {
        return getMapper().donePage(hisTask, page);
    }

    /**
     * 获取已办任务
     *
     * @param hisTask
     * @param page
     * @return
     */
    @Override
    public List<HisTask> doneAllPage(@Param("flowHisTask") HisTask hisTask
            , @Param("page") Page<HisTask> page) {
        return getMapper().doneAllPage(hisTask, page);
    }

}
