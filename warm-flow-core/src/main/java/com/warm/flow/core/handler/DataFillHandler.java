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
package com.warm.flow.core.handler;

/**
 * @author warm
 * @description: 数据填充handler，以下三个接口按照实际情况实现
 * @date: 2023/4/1 15:37
 */
public interface DataFillHandler {

    /**
     * id填充
     *
     * @param object
     */
    void idFill(Object object);

    /**
     * 新增填充
     *
     * @param object
     */
    void insertFill(Object object);

    /**
     * 设置更新常用参数
     *
     * @param object
     */
    void updateFill(Object object);
}
