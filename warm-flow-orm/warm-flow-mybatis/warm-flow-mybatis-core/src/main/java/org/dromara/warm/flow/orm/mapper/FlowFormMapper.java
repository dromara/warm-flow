package org.dromara.warm.flow.orm.mapper;

import org.apache.ibatis.annotations.Param;
import org.dromara.warm.flow.orm.entity.FlowDefinition;
import org.dromara.warm.flow.orm.entity.FlowForm;

import java.util.List;

/**
 * @author vanlin
 * @className FlowFormMapper
 * @description
 * @since 2024/8/19 14:30
 */
public interface FlowFormMapper extends WarmMapper<FlowForm> {
    List<FlowForm> queryByCodeList(@Param("formCodeList") List<String> formCodeList, @Param("entity") FlowForm entity);
}
