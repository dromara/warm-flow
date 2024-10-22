ALTER TABLE `flow_definition`
    ADD COLUMN `category` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '流程类别' AFTER `flow_name`
