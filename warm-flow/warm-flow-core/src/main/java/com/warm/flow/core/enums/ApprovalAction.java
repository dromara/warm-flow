package com.warm.flow.core.enums;

/**
 * @author minliuhua
 * @description: 审批动作
 * @date: 2023/3/31 12:16
 */
public enum ApprovalAction {
    PASS("PASS", "审批通过"),
    REJECT("REJECT", "驳回");

    private String key;
    private String value;

    private ApprovalAction(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public static String getKeyByValue(String value) {
        for (ApprovalAction item : ApprovalAction.values()) {
            if (item.getValue().equals(value)) {
                return item.getKey();
            }
        }
        return null;
    }

    public static String getValueByKey(String key) {
        for (ApprovalAction item : ApprovalAction.values()) {
            if (item.getKey().equals(key)) {
                return item.getValue();
            }
        }
        return null;
    }
}
