CREATE TABLE `flow_definition` (
                                   `id` bigint unsigned NOT NULL COMMENT '主键id',
                                   `flow_code` varchar(40)  NOT NULL COMMENT '流程编码',
                                   `flow_name` varchar(100)  NOT NULL COMMENT '流程名称',
                                   `version` varchar(20)  NOT NULL COMMENT '流程版本',
                                   `is_publish` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否发布（0未发布 1已发布 9失效）',
                                   `from_custom` char(1)  DEFAULT 'N' COMMENT '审批表单是否自定义（Y是 N否）',
                                   `from_path` varchar(100)  DEFAULT NULL COMMENT '审批表单路径',
                                   `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                   `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                   PRIMARY KEY (`id`) USING BTREE,
                                   UNIQUE KEY `flow_code_version` (`flow_code`,`version`) USING BTREE
) ENGINE=InnoDB  COMMENT='流程定义表';

CREATE TABLE `flow_his_task` (
                                 `id` bigint unsigned NOT NULL COMMENT '主键id',
                                 `definition_id` bigint NOT NULL COMMENT '对应flow_definition表的id',
                                 `instance_id` bigint NOT NULL COMMENT '对应flow_instance表的id',
                                 `tenant_id` varchar(40)  DEFAULT NULL COMMENT '租户id',
                                 `node_code` varchar(100)  DEFAULT NULL COMMENT '开始节点编码',
                                 `node_name` varchar(100)  DEFAULT NULL COMMENT '开始节点名称',
                                 `node_type` tinyint(1) DEFAULT NULL COMMENT '开始节点类型（0开始节点 1中间节点 2结束节点 3互斥网关 4并行网关）',
                                 `target_node_code` varchar(100)  DEFAULT NULL COMMENT '目标节点编码',
                                 `target_node_name` varchar(100)  DEFAULT NULL COMMENT '结束节点名称',
                                 `approver` varchar(40)  DEFAULT NULL COMMENT '审批者',
                                 `permission_flag` varchar(200)  DEFAULT NULL COMMENT '权限标识（权限类型:权限标识，可以多个，如role:1,role:2)',
                                 `flow_status` tinyint(1) NOT NULL COMMENT '流程状态（0待提交 1审批中 2 审批通过 8已完成 9已驳回 10失效）',
                                 `message` varchar(500)  DEFAULT NULL COMMENT '审批意见',
                                 `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                 `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                 PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB  COMMENT='历史任务记录表';

CREATE TABLE `flow_instance` (
                                 `id` bigint NOT NULL COMMENT '主键id',
                                 `definition_id` bigint NOT NULL COMMENT '对应flow_definition表的id',
                                 `business_id` varchar(40)  NOT NULL COMMENT '业务id',
                                 `tenant_id` varchar(40)   DEFAULT NULL COMMENT '租户id',
                                 `node_type` tinyint(1) NOT NULL COMMENT '结点类型（0开始节点 1中间节点 2结束节点 3互斥网关 4并行网关）',
                                 `node_code` varchar(40)  NOT NULL COMMENT '流程节点编码',
                                 `node_name` varchar(100)  DEFAULT NULL COMMENT '流程节点名称',
                                 `variable` text  COMMENT '任务变量',
                                 `flow_status` tinyint(1) NOT NULL COMMENT '流程状态（0待提交 1审批中 2 审批通过 8已完成 9已驳回 10失效）',
                                 `create_by` varchar(64)  DEFAULT '' COMMENT '创建者',
                                 `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                 `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                 `ext` varchar(500)  DEFAULT NULL COMMENT '扩展字段',
                                 PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB  COMMENT='流程实例表';

CREATE TABLE `flow_node` (
                             `id` bigint unsigned NOT NULL COMMENT '主键id',
                             `node_type` tinyint(1) NOT NULL COMMENT '节点类型（0开始节点 1中间节点 2结束结点 3互斥网关 4并行网关）',
                             `definition_id` bigint NOT NULL COMMENT '流程定义id',
                             `node_code` varchar(100)  NOT NULL COMMENT '流程节点编码',
                             `node_name` varchar(100)  DEFAULT NULL COMMENT '流程节点名称',
                             `permission_flag` varchar(200)  DEFAULT NULL COMMENT '权限标识（权限类型:权限标识，可以多个，如role:1,role:2)',
                             `coordinate` varchar(100)  DEFAULT NULL COMMENT '坐标',
                             `skip_any_node` varchar(100)  DEFAULT 'N' COMMENT '是否可以退回任意节点（Y是 N否）',
                             `listener_type` varchar(100)   DEFAULT NULL COMMENT '监听器类型',
                             `listener_path` varchar(400)   DEFAULT NULL COMMENT '监听器路径',
                             `version` varchar(20)  NOT NULL COMMENT '版本',
                             `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                             `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                             PRIMARY KEY (`id`) USING BTREE,
                             UNIQUE KEY `info_id_code` (`definition_id`,`node_code`) USING BTREE COMMENT '保证一个流程中node_code是唯一的'
) ENGINE=InnoDB  COMMENT='流程结点表';

CREATE TABLE `flow_skip` (
                             `id` bigint unsigned NOT NULL COMMENT '主键id',
                             `definition_id` bigint NOT NULL COMMENT '流程定义id',
                             `now_node_code` varchar(100)  NOT NULL COMMENT '当前流程节点的编码',
                             `now_node_type` tinyint(1) DEFAULT NULL COMMENT '当前节点类型（0开始节点 1中间节点 2结束节点 3互斥网关 4并行网关）',
                             `next_node_code` varchar(100)  NOT NULL COMMENT '下一个流程节点的编码',
                             `next_node_type` tinyint(1) DEFAULT NULL COMMENT '下一个节点类型（0开始节点 1中间节点 2结束节点 3互斥网关 4并行网关）',
                             `skip_name` varchar(100)  DEFAULT NULL COMMENT '跳转名称',
                             `skip_type` varchar(40)  DEFAULT NULL COMMENT '跳转类型（PASS审批通过 REJECT驳回）',
                             `skip_condition` varchar(200)  DEFAULT NULL COMMENT '跳转条件',
                             `coordinate` varchar(100)  DEFAULT NULL COMMENT '坐标',
                             `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                             `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                             PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB  COMMENT='结点跳转关联表';

CREATE TABLE `flow_task` (
                             `id` bigint NOT NULL COMMENT '主键id',
                             `definition_id` bigint NOT NULL COMMENT '对应flow_definition表的id',
                             `instance_id` bigint NOT NULL COMMENT '对应flow_instance表的id',
                             `tenant_id` varchar(40)  DEFAULT NULL COMMENT '租户id',
                             `node_code` varchar(100)  NOT NULL COMMENT '节点编码',
                             `node_name` varchar(100)  DEFAULT NULL COMMENT '节点名称',
                             `node_type` tinyint(1) NOT NULL COMMENT '节点类型（0开始节点 1中间节点 2结束节点 3互斥网关 4并行网关）',
                             `permission_flag` varchar(200)  DEFAULT NULL COMMENT '权限标识（权限类型:权限标识，可以多个，如role:1,role:2)',
                             `flow_status` tinyint(1) NOT NULL COMMENT '流程状态（0待提交 1审批中 2 审批通过 8已完成 9已驳回 10失效）',
                             `approver` varchar(40)  DEFAULT NULL COMMENT '审批者',
                             `assignee` varchar(40)  DEFAULT NULL COMMENT '转办人',
                             `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                             `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                             PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB  COMMENT='待办任务表';

