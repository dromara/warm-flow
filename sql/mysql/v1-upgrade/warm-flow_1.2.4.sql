ALTER TABLE `flow_his_task`
    ADD COLUMN `skip_type` VARCHAR(10) NULL DEFAULT NULL COMMENT '流转类型（PASS通过 REJECT退回 NONE无动作）' AFTER `collaborator`;
