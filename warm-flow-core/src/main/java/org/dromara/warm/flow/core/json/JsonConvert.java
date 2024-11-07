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
package org.dromara.warm.flow.core.json;

import java.util.Map;

/**
 * map和json字符串转换工具类
 *
 * @author warm
 */
public interface JsonConvert {

    /**
     * 将字符串转为map
     * @param jsonStr json字符串
     * @return map
     */
    public Map<String, Object> strToMap(String jsonStr);

    /**
     * 将map转为字符串
     * @param variable map
     * @return json字符串
     */
    public String mapToStr(Map<String, Object> variable);
}
