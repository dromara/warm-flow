ALTER TABLE `flow_definition`
    ADD COLUMN `mode` varchar(40) NOT NULL DEFAULT 'classics' COMMENT '设计器模式（classics经典模式 mimic仿钉钉模式）' AFTER `id`;
