ALTER TABLE flow_instance ADD listener_type varchar(100) NULL;
COMMENT ON COLUMN flow_instance.listener_type IS '监听器类型';

ALTER TABLE flow_instance ADD listener_path varchar(400) NULL;
COMMENT ON COLUMN flow_instance.listener_path IS '监听器路径';

ALTER TABLE flow_instance ALTER COLUMN flow_status SET DATA TYPE varchar(20);
ALTER TABLE flow_his_task ALTER COLUMN flow_status SET DATA TYPE varchar(20);