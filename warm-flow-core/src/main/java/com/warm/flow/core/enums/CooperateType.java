package com.warm.flow.core.enums;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * 协作方式
 *
 * @author vanlin
 * @date 2024/5/23 16:04
 */
public enum CooperateType {
    OR("orSign", "或签"),
    VOTE("voteSign", "票签"),
    ALL("countersign", "会签");

    public final static BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100);

    private String key;
    private String value;

    private CooperateType(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public static String getKeyByValue(String value) {
        for (CooperateType item : CooperateType.values()) {
            if (item.getValue().equals(value)) {
                return item.getKey();
            }
        }
        return null;
    }

    public static String getValueByKey(String Key) {
        for (CooperateType item : CooperateType.values()) {
            if (item.getKey().equals(Key)) {
                return item.getValue();
            }
        }
        return null;
    }

    /**
     * 判断是否为或签
     *
     * @param
     * @return
     */
    public static Boolean isOrSign(BigDecimal ratio) {
        if (Objects.isNull(ratio) || ratio.compareTo(BigDecimal.ZERO) <= 0) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }


    /**
     * 判断是否是票签
     *
     * @param
     * @return
     */
    public static Boolean isVoteSign(BigDecimal ratio) {
        if (Objects.nonNull(ratio)
            && ratio.compareTo(BigDecimal.ZERO) > 0
            && ratio.compareTo(ONE_HUNDRED) < 0) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    /**
     * 判断是否是会签
     *
     * @param
     * @return
     */
    public static Boolean isCountersign(BigDecimal ratio) {
        if (Objects.nonNull(ratio)
            && ratio.compareTo(ONE_HUNDRED) >= 0) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
