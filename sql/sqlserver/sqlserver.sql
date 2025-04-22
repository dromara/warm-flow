CREATE TABLE flow_definition (
    id bigint NOT NULL,
    flow_code nvarchar(40) NOT NULL,
    flow_name nvarchar(100) NOT NULL,
    category nvarchar(100) NULL,
    version nvarchar(20) NOT NULL,
    is_publish tinyint DEFAULT('0') NULL,
    form_custom nchar(1) DEFAULT('N') NULL,
    form_path nvarchar(100) NULL,
    activity_status tinyint DEFAULT('1') NULL,
    listener_type nvarchar(100) NULL,
    listener_path nvarchar(400) NULL,
    ext nvarchar(500) NULL,
    create_time datetime2(7)  NULL,
    update_time datetime2(7)  NULL,
    del_flag nchar(1) DEFAULT('0') NULL,
    tenant_id nvarchar(40) NULL,
    CONSTRAINT PK__flow_def__3213E83FEE39AE33 PRIMARY KEY CLUSTERED (id)
    WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
    ON [PRIMARY]
)
ON [PRIMARY]
GO

EXEC sp_addextendedproperty
'MS_Description', N'主键id',
'SCHEMA', N'dbo',
'TABLE', N'flow_definition',
'COLUMN', N'id'
GO

EXEC sp_addextendedproperty
'MS_Description', N'流程编码',
'SCHEMA', N'dbo',
'TABLE', N'flow_definition',
'COLUMN', N'flow_code'
GO

EXEC sp_addextendedproperty
'MS_Description', N'流程名称',
'SCHEMA', N'dbo',
'TABLE', N'flow_definition',
'COLUMN', N'flow_name'
GO

EXEC sp_addextendedproperty
'MS_Description', N'流程类别',
'SCHEMA', N'dbo',
'TABLE', N'flow_definition',
'COLUMN', N'category'
GO

EXEC sp_addextendedproperty
'MS_Description', N'流程版本',
'SCHEMA', N'dbo',
'TABLE', N'flow_definition',
'COLUMN', N'version'
GO

EXEC sp_addextendedproperty
'MS_Description', N'是否发布（0未发布 1已发布 9失效）',
'SCHEMA', N'dbo',
'TABLE', N'flow_definition',
'COLUMN', N'is_publish'
GO

EXEC sp_addextendedproperty
'MS_Description', N'审批表单是否自定义（Y是 N否）',
'SCHEMA', N'dbo',
'TABLE', N'flow_definition',
'COLUMN', N'form_custom'
GO

EXEC sp_addextendedproperty
'MS_Description', N'审批表单路径',
'SCHEMA', N'dbo',
'TABLE', N'flow_definition',
'COLUMN', N'form_path'
GO

EXEC sp_addextendedproperty
'MS_Description', N'流程激活状态（0挂起 1激活）',
'SCHEMA', N'dbo',
'TABLE', N'flow_definition',
'COLUMN', N'activity_status'
GO

EXEC sp_addextendedproperty
'MS_Description', N'监听器类型',
'SCHEMA', N'dbo',
'TABLE', N'flow_definition',
'COLUMN', N'listener_type'
GO

EXEC sp_addextendedproperty
'MS_Description', N'监听器路径',
'SCHEMA', N'dbo',
'TABLE', N'flow_definition',
'COLUMN', N'listener_path'
GO

EXEC sp_addextendedproperty
'MS_Description', N'业务详情 存业务表对象json字符串',
'SCHEMA', N'dbo',
'TABLE', N'flow_definition',
'COLUMN', N'ext'
GO

EXEC sp_addextendedproperty
'MS_Description', N'创建时间',
'SCHEMA', N'dbo',
'TABLE', N'flow_definition',
'COLUMN', N'create_time'
GO

EXEC sp_addextendedproperty
'MS_Description', N'更新时间',
'SCHEMA', N'dbo',
'TABLE', N'flow_definition',
'COLUMN', N'update_time'
GO

EXEC sp_addextendedproperty
'MS_Description', N'删除标志',
'SCHEMA', N'dbo',
'TABLE', N'flow_definition',
'COLUMN', N'del_flag'
GO

EXEC sp_addextendedproperty
'MS_Description', N'租户id',
'SCHEMA', N'dbo',
'TABLE', N'flow_definition',
'COLUMN', N'tenant_id'
GO

EXEC sp_addextendedproperty
'MS_Description', N'流程定义表',
'SCHEMA', N'dbo',
'TABLE', N'flow_definition'
GO

CREATE TABLE flow_node (
    id bigint NOT NULL,
    node_type tinyint NOT NULL,
    definition_id bigint NOT NULL,
    node_code nvarchar(100) NOT NULL,
    node_name nvarchar(100) NULL,
    permission_flag nvarchar(200) NULL,
    node_ratio decimal(6,3)  NULL,
    coordinate nvarchar(100) NULL,
    any_node_skip nvarchar(100) NULL,
    listener_type nvarchar(100) NULL,
    listener_path nvarchar(400) NULL,
    handler_type nvarchar(100) NULL,
    handler_path nvarchar(400) NULL,
    form_custom nchar(1) DEFAULT('N') NULL,
    form_path nvarchar(100) NULL,
    version nvarchar(20) NOT NULL,
    create_time datetime2(7)  NULL,
    update_time datetime2(7)  NULL,
    ext nvarchar(max) NULL,
    del_flag nchar(1) DEFAULT('0') NULL,
    tenant_id nvarchar(40) NULL,
    CONSTRAINT PK__flow_nod__3213E83F372470DE PRIMARY KEY CLUSTERED (id)
    WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
    ON [PRIMARY]
)
ON [PRIMARY]
GO

EXEC sp_addextendedproperty
'MS_Description', N'主键id',
'SCHEMA', N'dbo',
'TABLE', N'flow_node',
'COLUMN', N'id'
GO

EXEC sp_addextendedproperty
'MS_Description', N'节点类型（0开始节点 1中间节点 2结束节点 3互斥网关 4并行网关）',
'SCHEMA', N'dbo',
'TABLE', N'flow_node',
'COLUMN', N'node_type'
GO

EXEC sp_addextendedproperty
'MS_Description', N'流程定义id',
'SCHEMA', N'dbo',
'TABLE', N'flow_node',
'COLUMN', N'definition_id'
GO

EXEC sp_addextendedproperty
'MS_Description', N'流程节点编码',
'SCHEMA', N'dbo',
'TABLE', N'flow_node',
'COLUMN', N'node_code'
GO

EXEC sp_addextendedproperty
'MS_Description', N'流程节点名称',
'SCHEMA', N'dbo',
'TABLE', N'flow_node',
'COLUMN', N'node_name'
GO

EXEC sp_addextendedproperty
'MS_Description', N'权限标识（权限类型:权限标识，可以多个，用逗号隔开)',
'SCHEMA', N'dbo',
'TABLE', N'flow_node',
'COLUMN', N'permission_flag'
GO

EXEC sp_addextendedproperty
'MS_Description', N'流程签署比例值',
'SCHEMA', N'dbo',
'TABLE', N'flow_node',
'COLUMN', N'node_ratio'
GO

EXEC sp_addextendedproperty
'MS_Description', N'坐标',
'SCHEMA', N'dbo',
'TABLE', N'flow_node',
'COLUMN', N'coordinate'
GO

EXEC sp_addextendedproperty
'MS_Description', N'任意结点跳转',
'SCHEMA', N'dbo',
'TABLE', N'flow_node',
'COLUMN', N'any_node_skip'
GO

EXEC sp_addextendedproperty
'MS_Description', N'监听器类型',
'SCHEMA', N'dbo',
'TABLE', N'flow_node',
'COLUMN', N'listener_type'
GO

EXEC sp_addextendedproperty
'MS_Description', N'监听器路径',
'SCHEMA', N'dbo',
'TABLE', N'flow_node',
'COLUMN', N'listener_path'
GO

EXEC sp_addextendedproperty
'MS_Description', N'处理器类型',
'SCHEMA', N'dbo',
'TABLE', N'flow_node',
'COLUMN', N'handler_type'
GO

EXEC sp_addextendedproperty
'MS_Description', N'处理器路径',
'SCHEMA', N'dbo',
'TABLE', N'flow_node',
'COLUMN', N'handler_path'
GO

EXEC sp_addextendedproperty
'MS_Description', N'审批表单是否自定义（Y是 N否）',
'SCHEMA', N'dbo',
'TABLE', N'flow_node',
'COLUMN', N'form_custom'
GO

EXEC sp_addextendedproperty
'MS_Description', N'审批表单路径',
'SCHEMA', N'dbo',
'TABLE', N'flow_node',
'COLUMN', N'form_path'
GO

EXEC sp_addextendedproperty
'MS_Description', N'版本',
'SCHEMA', N'dbo',
'TABLE', N'flow_node',
'COLUMN', N'version'
GO

EXEC sp_addextendedproperty
'MS_Description', N'创建时间',
'SCHEMA', N'dbo',
'TABLE', N'flow_node',
'COLUMN', N'create_time'
GO

EXEC sp_addextendedproperty
'MS_Description', N'更新时间',
'SCHEMA', N'dbo',
'TABLE', N'flow_node',
'COLUMN', N'update_time'
GO

EXEC sp_addextendedproperty
'MS_Description', N'扩展属性',
'SCHEMA', N'dbo',
'TABLE', N'flow_instance',
'COLUMN', N'ext'
GO


EXEC sp_addextendedproperty
'MS_Description', N'删除标志',
'SCHEMA', N'dbo',
'TABLE', N'flow_node',
'COLUMN', N'del_flag'
GO

EXEC sp_addextendedproperty
'MS_Description', N'租户id',
'SCHEMA', N'dbo',
'TABLE', N'flow_node',
'COLUMN', N'tenant_id'
GO

EXEC sp_addextendedproperty
'MS_Description', N'流程节点表',
'SCHEMA', N'dbo',
'TABLE', N'flow_node'
GO

CREATE TABLE flow_skip (
    id bigint NOT NULL,
    definition_id bigint NOT NULL,
    now_node_code nvarchar(100) NOT NULL,
    now_node_type tinyint  NULL,
    next_node_code nvarchar(100) NOT NULL,
    next_node_type tinyint  NULL,
    skip_name nvarchar(100) NULL,
    skip_type nvarchar(40) NULL,
    skip_condition nvarchar(200) NULL,
    coordinate nvarchar(100) NULL,
    create_time datetime2(7)  NULL,
    update_time datetime2(7)  NULL,
    del_flag nchar(1) DEFAULT('0') NULL,
    tenant_id nvarchar(40) NULL,
    CONSTRAINT PK__flow_ski__3213E83F073FEE6E PRIMARY KEY CLUSTERED (id)
    WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
    ON [PRIMARY]
)
ON [PRIMARY]
GO

EXEC sp_addextendedproperty
'MS_Description', N'主键id',
'SCHEMA', N'dbo',
'TABLE', N'flow_skip',
'COLUMN', N'id'
GO

EXEC sp_addextendedproperty
'MS_Description', N'流程定义id',
'SCHEMA', N'dbo',
'TABLE', N'flow_skip',
'COLUMN', N'definition_id'
GO

EXEC sp_addextendedproperty
'MS_Description', N'当前流程节点的编码',
'SCHEMA', N'dbo',
'TABLE', N'flow_skip',
'COLUMN', N'now_node_code'
GO

EXEC sp_addextendedproperty
'MS_Description', N'当前节点类型（0开始节点 1中间节点 2结束节点 3互斥网关 4并行网关）',
'SCHEMA', N'dbo',
'TABLE', N'flow_skip',
'COLUMN', N'now_node_type'
GO

EXEC sp_addextendedproperty
'MS_Description', N'下一个流程节点的编码',
'SCHEMA', N'dbo',
'TABLE', N'flow_skip',
'COLUMN', N'next_node_code'
GO

EXEC sp_addextendedproperty
'MS_Description', N'下一个节点类型（0开始节点 1中间节点 2结束节点 3互斥网关 4并行网关）',
'SCHEMA', N'dbo',
'TABLE', N'flow_skip',
'COLUMN', N'next_node_type'
GO

EXEC sp_addextendedproperty
'MS_Description', N'跳转名称',
'SCHEMA', N'dbo',
'TABLE', N'flow_skip',
'COLUMN', N'skip_name'
GO

EXEC sp_addextendedproperty
'MS_Description', N'跳转类型（PASS审批通过 REJECT退回）',
'SCHEMA', N'dbo',
'TABLE', N'flow_skip',
'COLUMN', N'skip_type'
GO

EXEC sp_addextendedproperty
'MS_Description', N'跳转条件',
'SCHEMA', N'dbo',
'TABLE', N'flow_skip',
'COLUMN', N'skip_condition'
GO

EXEC sp_addextendedproperty
'MS_Description', N'坐标',
'SCHEMA', N'dbo',
'TABLE', N'flow_skip',
'COLUMN', N'coordinate'
GO

EXEC sp_addextendedproperty
'MS_Description', N'创建时间',
'SCHEMA', N'dbo',
'TABLE', N'flow_skip',
'COLUMN', N'create_time'
GO

EXEC sp_addextendedproperty
'MS_Description', N'更新时间',
'SCHEMA', N'dbo',
'TABLE', N'flow_skip',
'COLUMN', N'update_time'
GO

EXEC sp_addextendedproperty
'MS_Description', N'删除标志',
'SCHEMA', N'dbo',
'TABLE', N'flow_skip',
'COLUMN', N'del_flag'
GO

EXEC sp_addextendedproperty
'MS_Description', N'租户id',
'SCHEMA', N'dbo',
'TABLE', N'flow_skip',
'COLUMN', N'tenant_id'
GO

EXEC sp_addextendedproperty
'MS_Description', N'节点跳转关联表',
'SCHEMA', N'dbo',
'TABLE', N'flow_skip'
GO

CREATE TABLE flow_instance (
    id bigint NOT NULL,
    definition_id bigint NOT NULL,
    business_id nvarchar(40) NOT NULL,
    node_type tinyint NOT NULL,
    node_code nvarchar(40) NOT NULL,
    node_name nvarchar(100) NULL,
    variable nvarchar(max) NULL,
    flow_status nvarchar(20) NOT NULL,
    activity_status tinyint DEFAULT('1') NULL,
    def_json nvarchar(max) NULL,
    create_by nvarchar(64) NULL,
    create_time datetime2(7)  NULL,
    update_time datetime2(7)  NULL,
    ext nvarchar(500) NULL,
    del_flag nchar(1) DEFAULT('0') NULL,
    tenant_id nvarchar(40) NULL,
    CONSTRAINT PK__flow_ins__3213E83F5190FEE1 PRIMARY KEY CLUSTERED (id)
    WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
    ON [PRIMARY]
)
ON [PRIMARY]
TEXTIMAGE_ON [PRIMARY]
GO

EXEC sp_addextendedproperty
'MS_Description', N'主键id',
'SCHEMA', N'dbo',
'TABLE', N'flow_instance',
'COLUMN', N'id'
GO

EXEC sp_addextendedproperty
'MS_Description', N'对应flow_definition表的id',
'SCHEMA', N'dbo',
'TABLE', N'flow_instance',
'COLUMN', N'definition_id'
GO

EXEC sp_addextendedproperty
'MS_Description', N'业务id',
'SCHEMA', N'dbo',
'TABLE', N'flow_instance',
'COLUMN', N'business_id'
GO

EXEC sp_addextendedproperty
'MS_Description', N'节点类型（0开始节点 1中间节点 2结束节点 3互斥网关 4并行网关）',
'SCHEMA', N'dbo',
'TABLE', N'flow_instance',
'COLUMN', N'node_type'
GO

EXEC sp_addextendedproperty
'MS_Description', N'流程节点编码',
'SCHEMA', N'dbo',
'TABLE', N'flow_instance',
'COLUMN', N'node_code'
GO

EXEC sp_addextendedproperty
'MS_Description', N'流程节点名称',
'SCHEMA', N'dbo',
'TABLE', N'flow_instance',
'COLUMN', N'node_name'
GO

EXEC sp_addextendedproperty
'MS_Description', N'任务变量',
'SCHEMA', N'dbo',
'TABLE', N'flow_instance',
'COLUMN', N'variable'
GO

EXEC sp_addextendedproperty
'MS_Description', N'流程状态（0待提交 1审批中 2审批通过 4终止 5作废 6撤销 8已完成 9已退回 10失效 11拿回）',
'SCHEMA', N'dbo',
'TABLE', N'flow_instance',
'COLUMN', N'flow_status'
GO

EXEC sp_addextendedproperty
'MS_Description', N'流程激活状态（0挂起 1激活）',
'SCHEMA', N'dbo',
'TABLE', N'flow_instance',
'COLUMN', N'activity_status'
GO

EXEC sp_addextendedproperty
'MS_Description', N'流程定义json',
'SCHEMA', N'dbo',
'TABLE', N'flow_instance',
'COLUMN', N'def_json'
GO

EXEC sp_addextendedproperty
'MS_Description', N'创建者',
'SCHEMA', N'dbo',
'TABLE', N'flow_instance',
'COLUMN', N'create_by'
GO

EXEC sp_addextendedproperty
'MS_Description', N'创建时间',
'SCHEMA', N'dbo',
'TABLE', N'flow_instance',
'COLUMN', N'create_time'
GO

EXEC sp_addextendedproperty
'MS_Description', N'更新时间',
'SCHEMA', N'dbo',
'TABLE', N'flow_instance',
'COLUMN', N'update_time'
GO

EXEC sp_addextendedproperty
'MS_Description', N'扩展字段，预留给业务系统使用',
'SCHEMA', N'dbo',
'TABLE', N'flow_instance',
'COLUMN', N'ext'
GO

EXEC sp_addextendedproperty
'MS_Description', N'删除标志',
'SCHEMA', N'dbo',
'TABLE', N'flow_instance',
'COLUMN', N'del_flag'
GO

EXEC sp_addextendedproperty
'MS_Description', N'租户id',
'SCHEMA', N'dbo',
'TABLE', N'flow_instance',
'COLUMN', N'tenant_id'
GO

EXEC sp_addextendedproperty
'MS_Description', N'流程实例表',
'SCHEMA', N'dbo',
'TABLE', N'flow_instance'
GO

CREATE TABLE flow_task (
    id bigint NOT NULL,
    definition_id bigint NOT NULL,
    instance_id bigint NOT NULL,
    node_code nvarchar(100) NOT NULL,
    node_name nvarchar(100) NULL,
    node_type tinyint NOT NULL,
    flow_status nvarchar(20) NOT NULL,
    form_custom nchar(1) DEFAULT('N') NULL,
    form_path nvarchar(100) NULL,
    create_time datetime2(7)  NULL,
    update_time datetime2(7)  NULL,
    del_flag nchar(1) DEFAULT('0') NULL,
    tenant_id nvarchar(40) NULL,
    CONSTRAINT PK__flow_tas__3213E83F5AE1F1BA PRIMARY KEY CLUSTERED (id)
    WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
    ON [PRIMARY]
)
ON [PRIMARY]
GO

EXEC sp_addextendedproperty
'MS_Description', N'主键id',
'SCHEMA', N'dbo',
'TABLE', N'flow_task',
'COLUMN', N'id'
GO

EXEC sp_addextendedproperty
'MS_Description', N'对应flow_definition表的id',
'SCHEMA', N'dbo',
'TABLE', N'flow_task',
'COLUMN', N'definition_id'
GO

EXEC sp_addextendedproperty
'MS_Description', N'对应flow_instance表的id',
'SCHEMA', N'dbo',
'TABLE', N'flow_task',
'COLUMN', N'instance_id'
GO

EXEC sp_addextendedproperty
'MS_Description', N'节点编码',
'SCHEMA', N'dbo',
'TABLE', N'flow_task',
'COLUMN', N'node_code'
GO

EXEC sp_addextendedproperty
'MS_Description', N'节点名称',
'SCHEMA', N'dbo',
'TABLE', N'flow_task',
'COLUMN', N'node_name'
GO

EXEC sp_addextendedproperty
'MS_Description', N'节点类型（0开始节点 1中间节点 2结束节点 3互斥网关 4并行网关）',
'SCHEMA', N'dbo',
'TABLE', N'flow_task',
'COLUMN', N'node_type'
GO

EXEC sp_addextendedproperty
'MS_Description', N'流程状态（0待提交 1审批中 2审批通过 4终止 5作废 6撤销 8已完成 9已退回 10失效 11拿回）',
'SCHEMA', N'dbo',
'TABLE', N'flow_task',
'COLUMN', N'flow_status'
GO

EXEC sp_addextendedproperty
'MS_Description', N'审批表单是否自定义（Y是 N否）',
'SCHEMA', N'dbo',
'TABLE', N'flow_task',
'COLUMN', N'form_custom'
GO

EXEC sp_addextendedproperty
'MS_Description', N'审批表单路径',
'SCHEMA', N'dbo',
'TABLE', N'flow_task',
'COLUMN', N'form_path'
GO

EXEC sp_addextendedproperty
'MS_Description', N'创建时间',
'SCHEMA', N'dbo',
'TABLE', N'flow_task',
'COLUMN', N'create_time'
GO

EXEC sp_addextendedproperty
'MS_Description', N'更新时间',
'SCHEMA', N'dbo',
'TABLE', N'flow_task',
'COLUMN', N'update_time'
GO

EXEC sp_addextendedproperty
'MS_Description', N'删除标志',
'SCHEMA', N'dbo',
'TABLE', N'flow_task',
'COLUMN', N'del_flag'
GO

EXEC sp_addextendedproperty
'MS_Description', N'租户id',
'SCHEMA', N'dbo',
'TABLE', N'flow_task',
'COLUMN', N'tenant_id'
GO

EXEC sp_addextendedproperty
'MS_Description', N'待办任务表',
'SCHEMA', N'dbo',
'TABLE', N'flow_task'
GO

CREATE TABLE flow_his_task (
    id bigint NOT NULL,
    definition_id bigint NOT NULL,
    instance_id bigint NOT NULL,
    task_id bigint NOT NULL,
    node_code nvarchar(200) NULL,
    node_name nvarchar(200) NULL,
    node_type tinyint  NULL,
    target_node_code nvarchar(100) NULL,
    target_node_name nvarchar(100) NULL,
    approver nvarchar(40) NULL,
    cooperate_type tinyint DEFAULT('0') NULL,
    collaborator nvarchar(40) NULL,
    skip_type nvarchar(10) NOT NULL,
    flow_status nvarchar(20) NOT NULL,
    form_custom nchar(1) DEFAULT('N') NULL,
    form_path nvarchar(100) NULL,
    message nvarchar(500) NULL,
    variable nvarchar(max) NULL,
    ext nvarchar(500) NULL,
    create_time datetime2(7)  NULL,
    update_time datetime2(7)  NULL,
    del_flag nchar(1) DEFAULT('0') NULL,
    tenant_id nvarchar(40) NULL,
    CONSTRAINT PK__flow_his__3213E83F67951564 PRIMARY KEY CLUSTERED (id)
    WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
    ON [PRIMARY]
)
ON [PRIMARY]
GO

EXEC sp_addextendedproperty
'MS_Description', N'主键id',
'SCHEMA', N'dbo',
'TABLE', N'flow_his_task',
'COLUMN', N'id'
GO

EXEC sp_addextendedproperty
'MS_Description', N'对应flow_definition表的id',
'SCHEMA', N'dbo',
'TABLE', N'flow_his_task',
'COLUMN', N'definition_id'
GO

EXEC sp_addextendedproperty
'MS_Description', N'对应flow_instance表的id',
'SCHEMA', N'dbo',
'TABLE', N'flow_his_task',
'COLUMN', N'instance_id'
GO

EXEC sp_addextendedproperty
'MS_Description', N'对应flow_task表的id',
'SCHEMA', N'dbo',
'TABLE', N'flow_his_task',
'COLUMN', N'task_id'
GO

EXEC sp_addextendedproperty
'MS_Description', N'开始节点编码',
'SCHEMA', N'dbo',
'TABLE', N'flow_his_task',
'COLUMN', N'node_code'
GO

EXEC sp_addextendedproperty
'MS_Description', N'开始节点名称',
'SCHEMA', N'dbo',
'TABLE', N'flow_his_task',
'COLUMN', N'node_name'
GO

EXEC sp_addextendedproperty
'MS_Description', N'开始节点类型（0开始节点 1中间节点 2结束节点 3互斥网关 4并行网关）',
'SCHEMA', N'dbo',
'TABLE', N'flow_his_task',
'COLUMN', N'node_type'
GO

EXEC sp_addextendedproperty
'MS_Description', N'目标节点编码',
'SCHEMA', N'dbo',
'TABLE', N'flow_his_task',
'COLUMN', N'target_node_code'
GO

EXEC sp_addextendedproperty
'MS_Description', N'结束节点名称',
'SCHEMA', N'dbo',
'TABLE', N'flow_his_task',
'COLUMN', N'target_node_name'
GO

EXEC sp_addextendedproperty
'MS_Description', N'审批者',
'SCHEMA', N'dbo',
'TABLE', N'flow_his_task',
'COLUMN', N'approver'
GO

EXEC sp_addextendedproperty
'MS_Description', N'协作方式(1审批 2转办 3委派 4会签 5票签 6加签 7减签)',
'SCHEMA', N'dbo',
'TABLE', N'flow_his_task',
'COLUMN', N'cooperate_type'
GO

EXEC sp_addextendedproperty
'MS_Description', N'协作人',
'SCHEMA', N'dbo',
'TABLE', N'flow_his_task',
'COLUMN', N'collaborator'
GO

EXEC sp_addextendedproperty
'MS_Description', N'流转类型（PASS通过 REJECT退回 NONE无动作）',
'SCHEMA', N'dbo',
'TABLE', N'flow_his_task',
'COLUMN', N'skip_type'
GO

EXEC sp_addextendedproperty
'MS_Description', N'流程状态（0待提交 1审批中 2审批通过 4终止 5作废 6撤销 8已完成 9已退回 10失效 11拿回）',
'SCHEMA', N'dbo',
'TABLE', N'flow_his_task',
'COLUMN', N'flow_status'
GO

EXEC sp_addextendedproperty
'MS_Description', N'审批表单是否自定义（Y是 N否）',
'SCHEMA', N'dbo',
'TABLE', N'flow_his_task',
'COLUMN', N'form_custom'
GO

EXEC sp_addextendedproperty
'MS_Description', N'审批表单路径',
'SCHEMA', N'dbo',
'TABLE', N'flow_his_task',
'COLUMN', N'form_path'
GO

EXEC sp_addextendedproperty
'MS_Description', N'审批意见',
'SCHEMA', N'dbo',
'TABLE', N'flow_his_task',
'COLUMN', N'message'
GO

EXEC sp_addextendedproperty
'MS_Description', N'任务变量',
'SCHEMA', N'dbo',
'TABLE', N'flow_his_task',
'COLUMN', N'variable'
GO

EXEC sp_addextendedproperty
'MS_Description', N'业务详情 存业务表对象json字符串',
'SCHEMA', N'dbo',
'TABLE', N'flow_his_task',
'COLUMN', N'ext'
GO

EXEC sp_addextendedproperty
'MS_Description', N'任务开始时间',
'SCHEMA', N'dbo',
'TABLE', N'flow_his_task',
'COLUMN', N'create_time'
GO

EXEC sp_addextendedproperty
'MS_Description', N'审批完成时间',
'SCHEMA', N'dbo',
'TABLE', N'flow_his_task',
'COLUMN', N'update_time'
GO

EXEC sp_addextendedproperty
'MS_Description', N'删除标志',
'SCHEMA', N'dbo',
'TABLE', N'flow_his_task',
'COLUMN', N'del_flag'
GO

EXEC sp_addextendedproperty
'MS_Description', N'租户id',
'SCHEMA', N'dbo',
'TABLE', N'flow_his_task',
'COLUMN', N'tenant_id'
GO

EXEC sp_addextendedproperty
'MS_Description', N'历史任务记录表',
'SCHEMA', N'dbo',
'TABLE', N'flow_his_task'
GO

CREATE TABLE flow_user (
    id bigint NOT NULL,
    type nchar(1) NOT NULL,
    processed_by nvarchar(80) NULL,
    associated bigint NOT NULL,
    create_time datetime2(7)  NULL,
    create_by nvarchar(80) NULL,
    update_time datetime2(7)  NULL,
    del_flag nchar(1) DEFAULT('0') NULL,
    tenant_id nvarchar(40) NULL,
    CONSTRAINT PK__flow_use__3213E83FFA38CA8B PRIMARY KEY CLUSTERED (id)
    WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
    ON [PRIMARY]
)
ON [PRIMARY]
GO

CREATE NONCLUSTERED INDEX user_processed_type ON flow_user (processed_by ASC, type ASC)
GO
CREATE NONCLUSTERED INDEX user_associated_idx ON flow_user (associated ASC)
GO

EXEC sp_addextendedproperty
'MS_Description', N'主键id',
'SCHEMA', N'dbo',
'TABLE', N'flow_user',
'COLUMN', N'id'
GO

EXEC sp_addextendedproperty
'MS_Description', N'人员类型（1待办任务的审批人权限 2待办任务的转办人权限 3待办任务的委托人权限）',
'SCHEMA', N'dbo',
'TABLE', N'flow_user',
'COLUMN', N'type'
GO

EXEC sp_addextendedproperty
'MS_Description', N'权限人',
'SCHEMA', N'dbo',
'TABLE', N'flow_user',
'COLUMN', N'processed_by'
GO

EXEC sp_addextendedproperty
'MS_Description', N'任务表id',
'SCHEMA', N'dbo',
'TABLE', N'flow_user',
'COLUMN', N'associated'
GO

EXEC sp_addextendedproperty
'MS_Description', N'创建时间',
'SCHEMA', N'dbo',
'TABLE', N'flow_user',
'COLUMN', N'create_time'
GO

EXEC sp_addextendedproperty
'MS_Description', N'创建人',
'SCHEMA', N'dbo',
'TABLE', N'flow_user',
'COLUMN', N'create_by'
GO

EXEC sp_addextendedproperty
'MS_Description', N'更新时间',
'SCHEMA', N'dbo',
'TABLE', N'flow_user',
'COLUMN', N'update_time'
GO

EXEC sp_addextendedproperty
'MS_Description', N'删除标志',
'SCHEMA', N'dbo',
'TABLE', N'flow_user',
'COLUMN', N'del_flag'
GO

EXEC sp_addextendedproperty
'MS_Description', N'租户id',
'SCHEMA', N'dbo',
'TABLE', N'flow_user',
'COLUMN', N'tenant_id'
GO

EXEC sp_addextendedproperty
'MS_Description', N'流程用户表',
'SCHEMA', N'dbo',
'TABLE', N'flow_user'
GO
