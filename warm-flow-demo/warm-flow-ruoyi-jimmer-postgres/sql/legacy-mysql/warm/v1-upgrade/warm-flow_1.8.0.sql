ALTER TABLE `flow_definition`
    CHANGE COLUMN `mode` `model_value` varchar(40) NOT NULL DEFAULT 'CLASSICS' COMMENT '设计器模型（CLASSICS经典模型 MIMIC仿钉钉模型）' AFTER `flow_name`;

update flow_skip set skip_condition = REPLACE(skip_condition,'notNike','notLike');

ALTER TABLE `flow_his_task`
    MODIFY COLUMN `collaborator` varchar(500) NULL DEFAULT NULL COMMENT '协作人' AFTER `cooperate_type`;

INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (137, 12, '仿钉钉', '12', 'leave_type', NULL, 'default', 'N', '0', 'admin', '2025-08-01 10:09:38', 'admin', '2025-08-01 10:10:01', 'leaveFlow-mimic');
