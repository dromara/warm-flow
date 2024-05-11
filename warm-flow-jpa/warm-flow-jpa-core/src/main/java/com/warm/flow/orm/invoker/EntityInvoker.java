package com.warm.flow.orm.invoker;

import com.warm.flow.core.FlowFactory;
import com.warm.flow.orm.entity.*;

/**
 * 设置创建对象方法
 *
 * @author warm
 */
public class EntityInvoker {

    public static void setNewEntity() {
        FlowFactory.setNewDef(FlowDefinition::new);
        FlowFactory.setNewIns(FlowInstance::new);
        FlowFactory.setNewHisTask(FlowHisTask::new);
        FlowFactory.setNewNode(FlowNode::new);
        FlowFactory.setNewSkip(FlowSkip::new);
        FlowFactory.setNewTask(FlowTask::new);
    }
}
