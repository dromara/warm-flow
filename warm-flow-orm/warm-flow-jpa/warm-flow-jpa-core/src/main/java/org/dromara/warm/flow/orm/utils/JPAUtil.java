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
package org.dromara.warm.flow.orm.utils;

import org.dromara.warm.flow.core.entity.RootEntity;

import javax.persistence.Column;
import javax.persistence.Transient;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Objects;

/**
 * @author vanlin
 * @className JPAUtil
 * @description
 * @since 2024/5/11 21:34
 */
public class JPAUtil {

    /**
     * column 到 field 映射关系,兼容 order by 处理逻辑
     */
    public static <T extends RootEntity> void initMapping(Class<T> entityClass
            , HashMap<String, String> mapping) {
        Field[] fields = entityClass.getDeclaredFields();
        for (Field field : fields) {
            Transient transientAnnotation = field.getAnnotation(Transient.class);
            if (Objects.nonNull(transientAnnotation)) {
                continue;
            }
            Column column = field.getAnnotation(Column.class);
            if (Objects.nonNull(column)) {
                mapping.put(column.name(), field.getName());
            } else {
                mapping.put(field.getName(), field.getName());
            }
        }
    }

}
