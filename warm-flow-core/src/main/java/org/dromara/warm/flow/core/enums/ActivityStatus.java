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
import org.dromara.warm.flow.core.utils.ObjectUtil;

@Getter
@AllArgsConstructor
public enum ActivityStatus {

    SUSPENDED(0,"挂起"),

    ACTIVITY(1,"激活");

    private final Integer key;
    private final String value;

    public static Integer getKeyByValue(String value) {
        for (ActivityStatus item : ActivityStatus.values()) {
            if (item.getValue().equals(value)) {
                return item.getKey();
            }
        }
        return null;
    }

    public static String getValueByKey(Integer Key) {
        for (ActivityStatus item : ActivityStatus.values()) {
            if (item.getKey().equals(Key)) {
                return item.getValue();
            }
        }
        return null;
    }

    public static ActivityStatus getByKey(Integer key) {
        for (ActivityStatus item : ActivityStatus.values()) {
            if (item.getKey().equals(key)) {
                return item;
            }
        }
        return null;
    }

    /**
     * 判断流程是否激活
     * @param Key
     * @return
     */
    public static Boolean isActivity(Integer Key) {
        return ObjectUtil.isNotNull(Key) && (ActivityStatus.ACTIVITY.getKey().equals(Key));
    }
}
