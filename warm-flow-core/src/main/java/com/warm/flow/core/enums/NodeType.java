package com.warm.flow.core.enums;

/**
 * @author minliuhua
 * @description: 结点类型
 * @date: 2023/3/31 12:16
 */
public enum NodeType {
    /**
     * 开始结点
     */
    START(0, "start"),
    /**
     * 中间结点
     */
    BETWEEN(1, "between"),
    /**
     * 结束结点
     */
    END(2, "end");

    private Integer key;
    private String value;

    private NodeType(Integer key, String value) {
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

}
