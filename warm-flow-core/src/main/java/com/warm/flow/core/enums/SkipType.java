package com.warm.flow.core.enums;

import com.warm.tools.utils.StringUtils;

/**
 * @author minliuhua
 * @description: 审批动作
 * @date: 2023/3/31 12:16
 */
public enum SkipType {
    PASS("PASS", "审批通过"),
    REJECT("REJECT", "驳回");

    private String key;
    private String value;

    private SkipType(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public static String getKeyByValue(String value) {
        for (SkipType item : SkipType.values()) {
            if (item.getValue().equals(value)) {
                return item.getKey();
            }
        }
        return null;
    }

    public static String getValueByKey(String key) {
        for (SkipType item : SkipType.values()) {
            if (item.getKey().equals(key)) {
                return item.getValue();
            }
        }
        return null;
    }

    public static SkipType getByKey(String key) {
        for (SkipType item : SkipType.values()) {
            if (item.getKey().equals(key)) {
                return item;
            }
        }
        return null;
    }

    /**
     * 判断是否通过类型
     *
     * @param Key
     * @return
     */
    public static Boolean isPass(String Key) {
        return StringUtils.isNotEmpty(Key) && (SkipType.PASS.getKey().equals(Key));
    }

    /**
     * 判断是否驳回类型
     *
     * @param Key
     * @return
     */
    public static Boolean isReject(String Key) {
        return StringUtils.isNotEmpty(Key) && (SkipType.REJECT.getKey().equals(Key));
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
