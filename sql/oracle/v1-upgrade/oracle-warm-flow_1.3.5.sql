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


CREATE TABLE FLOW_FORM (
       ID NUMBER(20)  not null
           constraint FLOW_USER_PK
           primary key,
       FORM_CODE VARCHAR2(40 BYTE) NOT NULL,
       FORM_NAME VARCHAR2(100 BYTE) NOT NULL,
       VERSION VARCHAR2(20 BYTE) NOT NULL,
       IS_PUBLISH NUMBER(1) NOT NULL DEFAULT 0,
       FORM_TYPE NUMBER(1) DEFAULT 0,
       FORM_PATH VARCHAR2(100 BYTE) DEFAULT NULL,
       FORM_CONTENT CLOB DEFAULT NULL,
       EXT VARCHAR2(500),
       CREATE_TIME TIMESTAMP(6) DEFAULT NULL,
       UPDATE_TIME TIMESTAMP(6) DEFAULT NULL,
       DEL_FLAG VARCHAR2(1) default '0',
       TENANT_ID VARCHAR2(40 BYTE) DEFAULT NULL
)
    /

COMMENT ON TABLE FLOW_FORM IS '流程表单表'
/

COMMENT ON COLUMN FLOW_FORM.ID IS '主键ID'
/

COMMENT ON COLUMN FLOW_FORM.FORM_CODE IS '表单编码'
/

COMMENT ON COLUMN FLOW_FORM.FORM_NAME IS '表单名称'
/

COMMENT ON COLUMN FLOW_FORM.VERSION IS '表单版本'
/

COMMENT ON COLUMN FLOW_FORM.IS_PUBLISH IS '是否发布（0未发布 1已发布 9失效）'
/

COMMENT ON COLUMN FLOW_FORM.FORM_TYPE IS '表单类型（0内置表单 存 FORM_CONTENT 1外挂表单 存FORM_PATH）'
/

COMMENT ON COLUMN FLOW_FORM.FORM_PATH IS '表单路径'
/

COMMENT ON COLUMN FLOW_FORM.FORM_CONTENT IS '表单内容'
/

COMMENT ON COLUMN FLOW_FORM.EXT IS '表单扩展，用户自行使用'
/

COMMENT ON COLUMN FLOW_FORM.CREATE_TIME IS '创建时间'
/

COMMENT ON COLUMN FLOW_FORM.UPDATE_TIME IS '更新时间'
/

COMMENT ON COLUMN FLOW_FORM.DEL_FLAG IS '删除标志'
/

COMMENT ON COLUMN FLOW_FORM.TENANT_ID IS '租户ID'
/

ALTER TABLE "FLOW_HI_STASK" ADD ("VARIABLE" CLOB);

COMMENT ON COLUMN FLOW_HI_STASK.VARIABLE IS '任务变量'
/
