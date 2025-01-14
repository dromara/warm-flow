ALTER TABLE `flow_his_task` ADD COLUMN `variable` text DEFAULT NULL COMMENT '任务变量' AFTER `message`;

ALTER TABLE `flow_instance` ADD COLUMN `def_json` text  DEFAULT NULL COMMENT '流程定义json' AFTER `activity_status`;

ALTER TABLE `flow_his_task`
    MODIFY COLUMN `target_node_code` varchar(200) NULL DEFAULT NULL COMMENT '目标节点编码' AFTER `node_type`,
    MODIFY COLUMN `target_node_name` varchar(200) NULL DEFAULT NULL COMMENT '结束节点名称' AFTER `target_node_code`;

update flow_skip set skip_condition = REPLACE(skip_condition,'eq|','eq@@');

update flow_skip set skip_condition = REPLACE(skip_condition,'ge|','ge@@');

update flow_skip set skip_condition = REPLACE(skip_condition,'gt|','gt@@');

update flow_skip set skip_condition = REPLACE(skip_condition,'eq|','eq@@');

update flow_skip set skip_condition = REPLACE(skip_condition,'le|','le@@');

update flow_skip set skip_condition = REPLACE(skip_condition,'eq|','eq@@');

update flow_skip set skip_condition = REPLACE(skip_condition,'like|','like@@');

update flow_skip set skip_condition = REPLACE(skip_condition,'lt|','lt@@');

update flow_skip set skip_condition = REPLACE(skip_condition,'ne|','ne@@');

update flow_skip set skip_condition = REPLACE(skip_condition,'notNike|','notNike@@');

update flow_skip set skip_condition = REPLACE(skip_condition,'spel|','spel@@');
