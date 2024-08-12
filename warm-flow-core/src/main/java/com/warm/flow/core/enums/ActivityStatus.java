package com.warm.flow.core.enums;

import com.warm.flow.core.utils.ObjectUtil;

public enum ActivityStatus {

    SUSPENDED(0,"挂起"),

    ACTIVITY(1,"激活");

    private Integer key;
    private String value;

    ActivityStatus(Integer key, String value) {
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
        for (ActivityStatus item : ActivityStatus.values()) {
            if (item.getValue().equals(value)) {
                return item.getKey();
            }
        }
        return null;
    }

    public static String getValueByKey(Integer Key) {
        for (ActivityStatus item : ActivityStatus.values()) {
            if (item.getKey().equals(Key)) {
                return item.getValue();
            }
        }
        return null;
    }

    public static ActivityStatus getByKey(String key) {
        for (ActivityStatus item : ActivityStatus.values()) {
            if (item.getKey().equals(key)) {
                return item;
            }
        }
        return null;
    }

    /**
     * 判断流程是否激活
     * @param Key
     * @return
     */
    public static Boolean isActivity(Integer Key) {
        return ObjectUtil.isNotNull(Key) && (ActivityStatus.ACTIVITY.getKey().equals(Key));
    }
}
