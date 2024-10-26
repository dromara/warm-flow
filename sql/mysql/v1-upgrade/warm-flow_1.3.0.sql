ALTER TABLE `flow_definition`
    ADD COLUMN `category` varchar(100) DEFAULT NULL COMMENT '流程类别' AFTER `flow_name`
