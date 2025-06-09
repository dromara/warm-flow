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
