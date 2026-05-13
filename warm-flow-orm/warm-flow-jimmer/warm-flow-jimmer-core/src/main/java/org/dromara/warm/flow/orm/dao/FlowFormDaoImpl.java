/*
 *    Copyright 2024-2025, Warm-Flow (290631660@qq.com).
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.dromara.warm.flow.orm.dao;

import org.babyfish.jimmer.sql.ast.Predicate;
import org.babyfish.jimmer.sql.ast.table.Table;
import org.babyfish.jimmer.sql.ast.table.spi.TableProxy;
import org.dromara.warm.flow.core.orm.dao.FlowFormDao;
import org.dromara.warm.flow.core.utils.CollUtil;
import org.dromara.warm.flow.orm.entity.FlowForm;
import org.dromara.warm.flow.orm.model.FlowFormModel;
import org.dromara.warm.flow.orm.model.FlowFormModelTable;

import java.util.ArrayList;
import java.util.List;

public class FlowFormDaoImpl extends WarmDaoImpl<FlowForm, FlowFormModel> implements FlowFormDao<FlowForm> {

    private static final FlowFormModelTable TABLE = FlowFormModelTable.$;

    public FlowFormDaoImpl() {
        super(FlowFormModel.class);
    }

    @Override
    public FlowForm newEntity() {
        return new FlowForm();
    }

    @Override
    protected TableProxy<FlowFormModel> table() {
        return TABLE;
    }

    @Override
    protected List<Predicate> predicates(FlowForm entity, Table<FlowFormModel> table) {
        List<Predicate> predicates = basePredicates(entity, table);
        eqIfNotEmpty(predicates, table, "formCode", entity.getFormCode());
        eqIfNotEmpty(predicates, table, "formName", entity.getFormName());
        eqIfNotEmpty(predicates, table, "version", entity.getVersion());
        eq(predicates, table, "publishStatus", entity.getIsPublish());
        eq(predicates, table, "formType", entity.getFormType());
        eqIfNotEmpty(predicates, table, "formPath", entity.getFormPath());
        eqIfNotEmpty(predicates, table, "formContent", entity.getFormContent());
        eqIfNotEmpty(predicates, table, "ext", entity.getExt());
        return predicates;
    }

    @Override
    public List<FlowForm> queryByCodeList(List<String> formCodeList) {
        if (CollUtil.isEmpty(formCodeList)) {
            return new ArrayList<>();
        }
        return fromModels(sqlClient().createQuery(TABLE)
            .where(scopedPredicates(TABLE.formCode().in(formCodeList)).toArray(new Predicate[0]))
            .select(TABLE)
            .execute());
    }
}
