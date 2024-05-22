package com.warm.flow.core.enums;

/**
 * 一个任务权限流转类型
 *
 * @author xiarg
 * @date 2024/5/10 16:04
 */
public enum CirculationType {

    SIGNATURE("signature", "加减签", false),
    TRANSFER("transfer", "转办(不清理待办任务的计划审批人)", false),
    CHANGE("transfer", "转办(清理待办任务的计划审批人)", true),
    DEPUTE("depute", "委派", false);

    private String key;
    private String value;
    private boolean clear;

    private CirculationType(String key, String value, boolean clear) {
        this.key = key;
        this.value = value;
        this.clear = clear;
    }

    public static String getKeyByValue(String value) {
        for (CirculationType item : CirculationType.values()) {
            if (item.getValue().equals(value)) {
                return item.getKey();
            }
        }
        return null;
    }

    public static String getValueByKey(String key) {
        for (CirculationType item : CirculationType.values()) {
            if (item.getKey().equals(key)) {
                return item.getValue();
            }
        }
        return null;
    }

    public static CirculationType getByKey(String key) {
        for (CirculationType item : CirculationType.values()) {
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

    public boolean getClear() {
        return clear;
    }
}
