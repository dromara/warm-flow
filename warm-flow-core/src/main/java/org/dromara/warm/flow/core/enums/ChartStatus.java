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
import org.dromara.warm.flow.core.utils.CollUtil;
import org.dromara.warm.flow.core.utils.ObjectUtil;
import org.dromara.warm.flow.core.utils.StringUtils;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    DONE(2, "已办理", new Color(157, 255, 0));

    private final Integer key;
    private final String value;
    private final Color color;

    private static final Map<Integer, Color> CUSTOM_COLOR = new HashMap<>();


    public static void initCustomColor(List<String> chartStatusColor) {
        if (CollUtil.isNotEmpty(chartStatusColor) && chartStatusColor.size() == 3) {
            for (int i = 0; i < chartStatusColor.size(); i++) {
                String statusColor = chartStatusColor.get(i);
                if (StringUtils.isNotEmpty(statusColor)) {
                    String[] colorArr = statusColor.split(",");
                    if (colorArr.length == 3) {
                        ChartStatus.CUSTOM_COLOR.put(i, new Color(Integer.parseInt(colorArr[0]), Integer.parseInt(colorArr[1]), Integer.parseInt(colorArr[2])));
                    }
                }
            }
        }
    }

    public static Color getNotDone() {
        return getColorByKey(ChartStatus.NOT_DONE);
    }

    public static Color getToDo() {
        return getColorByKey(ChartStatus.TO_DO);
    }

    public static Color getDone() {
        return getColorByKey(ChartStatus.DONE);
    }

    public static Color getColorByKey(ChartStatus chartStatus) {
        Color color = ChartStatus.CUSTOM_COLOR.get(chartStatus.getKey());
        return ObjectUtil.defaultNull(color, chartStatus.getColor());
    }

    public static Color getColorByKey(Integer key) {
        for (ChartStatus item : ChartStatus.values()) {
            if (item.getKey().equals(key)) {
                Color color = ChartStatus.CUSTOM_COLOR.get(key);
                return ObjectUtil.defaultNull(color, item.getColor());
            }
        }
        return null;
    }

    /**
     * 判断是否未办理
     *
     * @param key 状态
     */
    public static Boolean isNotDone(Integer key) {
        return ObjectUtil.isNotNull(key) && (ChartStatus.NOT_DONE.getKey().equals(key));
    }

    /**
     * 判断是否待办理
     *
     * @param key 状态
     */
    public static Boolean isToDo(Integer key) {
        return ObjectUtil.isNotNull(key) && (ChartStatus.TO_DO.getKey().equals(key));
    }

    /**
     * 判断是否已办理
     *
     * @param key 状态
     */
    public static Boolean isDone(Integer key) {
        return ObjectUtil.isNotNull(key) && (ChartStatus.DONE.getKey().equals(key));
    }

}
