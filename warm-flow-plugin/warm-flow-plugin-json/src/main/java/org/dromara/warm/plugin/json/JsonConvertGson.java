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
package org.dromara.warm.plugin.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.dromara.warm.flow.core.json.JsonConvert;
import org.dromara.warm.flow.core.utils.ObjectUtil;
import org.dromara.warm.flow.core.utils.StringUtils;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * fastjson：map和json字符串转换工具类
 *
 * @author warm
 */
public class JsonConvertGson implements JsonConvert {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    /**
     * 将字符串转为map
     * @param jsonStr json字符串
     * @return map
     */
    @Override
    public Map<String, Object> strToMap(String jsonStr) {
        if (StringUtils.isNotEmpty(jsonStr)) {
            Type type = new TypeToken<Map<String, Object>>(){}.getType();
            return GSON.fromJson(jsonStr, type);
        }
        return new HashMap<>();
    }

    /**
     * 将字符串转为bean
     * @param jsonStr json字符串
     * @param clazz Class<T>
     * @return T
     */
    @Override
    public <T> T strToBean(String jsonStr, Class<T> clazz) {
        if (StringUtils.isNotEmpty(jsonStr)) {
            return GSON.fromJson(jsonStr, clazz);
        }
        return null;
    }

    /**
     * 将字符串转为集合
     * @param jsonStr json字符串
     * @return List<T>
     */
    @Override
    public <T> List<T> strToList(String jsonStr) {
        if (StringUtils.isNotEmpty(jsonStr)) {
            Type listType = new TypeToken<List<T>>() {}.getType();
            return GSON.fromJson(jsonStr, listType);
        }
        return null;
    }

    /**
     * 将对象转为字符串
     * @param variable object
     * @return json字符串
     */
    @Override
    public String objToStr(Object variable) {
        if (ObjectUtil.isNotNull(variable)) {
            return GSON.toJson(variable);
        }
        return null;
    }

}
