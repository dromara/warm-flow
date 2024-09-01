-- flow-user 建表语句
CREATE TABLE `flow_user`
(
    `id`              bigint unsigned NOT NULL COMMENT '主键id',
    `type`            char(1)  NOT NULL COMMENT '人员类型（1待办任务的审批人权限 2待办任务的转办人权限 3待办任务的委托人权限）',
    `processed_by`    varchar(80) DEFAULT NULL COMMENT '权限人',
    `associated`      bigint NOT NULL COMMENT '任务表id',
    `create_time`     datetime     DEFAULT NULL COMMENT '创建时间',
    `create_by`       varchar(80) DEFAULT NULL COMMENT '创建人',
    `update_time`     datetime     DEFAULT NULL COMMENT '更新时间',
    `del_flag`        char(1)      DEFAULT NULL COMMENT '删除标志',
    `tenant_id`       varchar(40)  DEFAULT NULL COMMENT '租户id',
    PRIMARY KEY (`id`) USING BTREE,
    KEY `user_processed_type` (`processed_by`,`type`)
) ENGINE=InnoDB  COMMENT='流程用户表';;

-- flow_task 历史数据迁移脚本sql
insert into `flow_user` (id,type,associated,processed_by,create_time,update_time,del_flag,tenant_id)
SELECT
    a.id+CONVERT((@rowNum:=@rowNum+1),SIGNED),'1',a.id,
    substring_index( substring_index( a.permission_flag, ',', b.help_topic_id + 1 ), ',', - 1 ),
    a.create_time,a.update_time,a.del_flag,a.tenant_id
FROM (select id,permission_flag,create_time,update_time,del_flag,tenant_id from flow_task) a
INNER JOIN mysql.help_topic b
    ON b.help_topic_id < (length( a.permission_flag ) - length(REPLACE ( a.permission_flag, ',', '' )) + 1)
left join (Select (@rowNum :=0) ) c ON 1=1;

-- 去掉 flow_task 表废弃字段sql
ALTER TABLE `flow_task` DROP COLUMN approver;
ALTER TABLE `flow_task` DROP COLUMN assignee;
ALTER TABLE `flow_task` DROP COLUMN permission_flag;
ALTER TABLE `flow_task` DROP COLUMN flow_status;

-- 去掉 flow_his_task 表废弃字段sql
ALTER TABLE `flow_his_task` DROP COLUMN permission_flag;

-- flow_node 增加会签，票签字段
ALTER TABLE `flow_node`
    ADD COLUMN `handler_type` VARCHAR(100) NULL DEFAULT NULL COMMENT '处理器类型' AFTER `listener_path`,
    ADD COLUMN `handler_path` VARCHAR(400) NULL DEFAULT NULL COMMENT '处理器路径' AFTER `handler_type`,
    ADD COLUMN `node_ratio` DECIMAL(6,3) NULL DEFAULT NULL COMMENT '流程签署比例值' AFTER `node_name`;

-- flow_his_task 增加task_id
ALTER TABLE `flow_his_task`
    ADD COLUMN `task_id` BIGINT(19) NOT NULL COMMENT '对应flow_task表的id' AFTER `instance_id`,
    ADD COLUMN `cooperate_type` tinyint(1) NULL DEFAULT 0 COMMENT '协作方式(1审批 2转办 3委派 4会签 5票签 6加签 7减签)' AFTER `approver`,
    ADD COLUMN `collaborator` VARCHAR(40) NULL DEFAULT NULL COMMENT '协作人' AFTER `cooperate_type`;

ALTER TABLE `flow_definition`  DROP INDEX `flow_code_version`;
ALTER TABLE `flow_node` DROP INDEX `info_id_code`;
