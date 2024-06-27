ALTER TABLE `flow_his_task`
    ADD COLUMN `ext` VARCHAR(400) NULL DEFAULT NULL COMMENT '业务详情 存业务表对象json字符串' AFTER `message`;
ALTER TABLE `flow_his_task`
    MODIFY COLUMN `create_time` datetime NULL DEFAULT NULL COMMENT '开始时间' AFTER `message`,
    MODIFY COLUMN `update_time` datetime NULL DEFAULT NULL COMMENT '结束时间' AFTER `create_time`