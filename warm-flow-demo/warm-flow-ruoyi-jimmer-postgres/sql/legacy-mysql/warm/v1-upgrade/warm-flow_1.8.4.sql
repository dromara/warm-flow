ALTER TABLE flow_node MODIFY COLUMN node_ratio varchar(200) NULL COMMENT '流程签署比例值';
ALTER TABLE flow_node DROP COLUMN handler_type;
ALTER TABLE flow_node DROP COLUMN handler_path;

INSERT INTO sys_dict_data (dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark) VALUES(141, 16, '网关直连-复杂', '16', 'leave_type', NULL, 'default', 'N', '0', 'admin', '2025-11-13 14:43:13', 'admin', '2025-11-13 14:43:19', 'leaveFlow-direct2');
