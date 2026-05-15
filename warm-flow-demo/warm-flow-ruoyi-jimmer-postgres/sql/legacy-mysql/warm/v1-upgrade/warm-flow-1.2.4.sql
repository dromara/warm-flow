ALTER TABLE `test_leave`
    ADD COLUMN `node_type` tinyint(1) NOT NULL COMMENT '节点类型（0开始节点 1中间节点 2结束节点 3互斥网关 4并行网关）' AFTER `node_name`;

update flow_node set listener_path ='org.dromara.warm.flow.core.test.Listener.StartListener@@com.ruoyi.system.Listener.AssignmentListener@@org.dromara.warm.flow.core.test.Listener.FinishListener@@org.dromara.warm.flow.core.test.Listener.PermissionListener@@org.dromara.warm.flow.core.test.Listener.CreateListener';

INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (118, 7, '串行-复杂互斥', '7', 'leave_type', NULL, 'default', 'N', '0', 'admin', '2024-08-07 13:08:52', 'admin', '2024-08-12 15:22:52', 'leaveFlow-serial4');
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (119, 8, '并行-串行', '8', 'leave_type', NULL, 'default', 'N', '0', 'admin', '2024-08-12 15:23:18', '', NULL, 'leaveFlow-parallel3');
