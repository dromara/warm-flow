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
package org.dromara.warm.flow.core.invoker;

import java.util.function.Function;

/**
 * 获取bean方法
 *
 * @author warm
 */
public class FrameInvoker<M> {

    public static FrameInvoker frameInvoker = new FrameInvoker<>();

    private Function<Class<M>, M> beanFunction;

    private Function<String, String> cfgFunction;

    public FrameInvoker() {
    }

    /**
     * 设置获取beanFunction
     *
     * @param function
     * @param <M>
     */
    public static <M> void setBeanFunction(Function<Class<M>, M> function) {
        frameInvoker.beanFunction = function;
    }

    public static <M> M getBean(Class<M> tClass) {
        try {
            return (M) frameInvoker.beanFunction.apply(tClass);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 设置获取配置function
     *
     * @param function
     */
    public static void setCfgFunction(Function<String, String> function) {
        frameInvoker.cfgFunction = function;
    }

    public static String getCfg(String key) {
        try {
            return (String) frameInvoker.cfgFunction.apply(key);
        } catch (Exception e) {
            return null;
        }
    }

}
