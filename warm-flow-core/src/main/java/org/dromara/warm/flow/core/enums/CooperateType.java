/*
 *    Copyright 2024-2025, Warm-Flow (290631660@qq.com).
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.dromara.warm.flow.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * 协作类型
 * APPROVAL-无：无其他协作方式
 * TRANSFER-转办：任务转给其他人办理
 * DEPUTE-委派：求助其他人审批，然后参照他的意见决定是否审批通过
 * COUNTERSIGN-会签：和其他人一起审批通过，才算通过
 * VOTE-票签：和部分人一起审批，达到一定通过率，才算通过
 * ADD_SIGNATURE-加签：办理中途，希望其他人一起参与办理
 * REDUCTION_SIGNATURE-减签：办理中途，希望某些人不参与办理
 *
 * @author xiarg
 * @since 2024/5/10 16:04
 */
@Getter
@AllArgsConstructor
public enum CooperateType {

    APPROVAL(1, "无"),
    TRANSFER(2, "转办"),
    DEPUTE(3, "委派"),
    COUNTERSIGN(4, "会签"),
    VOTE(5, "票签"),
    ADD_SIGNATURE(6, "加签"),
    REDUCTION_SIGNATURE(7, "减签");

    private final Integer key;
    private final String value;

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


    public final static BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100);

    /**
     * 判断是否为或签
     *
     * @param
     * @return
     */
    public static boolean isOrSign(BigDecimal ratio) {
        if (Objects.isNull(ratio) || ratio.compareTo(BigDecimal.ZERO) <= 0) {
            return true;
        }
        return false;
    }


    /**
     * 判断是否是票签
     *
     * @param
     * @return
     */
    public static boolean isVoteSign(BigDecimal ratio) {
        if (Objects.nonNull(ratio) && ratio.compareTo(BigDecimal.ZERO) > 0 && ratio.compareTo(ONE_HUNDRED) < 0) {
            return true;
        }
        return false;
    }

    /**
     * 判断是否是会签
     *
     * @param
     * @return
     */
    public static boolean isCountersign(BigDecimal ratio) {
        if (Objects.nonNull(ratio) && ratio.compareTo(ONE_HUNDRED) >= 0) {
            return true;
        }
        return false;
    }

}
