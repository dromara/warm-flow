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
package org.dromara.warm.flow.core.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.dromara.warm.flow.core.utils.StreamUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * 流程定义json对象数据集合
 *
 * @author warm
 * @since 2023/3/30 14:27
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DefCombine {
    /**
     * 所有的流程定义json对象
     */
    private DefJson defJson;

    /**
     * 所有的流程节点json对象
     */
    private List<NodeJson> nodeList = new ArrayList<>();

    /**
     * 所有的流程节点跳转关联json对象
     */
    private List<SkipJson> skipList = new ArrayList<>();

    public static DefCombine getDefJsonObj(DefJson defJson)  {
        List<SkipJson> skipList = StreamUtils.toListAll(defJson.getNodeList(), NodeJson::getSkipList);
        new DefCombine(defJson, defJson.getNodeList(), skipList);
        return new DefCombine(defJson, defJson.getNodeList(), skipList);
    }
}
