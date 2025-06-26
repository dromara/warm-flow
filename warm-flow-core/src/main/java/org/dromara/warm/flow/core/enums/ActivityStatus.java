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

/**
 * 激活状态
 *
 * @author warm
 * @since 2025/6/25
 */
@Getter
@AllArgsConstructor
public enum ActivityStatus {

    SUSPENDED(0,"挂起"),

    ACTIVITY(1,"激活");

    private final Integer key;
    private final String value;

    /**
     * 判断流程是否激活
     */
    public static Boolean isActivity(Integer key) {
        return ObjectUtil.isNotNull(key) && (ActivityStatus.ACTIVITY.getKey().equals(key));
    }

    /**
     * 判断流程是否挂起
     */
    public static Boolean isSuspended(Integer key) {
        return ObjectUtil.isNotNull(key) && (ActivityStatus.SUSPENDED.getKey().equals(key));
    }
}
