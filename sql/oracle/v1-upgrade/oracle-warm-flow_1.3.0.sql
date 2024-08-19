# 流程实例和流程历史表修改流程状态字段类型为字符串类型-varchar2
ALTER TABLE flow_instance MODIFY flow_status varchar2(2);
ALTER TABLE flow_his_task MODIFY flow_status varchar2(2);