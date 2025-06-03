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

import java.util.List;

/**
 * 下个任务处理人配置类型
 *
 * @author xiarg
 * @since 2025/06/03 15:32:03
 */
@Getter
@AllArgsConstructor
public enum NextHandlerConfigType {

    /**
     * 覆盖
     */
    COVER(1,"覆盖"),
    /**
     * 追加
     */
    APPEND(2,"追加");

    private final Integer key;
    private final String value;

    public static Integer getKeyByValue(String value) {
        for (NextHandlerConfigType item : NextHandlerConfigType.values()) {
            if (item.getValue().equals(value)) {
                return item.getKey();
            }
        }
        return null;
    }

    public static String getValueByKey(Integer key) {
        for (NextHandlerConfigType item : NextHandlerConfigType.values()) {
            if (item.getKey().equals(key)) {
                return item.getValue();
            }
        }
        return null;
    }

    public static NextHandlerConfigType getByKey(Integer key) {
        for (NextHandlerConfigType item : NextHandlerConfigType.values()) {
            if (item.getKey().equals(key)) {
                return item;
            }
        }
        return null;
    }

    /**
     * 下个任务处理人配置类型是否是覆盖
     *
     * @param type 下个任务处理人配置类型
     * @return 是否是覆盖
     * @author xiarg
     * @since 2025/06/03 15:33:38
     */
    public static Boolean isCover(NextHandlerConfigType type) {
        return ObjectUtil.isNotNull(type) && (NextHandlerConfigType.COVER.equals(type));
    }

    /**
     * 下个任务处理人配置类型是否是覆盖
     *
     * @param type 下个任务处理人配置类型
     * @return 是否是覆盖
     * @author xiarg
     * @since 2025/06/03 15:33:38
     */
    public static Boolean isAppend(NextHandlerConfigType type) {
        return ObjectUtil.isNotNull(type) && (NextHandlerConfigType.APPEND.equals(type));
    }

    /**
     * 处理下个任务的处理人
     *
     * @param type              下个任务处理人配置类型
     * @param nextHandlerList   下个任务的处理人
     * @param permissions       节点配置的原下个任务的处理人
     * @author xiarg
     * @since 2025/06/03 15:33:38
     */
    public static List<String> nextHandle(NextHandlerConfigType type, List<String> nextHandlerList, List<String> permissions) {
        if (CollUtil.isEmpty(nextHandlerList)) {
            return permissions;
        }
        if (isCover(type)) {
            permissions = nextHandlerList;
        }
        if (isAppend(type)) {
            permissions.addAll(nextHandlerList);
        }
        return permissions;
    }
}
