ALTER TABLE `flow_his_task`
    ADD COLUMN `skip_type` VARCHAR(10) NULL DEFAULT NULL COMMENT '流转类型（PASS通过 REJECT退回 NONE无动作）' AFTER `collaborator`;

update 	flow_his_task set skip_type = 'PASS' where flow_status=2;

ALTER TABLE `flow_node`
    ADD COLUMN `form_custom`     char(1)      DEFAULT 'N' COMMENT '审批表单是否自定义（Y是 N否）' AFTER `handler_path`;

ALTER TABLE `flow_node`
    ADD COLUMN `form_path`    varchar(100)      DEFAULT NULL COMMENT '审批表单路径' AFTER `form_custom`;

ALTER TABLE `flow_definition`
    CHANGE COLUMN `from_custom` `form_custom` CHAR(1) NULL DEFAULT 'N' COMMENT '审批表单是否自定义（Y是 N否）' COLLATE 'utf8mb4_general_ci' AFTER `is_publish`;

ALTER TABLE `flow_definition`
    CHANGE COLUMN `from_path` `form_path` varchar(100) DEFAULT NULL COMMENT '审批表单路径' COLLATE 'utf8mb4_general_ci' AFTER `form_custom`;

ALTER TABLE `flow_definition`
    ADD COLUMN `ext` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '扩展字段，预留给业务系统使用' AFTER `form_path`
