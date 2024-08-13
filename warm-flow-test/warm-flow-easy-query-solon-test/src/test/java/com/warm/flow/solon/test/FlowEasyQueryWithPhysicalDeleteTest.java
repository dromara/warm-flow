package com.warm.flow.solon.test;

import com.easy.query.api.proxy.client.EasyEntityQuery;
import com.easy.query.solon.annotation.Db;
import com.warm.flow.core.FlowFactory;
import com.warm.flow.core.dao.FlowUserDao;
import com.warm.flow.orm.entity.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.noear.solon.annotation.Inject;
import org.noear.solon.data.annotation.Tran;
import org.noear.solon.test.SolonTest;

/**
 * @author link2fun
 * 用于测试 EasyQuery 的功能
 *
 */
@SolonTest(value = SolonApp.class, properties = {"warm-flow.logic_delete=false"})
public class FlowEasyQueryWithPhysicalDeleteTest {

    @Inject
    private FlowUserDao userDao;

    @Db
    private EasyEntityQuery entityQuery;


    /** 测试逻辑删除功能开启了 */
    @Test
    @Tran
    public void testPhysicalDelete() {

        // 断言逻辑删除配置开启了
        Assertions.assertTrue(!FlowFactory.getFlowConfig().isLogicDelete(),"逻辑删除开启了，当前应不开启");


        // 断言查询的时候会自动加上逻辑删除条件
        Assertions.assertTrue(entityQuery.queryable(FlowDefinition.class).toSQL().endsWith("FROM `flow_definition`"));
        Assertions.assertTrue(entityQuery.queryable(FlowHisTask.class).toSQL().endsWith("FROM `flow_his_task`"));
        Assertions.assertTrue(entityQuery.queryable(FlowInstance.class).toSQL().endsWith("FROM `flow_instance`"));
        Assertions.assertTrue(entityQuery.queryable(FlowNode.class).toSQL().endsWith("FROM `flow_node`"));
        Assertions.assertTrue(entityQuery.queryable(FlowSkip.class).toSQL().endsWith("FROM `flow_skip`"));
        Assertions.assertTrue(entityQuery.queryable(FlowTask.class).toSQL().endsWith("FROM `flow_task`"));
        Assertions.assertTrue(entityQuery.queryable(FlowUser.class).toSQL().endsWith("FROM `flow_user`"));


        // 断言更新的时候 不会加上逻辑删除条件

        Assertions.assertEquals("UPDATE `flow_definition` SET `flow_name` = ? WHERE `flow_code` = ?",
                entityQuery.updatable(FlowDefinition.class)
                .setColumns(s -> s.flowName().set("test"))
                .where(s -> s.flowCode().eq("test"))
                .toSQL());

        Assertions.assertEquals("UPDATE `flow_his_task` SET `target_node_name` = ? WHERE `target_node_code` = ?",
                entityQuery.updatable(FlowHisTask.class)
                .setColumns(s -> s.targetNodeName().set("test"))
                .where(s -> s.targetNodeCode().eq("test"))
                .toSQL());

        Assertions.assertEquals("UPDATE `flow_instance` SET `node_name` = ? WHERE `node_code` = ?",
                entityQuery.updatable(FlowInstance.class)
                .setColumns(s -> s.nodeName().set("test"))
                .where(s -> s.nodeCode().eq("test"))
                .toSQL());

        Assertions.assertEquals("UPDATE `flow_node` SET `node_name` = ? WHERE `node_code` = ?",
                entityQuery.updatable(FlowNode.class)
                .setColumns(s -> s.nodeName().set("test"))
                .where(s -> s.nodeCode().eq("test"))
                .toSQL());

        Assertions.assertEquals("UPDATE `flow_skip` SET `skip_type` = ? WHERE `skip_name` = ?",
                entityQuery.updatable(FlowSkip.class)
                .setColumns(s -> s.skipType().set("test"))
                .where(s -> s.skipName().eq("test"))
                .toSQL());


        Assertions.assertEquals("UPDATE `flow_task` SET `node_name` = ? WHERE `node_code` = ?",
                entityQuery.updatable(FlowTask.class)
                .setColumns(s -> s.nodeName().set("test"))
                .where(s -> s.nodeCode().eq("test"))
                .toSQL());

        Assertions.assertEquals("UPDATE `flow_user` SET `create_by` = ? WHERE `id` = ?",
                entityQuery.updatable(FlowUser.class)
                .setColumns(s -> s.createBy().set("test"))
                .where(s -> s.id().eq(1L))
                .toSQL());



        // 断言删除的时候使用的是delete语句
        boolean logicDelete = FlowFactory.getFlowConfig().isLogicDelete();
        Assertions.assertEquals("DELETE FROM `flow_definition` WHERE `flow_code` = ?",
                entityQuery.deletable(FlowDefinition.class)
                        .useLogicDelete(logicDelete)
                        .allowDeleteStatement(!logicDelete)
                        .where(s -> s.flowCode().eq("test"))
                        .toSQL()
        );

        Assertions.assertEquals("DELETE FROM `flow_his_task` WHERE `target_node_code` = ?",
                entityQuery.deletable(FlowHisTask.class)
                        .useLogicDelete(logicDelete)
                        .allowDeleteStatement(!logicDelete)
                        .where(s -> s.targetNodeCode().eq("test"))
                        .toSQL()
        );

        Assertions.assertEquals("DELETE FROM `flow_instance` WHERE `node_code` = ?",
                entityQuery.deletable(FlowInstance.class)
                        .useLogicDelete(logicDelete)
                        .allowDeleteStatement(!logicDelete)
                        .where(s -> s.nodeCode().eq("test"))
                        .toSQL()
        );

        Assertions.assertEquals("DELETE FROM `flow_node` WHERE `node_code` = ?",
                entityQuery.deletable(FlowNode.class)
                        .useLogicDelete(logicDelete)
                        .allowDeleteStatement(!logicDelete)
                        .where(s -> s.nodeCode().eq("test"))
                        .toSQL()
        );

        Assertions.assertEquals("DELETE FROM `flow_skip` WHERE `skip_name` = ?",
                entityQuery.deletable(FlowSkip.class)
                        .useLogicDelete(logicDelete)
                        .allowDeleteStatement(!logicDelete)
                        .where(s -> s.skipName().eq("test"))
                        .toSQL()
        );

        Assertions.assertEquals("DELETE FROM `flow_task` WHERE `node_code` = ?",
                entityQuery.deletable(FlowTask.class)
                        .useLogicDelete(logicDelete)
                        .allowDeleteStatement(!logicDelete)
                        .where(s -> s.nodeCode().eq("test"))
                        .toSQL()
        );

        Assertions.assertEquals("DELETE FROM `flow_user` WHERE `id` = ?",
                entityQuery.deletable(FlowUser.class)
                        .useLogicDelete(logicDelete)
                        .allowDeleteStatement(!logicDelete)
                        .where(s -> s.id().eq(1L))
                        .toSQL()
        );

        




    }


}
