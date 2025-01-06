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

import java.awt.*;

/**
 * 流程图状态
 *
 * @author warm
 * @since 2023/3/31 12:16
 */
@Getter
@AllArgsConstructor
public enum ChartStatus {
    NOT_DONE(0, "未办理", new Color(0, 0, 0)),

    TO_DO(1, "待办理", new Color(255, 200, 0)),

    DONE(2, "已办理", new Color(157, 255, 0)),

    REJECT(3, "退回", new Color(255, 110, 0));

    private final Integer key;
    private final String value;
    private final Color color;

    public static Integer getKeyByValue(String value) {
        for (ChartStatus item : ChartStatus.values()) {
            if (item.getValue().equals(value)) {
                return item.getKey();
            }
        }
        return null;
    }

    public static Color getColorByKey(Integer key) {
        for (ChartStatus item : ChartStatus.values()) {
            if (item.getKey().equals(key)) {
                return item.getColor();
            }
        }
        return null;
    }

    public static ChartStatus getByKey(Integer key) {
        for (ChartStatus item : ChartStatus.values()) {
            if (item.getKey().equals(key)) {
                return item;
            }
        }
        return null;
    }

}
