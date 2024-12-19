update flow_skip set skip_condition = REPLACE(skip_condition,'@@eq@@|','eq|');
update flow_skip set skip_condition = REPLACE(skip_condition,'@@eq@@','|');

update flow_skip set skip_condition = REPLACE(skip_condition,'@@ge@@|','ge|');
update flow_skip set skip_condition = REPLACE(skip_condition,'@@ge@@','|');

update flow_skip set skip_condition = REPLACE(skip_condition,'@@gt@@|','gt|');
update flow_skip set skip_condition = REPLACE(skip_condition,'@@gt@@','|');

update flow_skip set skip_condition = REPLACE(skip_condition,'@@eq@@|','eq|');
update flow_skip set skip_condition = REPLACE(skip_condition,'@@eq@@','|');

update flow_skip set skip_condition = REPLACE(skip_condition,'@@le@@|','le|');
update flow_skip set skip_condition = REPLACE(skip_condition,'@@le@@','|');

update flow_skip set skip_condition = REPLACE(skip_condition,'@@eq@@|','eq|');
update flow_skip set skip_condition = REPLACE(skip_condition,'@@eq@@','|');

update flow_skip set skip_condition = REPLACE(skip_condition,'@@like@@|','like|');
update flow_skip set skip_condition = REPLACE(skip_condition,'@@like@@','|');

update flow_skip set skip_condition = REPLACE(skip_condition,'@@lt@@|','lt|');
update flow_skip set skip_condition = REPLACE(skip_condition,'@@lt@@','|');

update flow_skip set skip_condition = REPLACE(skip_condition,'@@ne@@|','ne|');
update flow_skip set skip_condition = REPLACE(skip_condition,'@@ne@@','|');

update flow_skip set skip_condition = REPLACE(skip_condition,'@@notNike@@|','notNike|');
update flow_skip set skip_condition = REPLACE(skip_condition,'@@notNike@@','|');

update flow_skip set skip_condition = REPLACE(skip_condition,'@@spel@@|','spel|');

ALTER TABLE `flow_node`
    MODIFY COLUMN `skip_any_node` varchar(100) NULL DEFAULT 'N' COMMENT '是否可以退回任意节点（Y是 N否）即将删除' AFTER `coordinate`,
    ADD COLUMN `any_node_skip` varchar(100) DEFAULT NULL COMMENT '任意结点跳转' AFTER `skip_any_node`;
