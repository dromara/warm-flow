ALTER TABLE `flow_definition`
    ADD COLUMN `listener_type` varchar(100) DEFAULT NULL COMMENT '监听器类型' AFTER `activity_status`;

ALTER TABLE `flow_definition`
    ADD COLUMN `listener_path` varchar(400) DEFAULT NULL COMMENT '监听器路径' AFTER `listener_type`;

-- 流程实例和流程历史表修改流程状态字段类型为字符串类型-varchar
ALTER TABLE `flow_instance` MODIFY
    COLUMN `flow_status` varchar(20)
    DEFAULT NULL COMMENT '流程状态（0待提交 1审批中 2 审批通过 3自动通过 8已完成 9已退回 10失效）'
    AFTER `variable`;

ALTER TABLE `flow_his_task` MODIFY
    COLUMN `flow_status` varchar(20)
    DEFAULT NULL COMMENT '流程状态（0待提交 1审批中 2 审批通过 3自动通过 8已完成 9已退回 10失效）'
    AFTER `skip_type`;