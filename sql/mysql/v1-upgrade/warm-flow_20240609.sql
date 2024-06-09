ALTER TABLE `flow_his_task`
    ADD COLUMN `ext` VARCHAR(400) NULL DEFAULT NULL COMMENT '业务详情 存业务表对象json字符串' AFTER `message`;
