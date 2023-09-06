package com.warm.flow.core.utils;

import com.warm.flow.core.exception.FlowException;
import com.warm.tools.utils.StringUtils;

import java.util.Collection;

/**
 * @author minliuhua
 * @description: Assert类
 * @date: 2023/3/30 14:05
 */
public class AssertUtil {
    private AssertUtil() {

    }

    public static void isNull(Object obj, String errorMsg) {
        if (obj == null) {
            throw new FlowException(errorMsg);
        }
    }

    public static void isBlank(String obj, String errorMsg) {
        if (StringUtils.isEmpty(obj)) {
            throw new FlowException(errorMsg);
        }
    }

    public static void isTrue(boolean obj, String errorMsg) {
        if (!obj) {
            throw new FlowException(errorMsg);
        }
    }

    public static void isFalse(boolean obj, String errorMsg) {
        if (obj) {
            throw new FlowException(errorMsg);
        }
    }

    public static void notEmpty(Collection<?> obj, String errorMsg) {
        if ((obj == null) || (obj.isEmpty())) {
            throw new FlowException(errorMsg);
        }
    }
}
