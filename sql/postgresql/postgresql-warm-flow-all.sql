CREATE TABLE flow_definition (
	id int8 NOT NULL, -- 主键id
	flow_code varchar(40) NOT NULL, -- 流程编码
	flow_name varchar(100) NOT NULL, -- 流程名称
    category varchar(100) NOT NULL, -- 流程类别
	"version" varchar(20) NOT NULL, -- 流程版本
	is_publish int2 NOT NULL DEFAULT 0, -- 是否发布（0未发布 1已发布 9失效）
	form_custom bpchar(1) NULL DEFAULT 'N'::character varying, -- 审批表单是否自定义（Y是 N否）
	form_path varchar(100) NULL, -- 审批表单路径
    activity_status int2 NOT NULL DEFAULT 1, -- 流程激活状态（0挂起 1激活）
    listener_type varchar(100) NULL, -- 监听器类型
    listener_path varchar(400) NULL, -- 监听器路径
    ext varchar(500) NULL, -- 扩展字段，预留给业务系统使用
    create_time timestamp NULL, -- 创建时间
	update_time timestamp NULL, -- 更新时间
	del_flag bpchar(1) NULL DEFAULT '0'::character varying, -- 删除标志
	tenant_id varchar(40) NULL, -- 租户id
	CONSTRAINT flow_definition_pkey PRIMARY KEY (id)
);
COMMENT ON TABLE flow_definition IS '流程定义表';

COMMENT ON COLUMN flow_definition.id IS '主键id';
COMMENT ON COLUMN flow_definition.flow_code IS '流程编码';
COMMENT ON COLUMN flow_definition.flow_name IS '流程名称';
COMMENT ON COLUMN flow_definition.category IS '流程类别';
COMMENT ON COLUMN flow_definition."version" IS '流程版本';
COMMENT ON COLUMN flow_definition.is_publish IS '是否发布（0未发布 1已发布 9失效）';
COMMENT ON COLUMN flow_definition.form_custom IS '审批表单是否自定义（Y是 N否）';
COMMENT ON COLUMN flow_definition.form_path IS '审批表单路径';
COMMENT ON COLUMN flow_definition.activity_status IS '流程激活状态（0挂起 1激活）';
COMMENT ON COLUMN flow_definition.listener_type IS '监听器类型';
COMMENT ON COLUMN flow_definition.listener_path IS '监听器路径';
COMMENT ON COLUMN flow_definition.ext IS '扩展字段，预留给业务系统使用';
COMMENT ON COLUMN flow_definition.create_time IS '创建时间';
COMMENT ON COLUMN flow_definition.update_time IS '更新时间';
COMMENT ON COLUMN flow_definition.del_flag IS '删除标志';
COMMENT ON COLUMN flow_definition.tenant_id IS '租户id';

CREATE TABLE flow_node (
    id int8 NOT NULL, -- 主键id
    node_type int2 NOT NULL, -- 节点类型（0开始节点 1中间节点 2结束结点 3互斥网关 4并行网关）
    definition_id int8 NOT NULL, -- 流程定义id
    node_code varchar(100) NOT NULL, -- 流程节点编码
    node_name varchar(100) NULL, -- 流程节点名称
    permission_flag varchar(200) NULL, -- 权限标识（权限类型:权限标识，可以多个，用逗号隔开)
    node_ratio numeric(6, 3) NULL, -- 流程签署比例值
    coordinate varchar(100) NULL, -- 坐标
    skip_any_node varchar(100) NULL DEFAULT 'N'::character varying, -- 是否可以退回任意节点（Y是 N否）
    listener_type varchar(100) NULL, -- 监听器类型
    listener_path varchar(400) NULL, -- 监听器路径
    handler_type varchar(100) NULL, -- 处理器类型
    handler_path varchar(400) NULL, -- 处理器路径
    form_custom bpchar(1) NULL DEFAULT 'N'::character varying, -- 审批表单是否自定义（Y是 N否）
    form_path varchar(100) NULL, -- 审批表单路径
    "version" varchar(20) NOT NULL, -- 版本
    create_time timestamp NULL, -- 创建时间
    update_time timestamp NULL, -- 更新时间
    del_flag bpchar(1) NULL DEFAULT '0'::character varying, -- 删除标志
    tenant_id varchar(40) NULL, -- 租户id
    CONSTRAINT flow_node_pkey PRIMARY KEY (id),
    CONSTRAINT info_id_code UNIQUE (definition_id, node_code)
);
COMMENT ON TABLE flow_node IS '流程结点表';

COMMENT ON COLUMN flow_node.id IS '主键id';
COMMENT ON COLUMN flow_node.node_type IS '节点类型（0开始节点 1中间节点 2结束结点 3互斥网关 4并行网关）';
COMMENT ON COLUMN flow_node.definition_id IS '流程定义id';
COMMENT ON COLUMN flow_node.node_code IS '流程节点编码';
COMMENT ON COLUMN flow_node.node_name IS '流程节点名称';
COMMENT ON COLUMN flow_node.permission_flag IS '权限标识（权限类型:权限标识，可以多个，用逗号隔开)';
COMMENT ON COLUMN flow_node.node_ratio IS '流程签署比例值';
COMMENT ON COLUMN flow_node.coordinate IS '坐标';
COMMENT ON COLUMN flow_node.skip_any_node IS '是否可以退回任意节点（Y是 N否）';
COMMENT ON COLUMN flow_node.listener_type IS '监听器类型';
COMMENT ON COLUMN flow_node.listener_path IS '监听器路径';
COMMENT ON COLUMN flow_node.handler_type IS '处理器类型';
COMMENT ON COLUMN flow_node.handler_path IS '处理器路径';
COMMENT ON COLUMN flow_node.form_custom IS '审批表单是否自定义（Y是 N否）';
COMMENT ON COLUMN flow_node.form_path IS '审批表单路径';
COMMENT ON COLUMN flow_node."version" IS '版本';
COMMENT ON COLUMN flow_node.create_time IS '创建时间';
COMMENT ON COLUMN flow_node.update_time IS '更新时间';
COMMENT ON COLUMN flow_node.del_flag IS '删除标志';
COMMENT ON COLUMN flow_node.tenant_id IS '租户id';


CREATE TABLE flow_skip (
    id int8 NOT NULL, -- 主键id
    definition_id int8 NOT NULL, -- 流程定义id
    now_node_code varchar(100) NOT NULL, -- 当前流程节点的编码
    now_node_type int2 NULL, -- 当前节点类型（0开始节点 1中间节点 2结束节点 3互斥网关 4并行网关）
    next_node_code varchar(100) NOT NULL, -- 下一个流程节点的编码
    next_node_type int2 NULL, -- 下一个节点类型（0开始节点 1中间节点 2结束节点 3互斥网关 4并行网关）
    skip_name varchar(100) NULL, -- 跳转名称
    skip_type varchar(40) NULL, -- 跳转类型（PASS审批通过 REJECT退回）
    skip_condition varchar(200) NULL, -- 跳转条件
    coordinate varchar(100) NULL, -- 坐标
    create_time timestamp NULL, -- 创建时间
    update_time timestamp NULL, -- 更新时间
    del_flag bpchar(1) NULL DEFAULT '0'::character varying, -- 删除标志
    tenant_id varchar(40) NULL, -- 租户id
    CONSTRAINT flow_skip_pkey PRIMARY KEY (id)
);
COMMENT ON TABLE flow_skip IS '结点跳转关联表';

COMMENT ON COLUMN flow_skip.id IS '主键id';
COMMENT ON COLUMN flow_skip.definition_id IS '流程定义id';
COMMENT ON COLUMN flow_skip.now_node_code IS '当前流程节点的编码';
COMMENT ON COLUMN flow_skip.now_node_type IS '当前节点类型（0开始节点 1中间节点 2结束节点 3互斥网关 4并行网关）';
COMMENT ON COLUMN flow_skip.next_node_code IS '下一个流程节点的编码';
COMMENT ON COLUMN flow_skip.next_node_type IS '下一个节点类型（0开始节点 1中间节点 2结束节点 3互斥网关 4并行网关）';
COMMENT ON COLUMN flow_skip.skip_name IS '跳转名称';
COMMENT ON COLUMN flow_skip.skip_type IS '跳转类型（PASS审批通过 REJECT退回）';
COMMENT ON COLUMN flow_skip.skip_condition IS '跳转条件';
COMMENT ON COLUMN flow_skip.coordinate IS '坐标';
COMMENT ON COLUMN flow_skip.create_time IS '创建时间';
COMMENT ON COLUMN flow_skip.update_time IS '更新时间';
COMMENT ON COLUMN flow_skip.del_flag IS '删除标志';
COMMENT ON COLUMN flow_skip.tenant_id IS '租户id';

CREATE TABLE flow_instance (
	id int8 NOT NULL, -- 主键id
	definition_id int8 NOT NULL, -- 对应flow_definition表的id
	business_id varchar(40) NOT NULL, -- 业务id
	node_type int2 NOT NULL, -- 结点类型（0开始节点 1中间节点 2结束节点 3互斥网关 4并行网关）
	node_code varchar(40) NOT NULL, -- 流程节点编码
	node_name varchar(100) NULL, -- 流程节点名称
	variable text NULL, -- 任务变量
	flow_status varchar(20) NOT NULL, -- 流程状态（0待提交 1审批中 2 审批通过 8已完成 9已退回 10失效）
    activity_status int2 NOT NULL DEFAULT 1, -- 流程激活状态（0挂起 1激活）
	create_by varchar(64) NULL DEFAULT ''::character varying, -- 创建者
	create_time timestamp NULL, -- 创建时间
	update_time timestamp NULL, -- 更新时间
	ext varchar(500) NULL, -- 扩展字段，预留给业务系统使用
	del_flag bpchar(1) NULL DEFAULT '0'::character varying, -- 删除标志
	tenant_id varchar(40) NULL, -- 租户id
	CONSTRAINT flow_instance_pkey PRIMARY KEY (id)
);
COMMENT ON TABLE flow_instance IS '流程实例表';

COMMENT ON COLUMN flow_instance.id IS '主键id';
COMMENT ON COLUMN flow_instance.definition_id IS '对应flow_definition表的id';
COMMENT ON COLUMN flow_instance.business_id IS '业务id';
COMMENT ON COLUMN flow_instance.node_type IS '结点类型（0开始节点 1中间节点 2结束节点 3互斥网关 4并行网关）';
COMMENT ON COLUMN flow_instance.node_code IS '流程节点编码';
COMMENT ON COLUMN flow_instance.node_name IS '流程节点名称';
COMMENT ON COLUMN flow_instance.variable IS '任务变量';
COMMENT ON COLUMN flow_instance.flow_status IS '流程状态（0待提交 1审批中 2 审批通过 3自动通过 8已完成 9已退回 10失效）';
COMMENT ON COLUMN flow_instance.activity_status IS '流程激活状态（0挂起 1激活）';
COMMENT ON COLUMN flow_instance.create_by IS '创建者';
COMMENT ON COLUMN flow_instance.create_time IS '创建时间';
COMMENT ON COLUMN flow_instance.update_time IS '更新时间';
COMMENT ON COLUMN flow_instance.ext IS '扩展字段，预留给业务系统使用';
COMMENT ON COLUMN flow_instance.del_flag IS '删除标志';
COMMENT ON COLUMN flow_instance.tenant_id IS '租户id';

CREATE TABLE flow_task (
	id int8 NOT NULL, -- 主键id
	definition_id int8 NOT NULL, -- 对应flow_definition表的id
	instance_id int8 NOT NULL, -- 对应flow_instance表的id
	node_code varchar(100) NOT NULL, -- 节点编码
	node_name varchar(100) NULL, -- 节点名称
	node_type int2 NOT NULL, -- 节点类型（0开始节点 1中间节点 2结束节点 3互斥网关 4并行网关）
    form_custom bpchar(1) NULL DEFAULT 'N'::character varying, -- 审批表单是否自定义（Y是 N否）
    form_path varchar(100) NULL, -- 审批表单路径
	create_time timestamp NULL, -- 创建时间
	update_time timestamp NULL, -- 更新时间
	del_flag bpchar(1) NULL DEFAULT '0'::character varying, -- 删除标志
	tenant_id varchar(40) NULL, -- 租户id
	CONSTRAINT flow_task_pkey PRIMARY KEY (id)
);
COMMENT ON TABLE flow_task IS '待办任务表';

COMMENT ON COLUMN flow_task.id IS '主键id';
COMMENT ON COLUMN flow_task.definition_id IS '对应flow_definition表的id';
COMMENT ON COLUMN flow_task.instance_id IS '对应flow_instance表的id';
COMMENT ON COLUMN flow_task.node_code IS '节点编码';
COMMENT ON COLUMN flow_task.node_name IS '节点名称';
COMMENT ON COLUMN flow_task.node_type IS '节点类型（0开始节点 1中间节点 2结束节点 3互斥网关 4并行网关）';
COMMENT ON COLUMN flow_task.form_custom IS '审批表单是否自定义（Y是 N否）';
COMMENT ON COLUMN flow_task.form_path IS '审批表单路径';
COMMENT ON COLUMN flow_task.create_time IS '创建时间';
COMMENT ON COLUMN flow_task.update_time IS '更新时间';
COMMENT ON COLUMN flow_task.del_flag IS '删除标志';
COMMENT ON COLUMN flow_task.tenant_id IS '租户id';

CREATE TABLE flow_his_task (
    id int8 NOT NULL, -- 主键id
    definition_id int8 NOT NULL, -- 对应flow_definition表的id
    instance_id int8 NOT NULL, -- 对应flow_instance表的id
    task_id int8 NOT NULL, -- 对应flow_task表的id
    node_code varchar(100) NULL, -- 开始节点编码
    node_name varchar(100) NULL, -- 开始节点名称
    node_type int2 NULL, -- 开始节点类型（0开始节点 1中间节点 2结束节点 3互斥网关 4并行网关）
    target_node_code varchar(100) NULL, -- 目标节点编码
    target_node_name varchar(100) NULL, -- 结束节点名称
    approver varchar(40) NULL, -- 审批者
    cooperate_type int2 NOT NULL DEFAULT 0, -- 协作方式(1审批 2转办 3委派 4会签 5票签 6加签 7减签)
    collaborator varchar(40) NULL, -- 协作人(只有转办、会签、票签、委派)
    skip_type varchar(10) NULL, -- 流转类型（PASS通过 REJECT退回 NONE无动作）
    flow_status varchar(20) NOT NULL, -- 流程状态（0待提交 1审批中 2 审批通过 8已完成 9已退回 10失效）
    form_custom bpchar(1) NULL DEFAULT 'N'::character varying, -- 审批表单是否自定义（Y是 N否）
    form_path varchar(100) NULL, -- 审批表单路径
    ext varchar(500) NULL, -- 扩展字段，预留给业务系统使用
    message varchar(500) NULL, -- 审批意见
    create_time timestamp NULL, -- 创建时间
    update_time timestamp NULL, -- 更新时间
    del_flag bpchar(1) NULL DEFAULT '0'::character varying, -- 删除标志
    tenant_id varchar(40) NULL, -- 租户id
    CONSTRAINT flow_his_task_pkey PRIMARY KEY (id)
);
COMMENT ON TABLE flow_his_task IS '历史任务记录表';

COMMENT ON COLUMN flow_his_task.id IS '主键id';
COMMENT ON COLUMN flow_his_task.definition_id IS '对应flow_definition表的id';
COMMENT ON COLUMN flow_his_task.instance_id IS '对应flow_instance表的id';
COMMENT ON COLUMN flow_his_task.task_id IS '对应flow_task表的id';
COMMENT ON COLUMN flow_his_task.node_code IS '开始节点编码';
COMMENT ON COLUMN flow_his_task.node_name IS '开始节点名称';
COMMENT ON COLUMN flow_his_task.node_type IS '开始节点类型（0开始节点 1中间节点 2结束节点 3互斥网关 4并行网关）';
COMMENT ON COLUMN flow_his_task.target_node_code IS '目标节点编码';
COMMENT ON COLUMN flow_his_task.target_node_name IS '结束节点名称';
COMMENT ON COLUMN flow_his_task.approver IS '审批者';
COMMENT ON COLUMN flow_his_task.cooperate_type IS '协作方式(1审批 2转办 3委派 4会签 5票签 6加签 7减签)';
COMMENT ON COLUMN flow_his_task.collaborator IS '协作人';
COMMENT ON COLUMN flow_his_task.skip_type IS '流转类型（PASS通过 REJECT退回 NONE无动作）';
COMMENT ON COLUMN flow_his_task.flow_status IS '流程状态（1审批中 2 审批通过 9已退回 10失效）';
COMMENT ON COLUMN flow_his_task.form_custom IS '审批表单是否自定义（Y是 N否）';
COMMENT ON COLUMN flow_his_task.form_path IS '审批表单路径';
COMMENT ON COLUMN flow_his_task.message IS '审批意见';
COMMENT ON COLUMN flow_his_task.ext IS '扩展字段，预留给业务系统使用';
COMMENT ON COLUMN flow_his_task.create_time IS '任务开始时间';
COMMENT ON COLUMN flow_his_task.update_time IS '审批完成时间';
COMMENT ON COLUMN flow_his_task.del_flag IS '删除标志';
COMMENT ON COLUMN flow_his_task.tenant_id IS '租户id';

CREATE TABLE flow_user (
	id int8 NOT NULL, -- 主键id
	"type" bpchar(1) NOT NULL, -- 人员类型（1待办任务的审批人权限 2待办任务的转办人权限 3流程实例的抄送人权限 4待办任务的委托人权限）
	processed_by varchar(80) NULL, -- 权限人
	associated int8 NOT NULL, -- 任务表id
	create_time timestamp NULL, -- 创建时间
	create_by varchar(80) NULL, -- 创建人
	update_time timestamp NULL, -- 更新时间
	del_flag bpchar(1) NULL DEFAULT '0'::character varying, -- 删除标志
	tenant_id varchar(40) NULL, -- 租户id
	CONSTRAINT flow_user_pk PRIMARY KEY (id)
);
CREATE INDEX user_processed_type ON flow_user USING btree (processed_by, type);
COMMENT ON TABLE flow_user IS '流程用户表';

COMMENT ON COLUMN flow_user.id IS '主键id';
COMMENT ON COLUMN flow_user."type" IS '人员类型（1待办任务的审批人权限 2待办任务的转办人权限 3待办任务的委托人权限）';
COMMENT ON COLUMN flow_user.processed_by IS '权限人';
COMMENT ON COLUMN flow_user.associated IS '任务表id';
COMMENT ON COLUMN flow_user.create_time IS '创建时间';
COMMENT ON COLUMN flow_user.create_by IS '创建人';
COMMENT ON COLUMN flow_user.update_time IS '更新时间';
COMMENT ON COLUMN flow_user.del_flag IS '删除标志';
COMMENT ON COLUMN flow_user.tenant_id IS '租户id';
