package com.warm.flow.orm.invoker;

import com.warm.flow.core.FlowFactory;
import com.warm.flow.orm.entity.*;

/**
 * 执行mapper方法
 *
 * @author warm
 */
public class EntityInvoker {

    public static void setEntity() {
        FlowFactory.setDefSupplier(FlowDefinition::new);
        FlowFactory.setInsSupplier(FlowInstance::new);
        FlowFactory.setHisTaskSupplier(FlowHisTask::new);
        FlowFactory.setNodeSupplier(FlowNode::new);
        FlowFactory.setSkipSupplier(FlowSkip::new);
        FlowFactory.setTaskSupplier(FlowTask::new);
    }
}
