ALTER TABLE `flow_definition`
    ADD COLUMN `mode` varchar(40) NOT NULL DEFAULT 'CLASSICS' COMMENT '设计器模式（CLASSICS经典模式 MIMIC仿钉钉模式）' AFTER `flow_name`;
