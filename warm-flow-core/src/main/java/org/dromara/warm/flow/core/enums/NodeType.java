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
 * 节点类型
 *
 * @author warm
 * @since 2023/3/31 12:16
 */
@AllArgsConstructor
@Getter
public enum NodeType {
    /**
     * 开始节点
     */
    START(0, "start"),
    /**
     * 中间节点
     */
    BETWEEN(1, "between"),
    /**
     * 结束节点
     */
    END(2, "end"),

    /**
     * 互斥网关
     */
    SERIAL(3, "serial"),

    /**
     * 并行网关
     */
    PARALLEL(4, "parallel");

    private final Integer key;
    private final String value;

    public static Integer getKeyByValue(String value) {
        for (NodeType item : NodeType.values()) {
            if (item.getValue().equals(value)) {
                return item.getKey();
            }
        }
        return null;
    }

    public static String getValueByKey(Integer Key) {
        for (NodeType item : NodeType.values()) {
            if (item.getKey().equals(Key)) {
                return item.getValue();
            }
        }
        return null;
    }

    public static NodeType getByKey(Integer key) {
        for (NodeType item : NodeType.values()) {
            if (item.getKey().equals(key)) {
                return item;
            }
        }
        return null;
    }

    /**
     * 判断是否开始节点
     *
     * @param Key
     * @return
     */
    public static Boolean isStart(Integer Key) {
        return ObjectUtil.isNotNull(Key) && (NodeType.START.getKey().equals(Key));
    }

    /**
     * 判断是否中间节点
     *
     * @param Key
     * @return
     */
    public static Boolean isBetween(Integer Key) {
        return ObjectUtil.isNotNull(Key) && (NodeType.BETWEEN.getKey().equals(Key));
    }

    /**
     * 判断是否结束节点
     *
     * @param Key
     * @return
     */
    public static Boolean isEnd(Integer Key) {
        return ObjectUtil.isNotNull(Key) && (NodeType.END.getKey().equals(Key));
    }

    /**
     * 判断是否网关节点
     *
     * @param Key
     * @return
     */
    public static Boolean isGateWay(Integer Key) {
        return ObjectUtil.isNotNull(Key) && (NodeType.SERIAL.getKey().equals(Key)
                || NodeType.PARALLEL.getKey().equals(Key));
    }

    /**
     * 判断是否互斥网关节点
     *
     * @param Key
     * @return
     */
    public static Boolean isGateWaySerial(Integer Key) {
        return ObjectUtil.isNotNull(Key) && NodeType.SERIAL.getKey().equals(Key);
    }

    /**
     * 判断是否并行网关节点
     *
     * @param Key
     * @return
     */
    public static Boolean isGateWayParallel(Integer Key) {
        return ObjectUtil.isNotNull(Key) && NodeType.PARALLEL.getKey().equals(Key);
    }

}
