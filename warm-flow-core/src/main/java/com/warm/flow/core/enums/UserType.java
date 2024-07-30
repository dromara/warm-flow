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
package com.warm.flow.core.enums;

/**
 * 流程用户类型
 *
 * @author xiarg
 * @date 2024/5/10 16:04
 */
public enum UserType {

    APPROVAL("1", "待办任务的审批人权限"),
    TRANSFER("2", "待办任务的转办人权限"),
    DEPUTE("3", "待办任务的委托人权限");

    private String key;
    private String value;

    UserType(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public static String getKeyByValue(String value) {
        for (UserType item : UserType.values()) {
            if (item.getValue().equals(value)) {
                return item.getKey();
            }
        }
        return null;
    }

    public static String getValueByKey(String key) {
        for (UserType item : UserType.values()) {
            if (item.getKey().equals(key)) {
                return item.getValue();
            }
        }
        return null;
    }

    public static UserType getByKey(String key) {
        for (UserType item : UserType.values()) {
            if (item.getKey().equals(key)) {
                return item;
            }
        }
        return null;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
