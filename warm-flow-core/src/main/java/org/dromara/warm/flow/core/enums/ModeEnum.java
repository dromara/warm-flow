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

/**
 * 设计器模型（CLASSICS经典模型 MIMIC仿钉钉模型）
 *
 * @author warm
 * @since 2025/6/25
 */
@Getter
@AllArgsConstructor
public enum ModeEnum {

    // 经典模型
    CLASSICS,

    // 仿钉钉模型
    MIMIC
}
