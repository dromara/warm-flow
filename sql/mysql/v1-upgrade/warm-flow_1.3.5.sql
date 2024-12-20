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


CREATE TABLE `flow_form` (
                             `id`           bigint(20) UNSIGNED NOT NULL COMMENT '主键id',
                             `form_code`    varchar(40) NOT NULL COMMENT '表单编码',
                             `form_name`    varchar(100) NOT NULL COMMENT '表单名称',
                             `version`      varchar(20) NOT NULL COMMENT '表单版本',
                             `is_publish`   tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否发布（0未发布 1已发布 9失效）',
                             `form_type`    tinyint(1) NULL DEFAULT '0' COMMENT '表单类型（0内置表单 存 form_content 1外挂表单 存form_path）',
                             `form_path`    varchar(100) NULL DEFAULT NULL COMMENT '表单路径',
                             `form_content` longtext NULL DEFAULT NULL COMMENT '表单内容',
                             `ext`          varchar(400) NULL DEFAULT NULL COMMENT '表单扩展，用户自行使用',
                             `create_time`  datetime NULL DEFAULT NULL COMMENT '创建时间',
                             `update_time`  datetime NULL DEFAULT NULL COMMENT '更新时间',
                             `del_flag`     char(1) NULL DEFAULT '0' COMMENT '删除标志',
                             `tenant_id`    varchar(40) NULL DEFAULT NULL COMMENT '租户id',
                             PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB COMMENT='流程表单表';

ALTER TABLE `flow_his_task` ADD COLUMN `variable` text DEFAULT NULL COMMENT '任务变量' AFTER `message`;
