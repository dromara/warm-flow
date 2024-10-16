ALTER TABLE flow_definition ADD category varchar(100) NULL;
COMMENT ON COLUMN flow_definition.category IS '流程类别';
