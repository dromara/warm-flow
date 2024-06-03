package com.warm.flow.core.enums;

/**
 * 历史记录动作类型
 *
 * @author xiarg
 * @date 2024/5/10 16:04
 */
public enum ActionType {

    APPROVAL(1, "审批"),
    TRANSFER(2, "转办"),
    DEPUTE(3, "委派"),
    COUNTERSIGN(4, "会签"),
    VOTE(5, "票签"),
    ADD_SIGNATURE(6, "加签"),
    REDUCTION_SIGNATURE(7, "减签");

    private Integer key;
    private String value;

    ActionType(Integer key, String value) {
        this.key = key;
        this.value = value;
    }

    public static Integer getKeyByValue(String value) {
        for (ActionType item : ActionType.values()) {
            if (item.getValue().equals(value)) {
                return item.getKey();
            }
        }
        return null;
    }

    public static String getValueByKey(Integer key) {
        for (ActionType item : ActionType.values()) {
            if (item.getKey().equals(key)) {
                return item.getValue();
            }
        }
        return null;
    }

    public static ActionType getByKey(Integer key) {
        for (ActionType item : ActionType.values()) {
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
