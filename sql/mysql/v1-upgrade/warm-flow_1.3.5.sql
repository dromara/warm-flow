update flow_skip set skip_condition = REPLACE(skip_condition,'@@eq@@|','eq|');
update flow_skip set skip_condition = REPLACE(skip_condition,'@@eq@@','|');

update flow_skip set skip_condition = REPLACE(skip_condition,'@@ge@@|','ge|');
update flow_skip set skip_condition = REPLACE(skip_condition,'@@ge@@','|');

update flow_skip set skip_condition = REPLACE(skip_condition,'@@gt@@|','gt|');
update flow_skip set skip_condition = REPLACE(skip_condition,'@@gt@@','|');

update flow_skip set skip_condition = REPLACE(skip_condition,'@@eq@@|','eq|');
update flow_skip set skip_condition = REPLACE(skip_condition,'@@eq@@','|');

update flow_skip set skip_condition = REPLACE(skip_condition,'@@le@@|','le|');
update flow_skip set skip_condition = REPLACE(skip_condition,'@@le@@','|');

update flow_skip set skip_condition = REPLACE(skip_condition,'@@eq@@|','eq|');
update flow_skip set skip_condition = REPLACE(skip_condition,'@@eq@@','|');

update flow_skip set skip_condition = REPLACE(skip_condition,'@@like@@|','like|');
update flow_skip set skip_condition = REPLACE(skip_condition,'@@like@@','|');

update flow_skip set skip_condition = REPLACE(skip_condition,'@@lt@@|','lt|');
update flow_skip set skip_condition = REPLACE(skip_condition,'@@lt@@','|');

update flow_skip set skip_condition = REPLACE(skip_condition,'@@ne@@|','ne|');
update flow_skip set skip_condition = REPLACE(skip_condition,'@@ne@@','|');

update flow_skip set skip_condition = REPLACE(skip_condition,'@@notNike@@|','notNike|');
update flow_skip set skip_condition = REPLACE(skip_condition,'@@notNike@@','|');

update flow_skip set skip_condition = REPLACE(skip_condition,'@@spel@@|','spel|');
