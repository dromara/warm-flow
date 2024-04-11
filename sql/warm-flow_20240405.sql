ALTER TABLE `flow_node`
    MODIFY COLUMN `listener_type` varchar (100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '监听器类型' AFTER `skip_any_node`,
    MODIFY COLUMN `listener_path` varchar (400) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '监听器路径' AFTER `listener_type`;
ALTER TABLE `flow_his_task`
    MODIFY COLUMN `node_type` tinyint(1) NULL COMMENT '开始节点类型（0开始节点 1中间节点 2结束节点 3互斥网关 4并行网关）' AFTER `node_name`