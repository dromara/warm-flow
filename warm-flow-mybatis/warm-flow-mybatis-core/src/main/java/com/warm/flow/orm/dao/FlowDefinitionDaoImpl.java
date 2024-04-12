package com.warm.flow.orm.dao;

import com.warm.flow.core.dao.FlowDefinitionDao;
import com.warm.flow.core.entity.Definition;
import com.warm.flow.core.invoker.FrameInvoker;
import com.warm.flow.orm.mapper.FlowDefinitionMapper;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * 流程定义Mapper接口
 *
 * @author warm
 * @date 2023-03-29
 */
public class FlowDefinitionDaoImpl extends WarmDaoImpl<Definition> implements FlowDefinitionDao {

    @Override
    public FlowDefinitionMapper getMapper() {
        return FrameInvoker.getBean(FlowDefinitionMapper.class);
    }

    @Override
    public List<Definition> queryByCodeList(List<String> flowCodeList) {
        return getMapper().queryByCodeList(flowCodeList);
    }

    public void closeFlowByCodeList(List<String> flowCodeList) {
        getMapper().closeFlowByCodeList(flowCodeList);
    }

    /**
     * 批量删除流程节点
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteNodeByDefIds(Collection<? extends Serializable> ids) {
        return getMapper().deleteNodeByDefIds(ids);
    }

    /**
     * 通过流程定义主键删除流程节点信息
     *
     * @param id 流程定义ID
     * @return 结果
     */
    public int deleteNodeByDefId(Serializable id) {
        return getMapper().deleteNodeByDefId(id);
    }

    /**
     * 批量删除节点跳转关联
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSkipByDefIds(Collection<? extends Serializable> ids) {
        return getMapper().deleteSkipByDefIds(ids);
    }


    /**
     * 通过流程定义主键删除节点跳转关联信息
     *
     * @param id 流程定义ID
     * @return 结果
     */
    public int deleteSkipByDefId(Serializable id) {
        return getMapper().deleteSkipByDefId(id);
    }

}
