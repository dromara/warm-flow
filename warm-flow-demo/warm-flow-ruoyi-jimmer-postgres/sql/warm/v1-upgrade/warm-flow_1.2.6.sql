ALTER TABLE `test_leave` MODIFY
    COLUMN `flow_status` varchar(20)
    DEFAULT NULL COMMENT '流程状态（0待提交 1审批中 2 审批通过 3自动通过 8已完成 9已退回 10失效）'
    AFTER `node_type`;