UPDATE flow_skip SET skip_condition = REPLACE(skip_condition, '@@eq@@|', 'eq|');
UPDATE flow_skip SET skip_condition = REPLACE(skip_condition, '@@eq@@', '|');

UPDATE flow_skip SET skip_condition = REPLACE(skip_condition, '@@ge@@|', 'ge|');
UPDATE flow_skip SET skip_condition = REPLACE(skip_condition, '@@ge@@', '|');

UPDATE flow_skip SET skip_condition = REPLACE(skip_condition, '@@gt@@|', 'gt|');
UPDATE flow_skip SET skip_condition = REPLACE(skip_condition, '@@gt@@', '|');

UPDATE flow_skip SET skip_condition = REPLACE(skip_condition, '@@le@@|', 'le|');
UPDATE flow_skip SET skip_condition = REPLACE(skip_condition, '@@le@@', '|');

UPDATE flow_skip SET skip_condition = REPLACE(skip_condition, '@@like@@|', 'like|');
UPDATE flow_skip SET skip_condition = REPLACE(skip_condition, '@@like@@', '|');

UPDATE flow_skip SET skip_condition = REPLACE(skip_condition, '@@lt@@|', 'lt|');
UPDATE flow_skip SET skip_condition = REPLACE(skip_condition, '@@lt@@', '|');

UPDATE flow_skip SET skip_condition = REPLACE(skip_condition, '@@ne@@|', 'ne|');
UPDATE flow_skip SET skip_condition = REPLACE(skip_condition, '@@ne@@', '|');

UPDATE flow_skip SET skip_condition = REPLACE(skip_condition, '@@notNike@@|', 'notNike|');
UPDATE flow_skip SET skip_condition = REPLACE(skip_condition, '@@notNike@@', '|');

UPDATE flow_skip SET skip_condition = REPLACE(skip_condition, '@@spel@@|', 'spel|');

ALTER TABLE flow_node ADD any_node_skip VARCHAR2(100) DEFAULT NULL;

COMMENT ON COLUMN flow_node.skip_any_node IS '是否可以退回任意节点（Y是 N否）即将删除';
COMMENT ON COLUMN flow_node.any_node_skip IS '任意结点跳转';
