# 流程实例和流程历史表修改流程状态字段类型为字符串类型-varchar
ALTER TABLE flow_instance ALTER COLUMN flow_status SET DATA TYPE varchar(2);
ALTER TABLE flow_his_task ALTER COLUMN flow_status SET DATA TYPE varchar(2);