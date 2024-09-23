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
package com.warm.plugin.json;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.warm.flow.core.json.JsonConvert;
import com.warm.flow.core.utils.MapUtil;
import com.warm.flow.core.utils.StringUtils;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * fastjson：map和json字符串转换工具类
 *
 * @author warm
 */
public class JsonConvertGson implements JsonConvert {

    private static final Gson gson = new Gson();

    /**
     * 将字符串转为map
     * @param jsonStr json字符串
     * @return map
     */
    @Override
    public Map<String, Object> strToMap(String jsonStr) {
        if (StringUtils.isNotEmpty(jsonStr)) {
            Type type = new TypeToken<Map<String, Object>>(){}.getType();
            return gson.fromJson(jsonStr, type);
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
            return gson.toJson(variable);
        }
        return null;
    }

}
