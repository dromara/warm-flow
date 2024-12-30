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
package org.dromara.warm.flow.core.utils;

import org.dromara.warm.flow.core.FlowEngine;
import org.dromara.warm.flow.core.constant.FlowCons;
import org.dromara.warm.flow.core.keygen.KenGen;
import org.dromara.warm.flow.core.keygen.SnowFlakeId14;
import org.dromara.warm.flow.core.keygen.SnowFlakeId15;
import org.dromara.warm.flow.core.keygen.SnowFlakeId19;

/**
 * 唯一id
 *
 * @author warm
 * @since 2023/5/17 23:08
 */
public class IdUtils {

    /** 内置id算法 */
    private volatile static KenGen instance;

    /** orm框架配置了原生id算法 */
    private static KenGen instanceNative;

    public static String nextIdStr() {
        return nextId().toString();
    }

    public static Long nextId() {
        return nextId(1, 1);
    }

    public static Long nextId(long workerId, long datacenterId) {
        if (instance == null) {
            synchronized (IdUtils.class) {
                if (instance == null) {
                    String keyType = FlowEngine.getFlowConfig().getKeyType();
                    if (FlowCons.SNOWID14.equals(keyType)) {
                        instance = new SnowFlakeId14(workerId);
                    } else if (FlowCons.SNOWID15.equals(keyType)) {
                        instance = new SnowFlakeId15(workerId);
                    }
                    if (instance == null) {
                        // 如果orm框架配置了原生id算法，则使用原生id算法，否则默认使用19位内置雪花算法
                        if (instanceNative != null) {
                            instance = instanceNative;
                        } else {
                            instance = new SnowFlakeId19(workerId, datacenterId);
                        }
                    }
                }
            }
        }
        return instance.nextId();
    }

    public static void setInstanceNative(KenGen instanceNative) {
        IdUtils.instanceNative = instanceNative;
    }

}
