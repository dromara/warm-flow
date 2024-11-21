update flow_node set permission_flag =  REPLACE(permission_flag,'@@default@@|','');
update flow_node set permission_flag =  REPLACE(permission_flag,'@@spel@@|','');
