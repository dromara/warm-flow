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
package com.warm.flow.core.utils;

import com.warm.flow.core.exception.FlowException;

import java.util.Collection;

/**
 * @author warm
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

    /**
     * 为true不抛异常
     *
     * @param obj
     * @param errorMsg
     */
    public static void isFalse(boolean obj, String errorMsg) {
        if (!obj) {
            throw new FlowException(errorMsg);
        }
    }

    /**
     * 为false不抛异常
     *
     * @param obj
     * @param errorMsg
     */
    public static void isTrue(boolean obj, String errorMsg) {
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
