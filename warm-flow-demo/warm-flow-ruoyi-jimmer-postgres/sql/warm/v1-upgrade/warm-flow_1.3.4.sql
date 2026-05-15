update flow_definition set listener_path = REPLACE(listener_path, 'GlobalListener.GlobalAssignmentListener', 'listener.DefAssignmentListener');
update flow_definition set listener_path = REPLACE(listener_path, 'GlobalListener.GlobalCreateListener', 'listener.DefCreateListener');
update flow_definition set listener_path = REPLACE(listener_path, 'GlobalListener.GlobalFinishListener', 'listener.DefFinishListener');
update flow_definition set listener_path = REPLACE(listener_path, 'GlobalListener.GlobalStartListener', 'listener.DefStartListener');

INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (128, 10, '监听器', '10', 'leave_type', NULL, 'default', 'N', '0', 'admin', '2024-11-25 13:49:51', '', NULL, 'leaveFlow-serial6');

