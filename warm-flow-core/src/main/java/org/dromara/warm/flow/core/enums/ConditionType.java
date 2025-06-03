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
package org.dromara.warm.flow.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 条件表达式类型
 *
 * @author xiarg
 * @since 2025/06/03 17:57:05
 */
@Getter
@AllArgsConstructor
public enum ConditionType {

    /**
     * 等于
     */
    EQ("eq","等于"),
    /**
     * 大于等于
     */
    GE("ge","大于等于"),
    /**
     * 大于
     */
    GT("gt","大于"),
    /**
     * 小于等于
     */
    LE("le","小于等于"),
    /**
     * 包含
     */
    LIKE("like","包含"),
    /**
     * 小于
     */
    LT("lt","小于"),
    /**
     * 不等于
     */
    NE("ne","不等于"),
    /**
     * 不包含
     */
    NOT_NIKE("notNike","不包含");

    /**
     * 表达式类型
     */
    private final String key;
    /**
     * 表达式描述
     */
    private final String value;

    public static String getKeyByValue(String value) {
        for (ConditionType item : ConditionType.values()) {
            if (item.getValue().equals(value)) {
                return item.getKey();
            }
        }
        return null;
    }

    public static String getValueByKey(String key) {
        for (ConditionType item : ConditionType.values()) {
            if (item.getKey().equals(key)) {
                return item.getValue();
            }
        }
        return null;
    }

    public static ConditionType getByKey(String key) {
        for (ConditionType item : ConditionType.values()) {
            if (item.getKey().equals(key)) {
                return item;
            }
        }
        return null;
    }
}
