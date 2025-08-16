ALTER TABLE `flow_definition`
    CHANGE COLUMN `mode` `model_value` varchar(40) NOT NULL DEFAULT 'CLASSICS' COMMENT '设计器模型（CLASSICS经典模型 MIMIC仿钉钉模型）' AFTER `flow_name`;

update flow_skip set skip_condition = REPLACE(skip_condition,'notNike','notLike');

ALTER TABLE `flow_his_task`
    MODIFY COLUMN `collaborator` varchar(500) NULL DEFAULT NULL COMMENT '协作人' AFTER `cooperate_type`;
