package org.dromara.warm.flow.core.orm.dao;

import org.dromara.warm.flow.core.entity.Form;

import java.util.List;

/**
 * 流程表单Dao接口，不同的orm扩展包实现它
 *
 * @author vanlin
 * @className FlowFormDao
 * @description
 * @since 2024/8/19 10:24
 */
public interface FlowFormDao<T extends Form> extends WarmDao<T> {
    List<T> queryByCodeList(List<String> formCodeList);
}
