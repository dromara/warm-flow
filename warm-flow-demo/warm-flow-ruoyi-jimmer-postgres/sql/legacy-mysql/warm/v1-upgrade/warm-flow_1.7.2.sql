CREATE TABLE `contract_process` (
                                    `id` bigint NOT NULL COMMENT '合同流程ID（主键）',
                                    `contract_name` varchar(255) NOT NULL COMMENT '合同名称',
                                    `contract_type` varchar(100) DEFAULT NULL COMMENT '合同类型（如采购、销售等）',
                                    `structure_and_nature` text COMMENT '合同结构和性质描述',
                                    `proposed_conditions` text COMMENT '拟定的合同条件',
                                    `negotiation_content` text COMMENT '谈判内容',
                                    `file_id` varchar(100) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '谈判附件',
                                    `negotiation_result` varchar(50) DEFAULT NULL COMMENT '谈判结果（如成功、有异议等）',
                                    `adjustment_scheme` text COMMENT '协商后的调整方案',
                                    `sign_date` datetime DEFAULT NULL COMMENT '合同签订日期',
                                    `signer` varchar(100) DEFAULT NULL COMMENT '签订人',
                                    `filing_date` datetime DEFAULT NULL COMMENT '备案日期',
                                    `filing_department` varchar(100) DEFAULT NULL COMMENT '备案部门',
                                    `instance_id` bigint DEFAULT NULL COMMENT '流程实例的id',
                                    `node_code` varchar(100) DEFAULT NULL COMMENT '节点编码',
                                    `node_name` varchar(100) DEFAULT NULL COMMENT '流程节点名称',
                                    `node_type` tinyint(1) NOT NULL COMMENT '节点类型（0开始节点 1中间节点 2结束节点 3互斥网关 4并行网关）',
                                    `flow_status` varchar(20) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '流程状态（0待提交 1审批中 2 审批通过 3自动通过 4终止 5作废 6撤销 7取回  8已完成 9已退回 10失效）',
                                    `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
                                    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                    `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
                                    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                    `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
                                    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB COMMENT='合同流程表';

INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `route_name`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (2007, '合同流程', 1061, 1, 'process', 'system/process/index', NULL, '', 1, 0, 'C', '0', '0', 'system:process:list', '#', 'admin', '2025-04-30 11:39:13', '', NULL, '合同流程菜单');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `route_name`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (2008, '合同流程查询', 2007, 1, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'system:process:query', '#', 'admin', '2025-04-30 11:39:13', '', NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `route_name`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (2009, '合同流程新增', 2007, 2, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'system:process:add', '#', 'admin', '2025-04-30 11:39:13', '', NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `route_name`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (2010, '合同流程修改', 2007, 3, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'system:process:edit', '#', 'admin', '2025-04-30 11:39:13', '', NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `route_name`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (2011, '合同流程删除', 2007, 4, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'system:process:remove', '#', 'admin', '2025-04-30 11:39:13', '', NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `route_name`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (2012, '合同流程导出', 2007, 5, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'system:process:export', '#', 'admin', '2025-04-30 11:39:13', '', NULL, '');

ALTER TABLE `flow_his_task` MODIFY COLUMN `ext` text NULL COMMENT '业务详情 存业务表对象json字符串' AFTER `variable`;

INSERT INTO `sys_user` (`user_id`, `dept_id`, `user_name`, `nick_name`, `user_type`, `email`, `phonenumber`, `sex`, `avatar`, `password`, `status`, `del_flag`, `login_ip`, `login_date`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (102, 106, 'contract_process_2', '拟定合同条件(用户)', '00', '', '', '0', '', '$2a$10$FMBbmYt8tcgOV3QmDYO9e.3jimjVxs.BggDb03AJXjLZtlgKeLOvO', '0', '0', '127.0.0.1', '2025-05-07 10:22:23', 'admin', '2025-05-06 14:30:56', 'admin', '2025-05-07 10:22:22', NULL);
INSERT INTO `sys_user` (`user_id`, `dept_id`, `user_name`, `nick_name`, `user_type`, `email`, `phonenumber`, `sex`, `avatar`, `password`, `status`, `del_flag`, `login_ip`, `login_date`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (103, 106, 'contract_process_1', '确定合同结构和性质(用户)', '00', '', '', '0', '', '$2a$10$tK4Y05uFDXzZFrANjgqwWeOns5yMd26Tin93kvJ5tjUDT/4eAzRde', '0', '0', '127.0.0.1', '2025-05-07 10:12:49', 'admin', '2025-05-06 14:42:31', 'admin', '2025-05-07 10:12:49', NULL);
INSERT INTO `sys_user` (`user_id`, `dept_id`, `user_name`, `nick_name`, `user_type`, `email`, `phonenumber`, `sex`, `avatar`, `password`, `status`, `del_flag`, `login_ip`, `login_date`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (104, 106, 'contract_process_3', '与合同签订单位谈判(用户)', '00', '', '', '0', '', '$2a$10$hE9xdfOZ984uV9nrILAgH.Gy1dIKeLnPe8p26yzdd5/5KI9hTzvC.', '0', '0', '127.0.0.1', '2025-05-07 10:29:54', 'admin', '2025-05-06 14:42:57', 'admin', '2025-05-07 10:29:54', NULL);
INSERT INTO `sys_user` (`user_id`, `dept_id`, `user_name`, `nick_name`, `user_type`, `email`, `phonenumber`, `sex`, `avatar`, `password`, `status`, `del_flag`, `login_ip`, `login_date`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (105, 106, 'contract_process_4', '合同签订(用户)', '00', '', '', '0', '', '$2a$10$IKZnM0HMIovStsrhO.PxD.ik0gONPnQjkyv1HVqzTBytYWDG0FSUu', '0', '2', '', NULL, 'admin', '2025-05-06 14:43:32', 'admin', '2025-05-06 14:59:17', NULL);
INSERT INTO `sys_user` (`user_id`, `dept_id`, `user_name`, `nick_name`, `user_type`, `email`, `phonenumber`, `sex`, `avatar`, `password`, `status`, `del_flag`, `login_ip`, `login_date`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (106, 106, 'contract_process_5', '项目合同和信息管理部门备案(用户)', '00', '', '', '0', '', '$2a$10$FZAs6oxWlhJjHkrL2YqqtOVb1VVMorRPe1rrreMNf9zh.lju6Py2W', '0', '0', '127.0.0.1', '2025-05-07 10:35:02', 'admin', '2025-05-06 14:43:53', 'admin', '2025-05-07 10:35:01', NULL);
INSERT INTO `sys_user` (`user_id`, `dept_id`, `user_name`, `nick_name`, `user_type`, `email`, `phonenumber`, `sex`, `avatar`, `password`, `status`, `del_flag`, `login_ip`, `login_date`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (107, 106, 'contract_process_n', '协商后做出调整方案(用户)', '00', '', '', '0', '', '$2a$10$lW7HkrrPRG6EiQmI4auzNeaCoAE5UNyVQhego1DJ7oU.nxUYcnR/e', '0', '0', '127.0.0.1', '2025-05-07 10:21:48', 'admin', '2025-05-06 14:45:05', 'admin', '2025-05-07 10:21:48', NULL);
INSERT INTO `sys_user` (`user_id`, `dept_id`, `user_name`, `nick_name`, `user_type`, `email`, `phonenumber`, `sex`, `avatar`, `password`, `status`, `del_flag`, `login_ip`, `login_date`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (108, 106, 'contract_process_l', '合同签订财务领导', '00', '', '', '0', '', '$2a$10$z1pR0.myNh/EAyYIACH8yOP9H17E9oJVnWnIgGGiLim75vRkL55EC', '0', '0', '127.0.0.1', '2025-05-07 10:32:51', 'admin', '2025-05-06 14:49:15', 'admin', '2025-05-07 10:32:50', NULL);
INSERT INTO `sys_user` (`user_id`, `dept_id`, `user_name`, `nick_name`, `user_type`, `email`, `phonenumber`, `sex`, `avatar`, `password`, `status`, `del_flag`, `login_ip`, `login_date`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (109, 103, 'contract_process_y', '合同签订研发领导', '00', '', '', '0', '', '$2a$10$7WKWNfWnOJ9Csod37tE3dOr0yDIVFkh9CaYu4RnbTwWuSuPmmJmru', '0', '0', '127.0.0.1', '2025-05-07 10:23:26', 'admin', '2025-05-06 15:43:16', '', '2025-05-07 10:23:26', NULL);
INSERT INTO `sys_user` (`user_id`, `dept_id`, `user_name`, `nick_name`, `user_type`, `email`, `phonenumber`, `sex`, `avatar`, `password`, `status`, `del_flag`, `login_ip`, `login_date`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (110, 106, 'contract_process_f1', '合同签订财务发起人', '00', '', '', '0', '', '$2a$10$iZtZ6nf/B65zYFF/dV4dH.RAOcvWylAuVakXd0By43XWUb7nJ36oK', '0', '0', '127.0.0.1', '2025-05-07 10:10:27', 'admin', '2025-05-06 15:44:09', '', '2025-05-07 10:10:26', NULL);
INSERT INTO `sys_user` (`user_id`, `dept_id`, `user_name`, `nick_name`, `user_type`, `email`, `phonenumber`, `sex`, `avatar`, `password`, `status`, `del_flag`, `login_ip`, `login_date`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (111, 103, 'contract_process_f2', '合同签订研发发起人', '00', '', '', '0', '', '$2a$10$4U2Wauo8GHjJCHLpNFSwp.PICloiFglVTYDhKprwQK79htDz0edLm', '0', '0', '', NULL, 'admin', '2025-05-06 15:44:36', '', NULL, NULL);
INSERT INTO `sys_user` (`user_id`, `dept_id`, `user_name`, `nick_name`, `user_type`, `email`, `phonenumber`, `sex`, `avatar`, `password`, `status`, `del_flag`, `login_ip`, `login_date`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (112, 106, 'contract_process_3_2', '与合同签订单位谈判(用户2)', '00', '', '', '0', '', '$2a$10$I0zRUSQ.AmFLqOQZTedyoOU46t1hHWFllmZp.a2X.qCuFLTirjTnG', '0', '0', '127.0.0.1', '2025-05-07 10:30:12', 'admin', '2025-05-06 16:11:32', '', '2025-05-07 10:30:12', NULL);
INSERT INTO `sys_user` (`user_id`, `dept_id`, `user_name`, `nick_name`, `user_type`, `email`, `phonenumber`, `sex`, `avatar`, `password`, `status`, `del_flag`, `login_ip`, `login_date`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (113, 106, 'contract_process_3_3', '与合同签订单位谈判(用户3)', '00', '', '', '0', '', '$2a$10$Qx7Ct2D6yP9WoxllDVcyQON5P/IjxEPKolyFW5T8JVcbPdkUQOD9C', '0', '0', '', NULL, 'admin', '2025-05-06 16:11:59', '', NULL, NULL);

INSERT INTO `sys_user_role` (`user_id`, `role_id`) VALUES (102, 2);
INSERT INTO `sys_user_role` (`user_id`, `role_id`) VALUES (103, 2);
INSERT INTO `sys_user_role` (`user_id`, `role_id`) VALUES (104, 2);
INSERT INTO `sys_user_role` (`user_id`, `role_id`) VALUES (106, 2);
INSERT INTO `sys_user_role` (`user_id`, `role_id`) VALUES (107, 2);
INSERT INTO `sys_user_role` (`user_id`, `role_id`) VALUES (108, 100);
INSERT INTO `sys_user_role` (`user_id`, `role_id`) VALUES (109, 100);
INSERT INTO `sys_user_role` (`user_id`, `role_id`) VALUES (110, 2);
INSERT INTO `sys_user_role` (`user_id`, `role_id`) VALUES (111, 2);
INSERT INTO `sys_user_role` (`user_id`, `role_id`) VALUES (112, 2);
INSERT INTO `sys_user_role` (`user_id`, `role_id`) VALUES (113, 2);

-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('企业采购', '1061', '1', 'steps', 'system/steps/index', 1, 0, 'C', '0', '0', 'system:steps:list', '#', 'admin', sysdate(), '', null, '企业采购菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('企业采购查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'system:steps:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('企业采购新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'system:steps:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('企业采购修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'system:steps:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('企业采购删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'system:steps:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('企业采购导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'system:steps:export',       '#', 'admin', sysdate(), '', null, '');

CREATE TABLE `procurement_steps` (
                                     `id` bigint NOT NULL COMMENT '主键ID',
                                     `purchase_name` varchar(255) DEFAULT NULL COMMENT '采购名称',
                                     `purchase_plan` varchar(2000) DEFAULT NULL COMMENT '采购计划',
                                     `urgent` char(1) DEFAULT NULL COMMENT '是否加急（0加急 1不加急）',
                                     `urgent_purchase_plan` varchar(2000) DEFAULT NULL COMMENT '加急采购计划',
                                     `on_demand_procurement` varchar(255) COMMENT '按需采购',
                                     `provide_items` varchar(255) COMMENT '提供物品',
                                     `product_inspection` char(1) DEFAULT NULL COMMENT '产品验收是否合格（0合格 1不合格）',
                                     `issue_invoice` varchar(255) COMMENT '出具发票',
                                     `record_entry` varchar(2000) DEFAULT NULL COMMENT '登记记录',
                                     `return_items` varchar(255) COMMENT '退货',
                                     `warehousing` varchar(255) COMMENT '入库',
                                     `receive_items` varchar(255) COMMENT '接收物品',
                                     `maintain_records` varchar(2000) DEFAULT NULL COMMENT '做好记录',
                                     `instance_id` bigint DEFAULT NULL COMMENT '流程实例的id',
                                     `node_code` varchar(100) COMMENT '节点编码',
                                     `node_name` varchar(100) COMMENT '流程节点名称',
                                     `node_type` tinyint(1) NOT NULL COMMENT '节点类型（0开始节点 1中间节点 2结束节点 3互斥网关 4并行网关）',
                                     `flow_status` varchar(20) DEFAULT NULL COMMENT '流程状态（0待提交 1审批中 2 审批通过 3自动通过 4终止 5作废 6撤销 7取回  8已完成 9已退回 10失效）',
                                     `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
                                     `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                     `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
                                     `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                     `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
                                     PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB COMMENT='企业采购流程表';

INSERT INTO `sys_user` (`user_id`, `dept_id`, `user_name`, `nick_name`, `user_type`, `email`, `phonenumber`, `sex`, `avatar`, `password`, `status`, `del_flag`, `login_ip`, `login_date`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (114, 108, 'procurement_steps_1', '需求部门(用户)', '00', '', '', '0', '', '$2a$10$6fokjBkNAFFW/Egx3zZvK.AqGxnxsFPumoOSdjgYAieMo4BQj6qSy', '0', '0', '', NULL, 'admin', '2025-05-09 15:57:33', '', NULL, NULL);
INSERT INTO `sys_user` (`user_id`, `dept_id`, `user_name`, `nick_name`, `user_type`, `email`, `phonenumber`, `sex`, `avatar`, `password`, `status`, `del_flag`, `login_ip`, `login_date`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (115, 108, 'procurement_steps_2', '财政部门(用户)', '00', '', '', '0', '', '$2a$10$T1U0jsXyItwUfkJsga648uBO86eo5dpu.DnQFeRiLNikCUmi.A8E.', '0', '0', '', NULL, 'admin', '2025-05-09 15:58:14', '', NULL, NULL);
INSERT INTO `sys_user` (`user_id`, `dept_id`, `user_name`, `nick_name`, `user_type`, `email`, `phonenumber`, `sex`, `avatar`, `password`, `status`, `del_flag`, `login_ip`, `login_date`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (116, 108, 'procurement_steps_3', '财政主管(用户)', '00', '', '', '0', '', '$2a$10$kc5BDFM5mVf.alEz0.XLce6KNjR.ZRi6SWXsD6NweDsy7UNhR9avm', '0', '0', '', NULL, 'admin', '2025-05-09 15:58:39', '', NULL, NULL);
INSERT INTO `sys_user` (`user_id`, `dept_id`, `user_name`, `nick_name`, `user_type`, `email`, `phonenumber`, `sex`, `avatar`, `password`, `status`, `del_flag`, `login_ip`, `login_date`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (117, 108, 'procurement_steps_4', '总经理(用户)', '00', '', '', '0', '', '$2a$10$TDhNZLPRoR9yblRMNLVu8uxx/.UCfiOX5wukealWuhv26LXXt5cSa', '0', '0', '', NULL, 'admin', '2025-05-09 15:59:01', 'admin', '2025-05-09 15:59:17', NULL);
INSERT INTO `sys_user` (`user_id`, `dept_id`, `user_name`, `nick_name`, `user_type`, `email`, `phonenumber`, `sex`, `avatar`, `password`, `status`, `del_flag`, `login_ip`, `login_date`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (119, 108, 'procurement_steps_5', '采购部门(用户)', '00', '', '', '0', '', '$2a$10$weDVKteBVpDBxiOfPUOE6.v7XScO0oTv/BGOYDNGETQKDkZrG.PIK', '0', '0', '', NULL, 'admin', '2025-05-09 16:00:35', '', NULL, NULL);
INSERT INTO `sys_user` (`user_id`, `dept_id`, `user_name`, `nick_name`, `user_type`, `email`, `phonenumber`, `sex`, `avatar`, `password`, `status`, `del_flag`, `login_ip`, `login_date`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (120, 108, 'procurement_steps_6', '供应商(用户)', '00', '', '', '0', '', '$2a$10$twZodXw3opMCx799MxGGD.1F2Wa7adofMnBwdjgezkPeExJje8Br2', '0', '0', '', NULL, 'admin', '2025-05-09 16:00:56', '', NULL, NULL);

INSERT INTO `sys_user_role` (`user_id`, `role_id`) VALUES (114, 2);
INSERT INTO `sys_user_role` (`user_id`, `role_id`) VALUES (115, 2);
INSERT INTO `sys_user_role` (`user_id`, `role_id`) VALUES (116, 100);
INSERT INTO `sys_user_role` (`user_id`, `role_id`) VALUES (117, 100);
INSERT INTO `sys_user_role` (`user_id`, `role_id`) VALUES (119, 2);
INSERT INTO `sys_user_role` (`user_id`, `role_id`) VALUES (120, 2);
