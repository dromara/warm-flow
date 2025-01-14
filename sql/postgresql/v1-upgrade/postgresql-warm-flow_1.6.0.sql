ALTER TABLE flow_his_task ADD COLUMN variable TEXT DEFAULT NULL COMMENT '任务变量' AFTER message;

ALTER TABLE flow_instance ADD COLUMN def_json TEXT DEFAULT NULL COMMENT '流程定义json' AFTER activity_status;

ALTER TABLE flow_his_task
ALTER COLUMN target_node_code TYPE VARCHAR(200) USING target_node_code::VARCHAR(200),
    ALTER COLUMN target_node_code SET DEFAULT NULL,
    ALTER COLUMN target_node_name TYPE VARCHAR(200) USING target_node_name::VARCHAR(200),
    ALTER COLUMN target_node_name SET DEFAULT NULL;
COMMENT ON COLUMN flow_his_task.target_node_code IS '目标节点编码';
COMMENT ON COLUMN flow_his_task.target_node_name IS '结束节点名称';


UPDATE flow_skip SET skip_condition = REPLACE(skip_condition, 'eq|', 'eq@@');

UPDATE flow_skip SET skip_condition = REPLACE(skip_condition, 'ge|', 'ge@@');

UPDATE flow_skip SET skip_condition = REPLACE(skip_condition, 'gt|', 'gt@@');

UPDATE flow_skip SET skip_condition = REPLACE(skip_condition, 'le|', 'le@@');

UPDATE flow_skip SET skip_condition = REPLACE(skip_condition, 'like|', 'like@@');

UPDATE flow_skip SET skip_condition = REPLACE(skip_condition, 'lt|', 'lt@@');

UPDATE flow_skip SET skip_condition = REPLACE(skip_condition, 'ne|', 'ne@@');

UPDATE flow_skip SET skip_condition = REPLACE(skip_condition, 'notNike|', 'notNike@@');

UPDATE flow_skip SET skip_condition = REPLACE(skip_condition, 'spel|', 'spel@@');
