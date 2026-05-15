INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (135, 11, '拿回', '11', 'flow_status', NULL, 'warning', 'N', '0', 'admin', '2025-04-17 12:10:30', '', NULL, '拿回');
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (134, 6, '撤销', '6', 'flow_status', NULL, 'danger', 'N', '0', 'admin', '2025-04-16 12:01:38', '', NULL, '撤销');
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (136, 4, '终止', '4', 'flow_status', NULL, 'info', 'N', '0', 'admin', '2025-04-18 14:35:56', 'admin', '2025-04-18 14:36:03', '终止');

ALTER TABLE `flow_task`
    ADD COLUMN `flow_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL
    COMMENT '流程状态（0待提交 1审批中 2审批通过 4终止 5作废 6撤销 8已完成 9已退回 10失效 11拿回）' AFTER `node_type`;

ALTER TABLE `flow_instance`
    MODIFY COLUMN `flow_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL
    COMMENT '流程状态（0待提交 1审批中 2审批通过 4终止 5作废 6撤销 8已完成 9已退回 10失效 11拿回）' AFTER `variable`;

ALTER TABLE `flow_his_task`
    MODIFY COLUMN `flow_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL
    COMMENT '流程状态（0待提交 1审批中 2审批通过 4终止 5作废 6撤销 8已完成 9已退回 10失效 11拿回）' AFTER `skip_type`
