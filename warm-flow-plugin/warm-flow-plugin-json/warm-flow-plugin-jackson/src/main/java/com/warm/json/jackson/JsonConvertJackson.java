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
package com.warm.json.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.warm.flow.core.exception.FlowException;
import com.warm.flow.core.json.JsonConvert;
import com.warm.flow.core.utils.MapUtil;
import com.warm.flow.core.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * jackson：map和json字符串转换工具类
 *
 * @author warm
 */
public class JsonConvertJackson implements JsonConvert {

    private static final Logger log = LoggerFactory.getLogger(JsonConvertJackson.class);

    /**
     * 将字符串转为map
     * @param jsonStr json字符串
     * @return map
     */
    @Override
    public Map<String, Object> strToMap(String jsonStr) {
        if (StringUtils.isNotEmpty(jsonStr)) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                return objectMapper.readValue(jsonStr, TypeFactory.defaultInstance().constructMapType(Map.class, String.class, Object.class));
            } catch (IOException e) {
                log.error("json转换异常", e);
                throw new FlowException("json转换异常");
            }
        }
        return new HashMap<>();
    }

    /**
     * 将map转为字符串
     * @param variable map
     * @return json字符串
     */
    @Override
    public String mapToStr(Map<String, Object> variable) {
        if (MapUtil.isNotEmpty(variable)) {
            // 创建 ObjectMapper 实例
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                return objectMapper.writeValueAsString(variable);
            } catch (Exception e) {
                log.error("Map转换异常", e);
                throw new FlowException("Map转换异常");
            }
        }
        return null;
    }

}
