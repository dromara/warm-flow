ALTER TABLE flow_definition RENAME COLUMN from_custom TO form_custom;

ALTER TABLE flow_definition RENAME COLUMN from_path TO form_path;

ALTER TABLE flow_definition ADD ext varchar(500) NULL;
COMMENT ON COLUMN flow_definition.ext IS '扩展字段，预留给业务系统使用';

ALTER TABLE flow_definition ALTER COLUMN del_flag SET DEFAULT '0'::character varying;

ALTER TABLE flow_his_task ADD skip_type varchar(10) NULL;
COMMENT ON COLUMN flow_his_task.skip_type IS '流转类型（PASS通过 REJECT退回 NONE无动作）';

UPDATE flow_his_task SET skip_type = 'PASS' WHERE flow_status = 2;

ALTER TABLE flow_his_task ADD form_custom bpchar(1) NULL DEFAULT 'N'::character varying;
COMMENT ON COLUMN flow_his_task.form_custom IS '审批表单是否自定义（Y是 N否）';

ALTER TABLE flow_his_task ADD form_path varchar(100) NULL;
COMMENT ON COLUMN flow_his_task.form_path IS '审批表单路径';

ALTER TABLE flow_his_task ALTER COLUMN del_flag SET DEFAULT '0'::character varying;

ALTER TABLE flow_instance ALTER COLUMN del_flag SET DEFAULT '0'::character varying;

ALTER TABLE flow_node ADD form_custom bpchar(1) NULL DEFAULT 'N'::character varying;
COMMENT ON COLUMN flow_node.form_custom IS '审批表单是否自定义（Y是 N否）';

ALTER TABLE flow_node ADD form_path varchar(100) NULL;
COMMENT ON COLUMN flow_node.form_path IS '审批表单路径';

ALTER TABLE flow_node ALTER COLUMN del_flag SET DEFAULT '0'::character varying;

ALTER TABLE flow_skip ALTER COLUMN del_flag SET DEFAULT '0'::character varying;

ALTER TABLE flow_task ADD form_custom bpchar(1) NULL DEFAULT 'N'::character varying;
COMMENT ON COLUMN flow_task.form_custom IS '审批表单是否自定义（Y是 N否）';

ALTER TABLE flow_task ADD form_path varchar(100) NULL;
COMMENT ON COLUMN flow_task.form_path IS '审批表单路径';

ALTER TABLE flow_task ALTER COLUMN del_flag SET DEFAULT '0'::character varying;

ALTER TABLE flow_user ALTER COLUMN del_flag SET DEFAULT '0'::character varying;

