ALTER TABLE `flow_instance`
    ADD COLUMN `listener_type` varchar(100) DEFAULT NULL COMMENT '监听器类型' AFTER `activity_status`;

ALTER TABLE `flow_instance`
    ADD COLUMN `listener_path` varchar(400) DEFAULT NULL COMMENT '监听器路径' AFTER `listener_type`;