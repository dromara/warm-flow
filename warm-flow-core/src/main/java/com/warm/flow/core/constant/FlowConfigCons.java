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
package com.warm.flow.core.constant;

/**
 * warm-flow配置文件常量
 *
 * @author warm
 */
public class FlowConfigCons {

    /**
     * 是否支持任意跳转
     */
    public static final String BANNER = "warm-flow.banner";

    /**
     * 是否开启逻辑删除
     */
    public static final String LOGICDELETE = "warm-flow.logic_delete";

    /**
     * 是否开启逻辑删除
     */
    public static final String KEYTYPE = "warm-flow.key_type";

    /**
     * 逻辑删除字段值
     */
    public static final String LOGICDELETEVALUE = "warm-flow.logic_delete_value";

    /**
     * 逻辑未删除字段
     */
    public static final String LOGICNOTDELETEVALUE = "warm-flow.logic_not_delete_value";

    /**
     * 数据填充处理类路径
     */
    public static final String DATAFILLHANDLEPATH = "warm-flow.data-fill-handler-path";

    /**
     * 租户处理类路径
     */
    public static final String TENANTHANDLERPATH = "warm-flow.tenant_handler_path";
    /**
     * 数据源类型, mybatis模块对orm进一步的封装, 由于各数据库分页语句存在差异,
     * 当配置此参数时, 以此参数结果为基准, 未配置时, 取DataSource中数据源类型,
     * 兜底为mysql数据库
     */
    public static final String DATA_SOURCE_TYPE = "warm-flow.data_source_type";
}
