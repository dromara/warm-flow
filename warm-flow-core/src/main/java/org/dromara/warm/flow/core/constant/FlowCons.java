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
package org.dromara.warm.flow.core.constant;

import java.util.regex.Pattern;

/**
 * warm-flow常量
 *
 * @author warm
 */
public class FlowCons {

    /**
     * 分隔符
     */
    public static final String splitAt = "@@";
    public static final String splitVertical = "\\|";
    public static final String splitAnd = "and";
    public static final String splitOr = "or";

    public static final Pattern listenerPattern = Pattern.compile("^([^()]*)(.*)$");

    /**
     * 权限标识中的发起人标识符，办理过程中进行替换
     */
    public static final String WARMFLOWINITIATOR = "warmFlowInitiator";

    /**
     * 监听器参数
     */
    public static final String WARM_LISTENER_PARAM = "WarmListenerParam";

    /**
     * 雪花id 14位
     */
    public static final String SNOWID14 = "SnowId14";

    /**
     * 雪花id 15位
     */
    public static final String SNOWID15 = "SnowId15";

    /**
     * 雪花id 14位
     */
    public static final String SNOWID19 = "SnowId19";


    /**
     * 表单自定义状态
     * 内置表单
     */
    public static final String FORM_CUSTOM_Y = "Y";
    /**
     * 外挂表单路径
     */
    public static final String FORM_CUSTOM_N = "N";

    /**
     * 表单数据
     */
    public static final String FORM_DATA = "formData";
}
