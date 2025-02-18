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

import org.dromara.warm.flow.core.json.JsonConvert;
import org.dromara.warm.flow.core.utils.ObjectUtil;
import org.dromara.warm.flow.core.utils.StringUtils;
import org.noear.snack.ONode;
import org.noear.snack.core.Feature;
import org.noear.snack.core.Options;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * snack：map和json字符串转换工具类
 *
 * @author warm
 */
public class JsonConvertSnack implements JsonConvert {

    /**
     * 不可删除，为了在spi加载时候，发下不存在snack3依赖包，触发异常不加载此实现类
     */
    private static final ONode O_NODE = new ONode();

    /**
     * 将字符串转为map
     * @param jsonStr json字符串
     * @return map
     */
    @Override
    public Map<String, Object> strToMap(String jsonStr) {
        return StringUtils.isEmpty(jsonStr) ? new HashMap<>() : ONode.deserialize(jsonStr);
    }

    /**
     * 将字符串转为bean
     * @param jsonStr json字符串
     * @param clazz Class<T>
     * @return T
     */
    @Override
    public <T> T strToBean(String jsonStr, Class<T> clazz) {
        return StringUtils.isEmpty(jsonStr) ? null : ONode.deserialize(jsonStr, clazz);
    }

    /**
     * 将字符串转为集合
     * @param jsonStr json字符串
     * @return List<T>
     */
    @Override
    public <T> List<T> strToList(String jsonStr) {
        return StringUtils.isEmpty(jsonStr) ? null : ONode.deserialize(jsonStr, (new ArrayList<T>(){}).getClass());
    }

    /**
     * 将对象转为字符串
     * @param variable object
     * @return json字符串
     */
    @Override
    public String objToStr(Object variable) {
        if (ObjectUtil.isNotNull(variable)) {
            return ONode.stringify(variable, Options.def().add(Feature.PrettyFormat));
        }
        return null;
    }
}
