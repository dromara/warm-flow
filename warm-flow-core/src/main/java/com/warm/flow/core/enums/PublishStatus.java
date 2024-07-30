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
 * @author warm
 * @description: 节点类型
 * @date: 2023/3/31 12:16
 */
public enum PublishStatus {
    EXPIRED(9, "已失效"),
    UNPUBLISHED(0, "未发布"),
    PUBLISHED(1, "已发布");

    private Integer key;
    private String value;

    PublishStatus(Integer key, String value) {
        this.key = key;
        this.value = value;
    }

    public static Integer getKeyByValue(String value) {
        for (PublishStatus item : PublishStatus.values()) {
            if (item.getValue().equals(value)) {
                return item.getKey();
            }
        }
        return null;
    }

    public static String getValueByKey(Integer key) {
        for (PublishStatus item : PublishStatus.values()) {
            if (item.getKey().equals(key)) {
                return item.getValue();
            }
        }
        return null;
    }

    public static PublishStatus getByKey(String key) {
        for (PublishStatus item : PublishStatus.values()) {
            if (item.getKey().equals(key)) {
                return item;
            }
        }
        return null;
    }

    public Integer getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
