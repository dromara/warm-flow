ALTER TABLE flow_definition ADD listener_type varchar(100) NULL;
COMMENT ON COLUMN flow_definition.listener_type IS '监听器类型';

ALTER TABLE flow_definition ADD listener_path varchar(400) NULL;
COMMENT ON COLUMN flow_definition.listener_path IS '监听器路径';