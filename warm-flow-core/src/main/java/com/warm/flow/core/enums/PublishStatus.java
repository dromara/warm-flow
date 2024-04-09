package com.warm.flow.core.enums;

/**
 * @author minliuhua
 * @description: 节点类型
 * @date: 2023/3/31 12:16
 */
public enum PublishStatus {
    EXPIRED(9, "已失效"),
    UNPUBLISHED(0, "未发布"),
    PUBLISHED(1, "已发布");

    private Integer key;
    private String value;

    private PublishStatus(Integer key, String value) {
        this.key = key;
        this.value = value;
    }

    public Integer getKey() {
        return key;
    }

    public String getValue() {
        return value;
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
}
