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

import com.warm.flow.core.FlowFactory;
import com.warm.flow.core.constant.FlowCons;
import com.warm.flow.core.keygen.KenGen;
import com.warm.flow.core.keygen.SnowFlakeId14;
import com.warm.flow.core.keygen.SnowFlakeId15;
import com.warm.flow.core.keygen.SnowFlakeId19;

/**
 * @author warm
 * @description: 唯一id
 * @date: 2023/5/17 23:08
 */
public class IdUtils {

    private volatile static KenGen instance;
    private static KenGen instanceExt;

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
                    String keyType = FlowFactory.getFlowConfig().getKeyType();
                    if (FlowCons.SNOWID14.equals(keyType)) {
                        instance = new SnowFlakeId14(workerId);
                    } else if (FlowCons.SNOWID15.equals(keyType)) {
                        instance = new SnowFlakeId15(workerId);
                    }
                    if (instanceExt != null) {
                        instance = instanceExt;
                    }
                    if (instance == null) {
                        instance = new SnowFlakeId19(workerId, datacenterId);
                    }
                }
            }
        }
        return instance.nextId();
    }

    public static void setInstanceExt(KenGen instanceExt) {
        IdUtils.instanceExt = instanceExt;
    }

}
