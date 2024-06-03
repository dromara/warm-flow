package com.warm.flow.core.enums;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * 协作类型
 *
 * @author xiarg
 * @date 2024/5/10 16:04
 */
public enum CooperateType {

    OR_SIGN(1, "或签"),
    TRANSFER(2, "转办"),
    DEPUTE(3, "委派"),
    COUNTERSIGN(4, "会签"),
    VOTE(5, "票签"),
    ADD_SIGNATURE(6, "加签"),
    REDUCTION_SIGNATURE(7, "减签");

    private Integer key;
    private String value;

    CooperateType(Integer key, String value) {
        this.key = key;
        this.value = value;
    }

    public static Integer getKeyByValue(String value) {
        for (CooperateType item : CooperateType.values()) {
            if (item.getValue().equals(value)) {
                return item.getKey();
            }
        }
        return null;
    }

    public static String getValueByKey(Integer key) {
        for (CooperateType item : CooperateType.values()) {
            if (item.getKey().equals(key)) {
                return item.getValue();
            }
        }
        return null;
    }

    public static CooperateType getByKey(Integer key) {
        for (CooperateType item : CooperateType.values()) {
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


    public final static BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100);

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
        if (Objects.nonNull(ratio) && ratio.compareTo(BigDecimal.ZERO) > 0 && ratio.compareTo(ONE_HUNDRED) < 0) {
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
        if (Objects.nonNull(ratio) && ratio.compareTo(ONE_HUNDRED) >= 0) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

}
