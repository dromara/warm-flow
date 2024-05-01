package com.warm.flow.core.enums;

import com.warm.tools.utils.ObjectUtil;

/**
 * @author minliuhua
 * @description: 流程状态
 * @date: 2023/3/31 12:16
 */
public enum FlowStatus {
    TOBESUBMIT(0, "待提交"),
    APPROVAL(1, "审批中"),
    PASS(2, "审批通过"),
    FINISHED(8, "已完成"),
    REJECT(9, "已退回"),
    INVALID(10, "失效");

    private Integer key;
    private String value;

    private FlowStatus(Integer key, String value) {
        this.key = key;
        this.value = value;
    }

    public static Integer getKeyByValue(String value) {
        for (FlowStatus item : FlowStatus.values()) {
            if (item.getValue().equals(value)) {
                return item.getKey();
            }
        }
        return null;
    }

    public static String getValueByKey(Integer key) {
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
    public static Boolean isFinished(Integer Key) {
        return ObjectUtil.isNotNull(Key) && (FlowStatus.FINISHED.getKey().equals(Key));
    }

    public Integer getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
