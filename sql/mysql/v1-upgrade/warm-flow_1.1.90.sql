ALTER TABLE `flow_his_task`
    MODIFY COLUMN `flow_status` tinyint(1) NOT NULL COMMENT '流程状态（0待提交 1审批中 2 审批通过 3自动通过 8已完成 9已退回 10失效）' AFTER `permission_flag`;

ALTER TABLE `flow_instance`
    MODIFY COLUMN `flow_status` tinyint(1) NOT NULL COMMENT '流程状态（0待提交 1审批中 2 审批通过 3自动通过 8已完成 9已退回 10失效）' AFTER `variable`;

ALTER TABLE `flow_skip`
    MODIFY COLUMN `skip_type` varchar(40) DEFAULT NULL COMMENT '跳转类型（PASS审批通过 REJECT退回）' AFTER `skip_name`;

ALTER TABLE `flow_task`
    MODIFY COLUMN `flow_status` tinyint(1) NOT NULL COMMENT '流程状态（0待提交 1审批中 2 审批通过 3自动通过 8已完成 9已退回 10失效）' AFTER `permission_flag`;

ALTER TABLE `flow_definition` ADD COLUMN `del_flag` char(1)  DEFAULT NULL COMMENT '删除标志' AFTER `update_time`;
ALTER TABLE `flow_his_task` ADD COLUMN `del_flag` char(1)  DEFAULT NULL COMMENT '删除标志' AFTER `update_time`;
ALTER TABLE `flow_instance` ADD COLUMN `del_flag` char(1)  DEFAULT NULL COMMENT '删除标志' AFTER `update_time`;
ALTER TABLE `flow_node` ADD COLUMN `del_flag` char(1)  DEFAULT NULL COMMENT '删除标志' AFTER `update_time`;
ALTER TABLE `flow_skip` ADD COLUMN `del_flag` char(1)  DEFAULT NULL COMMENT '删除标志' AFTER `update_time`;
ALTER TABLE `flow_task` ADD COLUMN `del_flag` char(1)  DEFAULT NULL COMMENT '删除标志' AFTER `update_time`;

ALTER TABLE `flow_definition` ADD COLUMN `tenant_id` varchar(40)  DEFAULT NULL COMMENT '租户id' AFTER `del_flag`;
ALTER TABLE `flow_node` ADD COLUMN `tenant_id` varchar(40)  DEFAULT NULL COMMENT '租户id' AFTER `del_flag`;
ALTER TABLE `flow_skip` ADD COLUMN `tenant_id` varchar(40)  DEFAULT NULL COMMENT '租户id' AFTER `del_flag`;

ALTER TABLE `flow_definition` DROP INDEX `flow_code_version`;

ALTER TABLE `flow_task` MODIFY COLUMN `tenant_id` varchar(40) DEFAULT NULL COMMENT '租户id' AFTER `del_flag`;
ALTER TABLE `flow_instance` MODIFY COLUMN `tenant_id` varchar(40) DEFAULT NULL COMMENT '租户id' AFTER `del_flag`;
ALTER TABLE `flow_his_task` MODIFY COLUMN `tenant_id` varchar(40) DEFAULT NULL COMMENT '租户id' AFTER `del_flag`;