package org.dromara.warm.flow.orm.dao;

import com.mybatisflex.core.query.QueryWrapper;
import org.dromara.warm.flow.core.orm.dao.FlowFormDao;
import org.dromara.warm.flow.core.invoker.FrameInvoker;
import org.dromara.warm.flow.orm.entity.FlowDefinition;
import org.dromara.warm.flow.orm.entity.FlowForm;
import org.dromara.warm.flow.orm.mapper.FlowFormMapper;
import org.dromara.warm.flow.orm.mapper.WarmMapper;
import org.dromara.warm.flow.orm.utils.TenantDeleteUtil;

import java.util.List;

/**
 * @author vanlin
 * @className FlowFormDaoImpl
 * @description
 * @since 2024-9-23 16:17
 */
public class FlowFormDaoImpl extends WarmDaoImpl<FlowForm> implements FlowFormDao<FlowForm> {

    @Override
    public WarmMapper<FlowForm> getMapper() {
        return FrameInvoker.getBean(FlowFormMapper.class);
    }

    @Override
    public List<FlowForm> queryByCodeList(List<String> formCodeList) {
        QueryWrapper queryWrapper = TenantDeleteUtil.getDefaultWrapper(newEntity());
        queryWrapper.in(FlowForm::getFormCode, formCodeList);
        return getMapper().selectListByQueryAs(queryWrapper, FlowForm.class);
    }


    @Override
    public FlowForm newEntity() {
        return new FlowForm();
    }
}
