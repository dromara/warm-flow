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

import java.util.ArrayList;
import java.util.List;

/**
 * @author link2fun
 * 用于测试 EasyQuery 的功能
 *
 */
@SolonTest(value = SolonApp.class, properties = {"warm-flow.logic_delete=true"})
public class FlowEasyQueryWithLogicDeleteTest {

    @Inject
    private FlowUserDao userDao;

    @Db
    private EasyEntityQuery entityQuery;


    /** 测试逻辑删除功能开启了 */
    @Test
    @Tran
    public void testLogicDelete() {

        // 断言逻辑删除配置开启了
        Assertions.assertTrue(FlowFactory.getFlowConfig().isLogicDelete(),"逻辑删除配置未开启");


        // 断言查询的时候会自动加上逻辑删除条件
        Assertions.assertTrue(entityQuery.queryable(FlowDefinition.class).toSQL().endsWith("WHERE `del_flag` = ?"));
        Assertions.assertTrue(entityQuery.queryable(FlowHisTask.class).toSQL().endsWith("WHERE `del_flag` = ?"));
        Assertions.assertTrue(entityQuery.queryable(FlowInstance.class).toSQL().endsWith("WHERE `del_flag` = ?"));
        Assertions.assertTrue(entityQuery.queryable(FlowNode.class).toSQL().endsWith("WHERE `del_flag` = ?"));
        Assertions.assertTrue(entityQuery.queryable(FlowSkip.class).toSQL().endsWith("WHERE `del_flag` = ?"));
        Assertions.assertTrue(entityQuery.queryable(FlowTask.class).toSQL().endsWith("WHERE `del_flag` = ?"));
        Assertions.assertTrue(entityQuery.queryable(FlowUser.class).toSQL().endsWith("WHERE `del_flag` = ?"));


        // 断言更新的时候 会加上逻辑删除条件

        Assertions.assertEquals("UPDATE `flow_definition` SET `flow_name` = ? WHERE `del_flag` = ? AND `flow_code` = ?",
                entityQuery.updatable(FlowDefinition.class)
                .setColumns(s -> s.flowName().set("test"))
                .where(s -> s.flowCode().eq("test"))
                .toSQL());

        Assertions.assertEquals("UPDATE `flow_his_task` SET `target_node_name` = ? WHERE `del_flag` = ? AND `target_node_code` = ?",
                entityQuery.updatable(FlowHisTask.class)
                .setColumns(s -> s.targetNodeName().set("test"))
                .where(s -> s.targetNodeCode().eq("test"))
                .toSQL());

        Assertions.assertEquals("UPDATE `flow_instance` SET `node_name` = ? WHERE `del_flag` = ? AND `node_code` = ?",
                entityQuery.updatable(FlowInstance.class)
                .setColumns(s -> s.nodeName().set("test"))
                .where(s -> s.nodeCode().eq("test"))
                .toSQL());

        Assertions.assertEquals("UPDATE `flow_node` SET `node_name` = ? WHERE `del_flag` = ? AND `node_code` = ?",
                entityQuery.updatable(FlowNode.class)
                .setColumns(s -> s.nodeName().set("test"))
                .where(s -> s.nodeCode().eq("test"))
                .toSQL());

        Assertions.assertEquals("UPDATE `flow_skip` SET `skip_type` = ? WHERE `del_flag` = ? AND `skip_name` = ?",
                entityQuery.updatable(FlowSkip.class)
                .setColumns(s -> s.skipType().set("test"))
                .where(s -> s.skipName().eq("test"))
                .toSQL());


        Assertions.assertEquals("UPDATE `flow_task` SET `node_name` = ? WHERE `del_flag` = ? AND `node_code` = ?",
                entityQuery.updatable(FlowTask.class)
                .setColumns(s -> s.nodeName().set("test"))
                .where(s -> s.nodeCode().eq("test"))
                .toSQL());

        Assertions.assertEquals("UPDATE `flow_user` SET `create_by` = ? WHERE `del_flag` = ? AND `id` = ?",
                entityQuery.updatable(FlowUser.class)
                .setColumns(s -> s.createBy().set("test"))
                .where(s -> s.id().eq(1L))
                .toSQL());



        // 断言删除的时候使用的是更新语句
        Assertions.assertEquals("UPDATE `flow_definition` SET `del_flag` = ? WHERE `del_flag` = ? AND `flow_code` = ?",
                entityQuery.deletable(FlowDefinition.class)
                        .where(s -> s.flowCode().eq("test"))
                        .toSQL()
        );

        Assertions.assertEquals("UPDATE `flow_his_task` SET `del_flag` = ? WHERE `del_flag` = ? AND `target_node_code` = ?",
                entityQuery.deletable(FlowHisTask.class)
                        .where(s -> s.targetNodeCode().eq("test"))
                        .toSQL()
        );

        Assertions.assertEquals("UPDATE `flow_instance` SET `del_flag` = ? WHERE `del_flag` = ? AND `node_code` = ?",
                entityQuery.deletable(FlowInstance.class)
                        .where(s -> s.nodeCode().eq("test"))
                        .toSQL()
        );

        Assertions.assertEquals("UPDATE `flow_node` SET `del_flag` = ? WHERE `del_flag` = ? AND `node_code` = ?",
                entityQuery.deletable(FlowNode.class)
                        .where(s -> s.nodeCode().eq("test"))
                        .toSQL()
        );

        Assertions.assertEquals("UPDATE `flow_skip` SET `del_flag` = ? WHERE `del_flag` = ? AND `skip_name` = ?",
                entityQuery.deletable(FlowSkip.class)
                        .where(s -> s.skipName().eq("test"))
                        .toSQL()
        );

        Assertions.assertEquals("UPDATE `flow_task` SET `del_flag` = ? WHERE `del_flag` = ? AND `node_code` = ?",
                entityQuery.deletable(FlowTask.class)
                        .where(s -> s.nodeCode().eq("test"))
                        .toSQL()
        );

        Assertions.assertEquals("UPDATE `flow_user` SET `del_flag` = ? WHERE `del_flag` = ? AND `id` = ?",
                entityQuery.deletable(FlowUser.class)
                        .where(s -> s.id().eq(1L))
                        .toSQL()
        );






    }


}
