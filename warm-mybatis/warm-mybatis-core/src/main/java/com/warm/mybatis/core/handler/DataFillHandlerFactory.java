/**
 * Copyright (c) 2022-2023, Mybatis-Flex (fuhai999@gmail.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.warm.mybatis.core.handler;

/**
 * @description: 填充类工厂
 * @author minliuhua
 * @date 2023/6/23 15:46
 */
public class DataFillHandlerFactory {

    private static DataFillHandler DATA_FILL_MAP = null;


    /**
     * 注册填充类
     *
     * @param dataFillHandler
     */
    public static void set(DataFillHandler dataFillHandler) {
        DATA_FILL_MAP = dataFillHandler;
    }

    /**
     * 获取填充类
     *
     */
    public static DataFillHandler get() {
        return DATA_FILL_MAP;
    }

}
