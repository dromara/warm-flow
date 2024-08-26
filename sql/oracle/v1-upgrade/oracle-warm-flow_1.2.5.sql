ALTER TABLE "FLOW_INSTANCE"
    ADD ("LISTENER_TYPE" VARCHAR2(100) )
    ADD ("LISTENER_PATH" VARCHAR2(500) );
comment on column FLOW_INSTANCE.LISTENER_TYPE is '监听器类型';
comment on column FLOW_INSTANCE.LISTENER_PATH is '监听器路径';

ALTER TABLE flow_instance MODIFY flow_status varchar2(20);
ALTER TABLE flow_his_task MODIFY flow_status varchar2(20);