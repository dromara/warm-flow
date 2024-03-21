ALTER TABLE `flow_node`
    ADD COLUMN `listener_type` varchar(40) DEFAULT NULL COMMENT '监听器类型' AFTER `skip_any_node`,
ADD COLUMN `listener_path` varchar(200) DEFAULT NULL COMMENT '监听器路径' AFTER `listener_type`;

ALTER TABLE `flow_task`
    ADD COLUMN `variable` text COMMENT '任务变量' AFTER `gateway_node`;

ALTER TABLE `flow_instance`
    ADD COLUMN `variable` text COMMENT '任务变量' AFTER `node_name`;

ALTER TABLE `flow_instance`
    ADD COLUMN `tenant_id` varchar(40) DEFAULT NULL COMMENT '租户id' AFTER `business_id`