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

import org.dromara.warm.flow.core.utils.ObjectUtil;

/**
 * 流程状态
 *
 * @author warm
 * @since 2023/3/31 12:16
 */
public enum FlowStatus {
    TOBESUBMIT("0", "待提交"),

    APPROVAL("1", "审批中"),

    PASS("2", "审批通过"),

    AUTO_PASS("3", "自动完成"),

    TERMINATE("4", "终止"),

    NULLIFY("5", "作废"),

    CANCEL("6", "撤销"),

    RETRIEVE("7", "取回"),

    FINISHED("8", "已完成"),

    REJECT("9", "已退回"),

    INVALID("10", "失效");

    private String key;
    private String value;

    FlowStatus(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public static String getKeyByValue(String value) {
        for (FlowStatus item : FlowStatus.values()) {
            if (item.getValue().equals(value)) {
                return item.getKey();
            }
        }
        return null;
    }

    public static String getValueByKey(String key) {
        for (FlowStatus item : FlowStatus.values()) {
            if (item.getKey().equals(key)) {
                return item.getValue();
            }
        }
        return null;
    }

    public static FlowStatus getByKey(String key) {
        for (FlowStatus item : FlowStatus.values()) {
            if (item.getKey().equals(key)) {
                return item;
            }
        }
        return null;
    }

    /**
     * 判断是否结束节点
     *
     * @param Key
     * @return
     */
    public static Boolean isFinished(String Key) {
        return ObjectUtil.isNotNull(Key) && (FlowStatus.FINISHED.getKey().equals(Key));
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
