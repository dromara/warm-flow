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

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.TypeReference;
import org.dromara.warm.flow.core.json.JsonConvert;
import org.dromara.warm.flow.core.utils.ObjectUtil;
import org.dromara.warm.flow.core.utils.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * fastjson：map和json字符串转换工具类
 *
 * @author warm
 */
public class JsonConvertFastJson implements JsonConvert {

    /**
     * 将字符串转为map
     * @param jsonStr json字符串
     * @return map
     */
    @Override
    public Map<String, Object> strToMap(String jsonStr) {
        if (StringUtils.isNotEmpty(jsonStr)) {
            return JSON.parseObject(jsonStr, new TypeReference<Map<String, Object>>(){});
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
            return JSON.parseObject(jsonStr, clazz);
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
            return JSON.parseObject(jsonStr, new TypeReference<List<T>>() {});
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
            return JSON.toJSONString(variable, JSONWriter.Feature.PrettyFormat);
        }
        return null;
    }

}
