ALTER TABLE `flow_definition`
    CHANGE COLUMN `mode` `model_value` varchar(40) NOT NULL DEFAULT 'CLASSICS' COMMENT '设计器模型（CLASSICS经典模型 MIMIC仿钉钉模型）' AFTER `flow_name`
