package com.warm.flow.core.enums;

/**
 * 流程用户类型
 *
 * @author xiarg
 * @date 2024/5/10 16:04
 */
public enum UserType {

    APPROVAL("1", "代办任务的审批人权限"),
    ASSIGNEE("2", "代办任务的转办人权限"),
    CCTO("3", "流程实例的抄送人权限"),
    APPROVER("4", "流程历史的已审批人"),
    PROPOSE("5", "流程节点的权限"),
    DEPUTE("6", "待办任务的委托人权限");

    private String key;
    private String value;

    private UserType(String key, String value) {
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
