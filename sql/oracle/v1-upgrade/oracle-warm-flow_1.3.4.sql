update flow_node set permission_flag =  REPLACE(permission_flag,'@@default@@|','');
update flow_node set permission_flag =  REPLACE(permission_flag,'@@spel@@|','');

UPDATE flow_node SET permission_flag = REPLACE(permission_flag, '@@default@@|', '');
UPDATE flow_node SET permission_flag = REPLACE(permission_flag, '@@spel@@|', '');
