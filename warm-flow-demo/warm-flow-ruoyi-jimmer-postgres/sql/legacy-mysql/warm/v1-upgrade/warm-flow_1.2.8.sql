-- 升级了ruoyi系统的升级脚本，不是warm-flow的升级脚本
ALTER TABLE `sys_menu`
    ADD COLUMN `route_name` varchar(50) NULL DEFAULT '' COMMENT '路由名称' AFTER `query`;
