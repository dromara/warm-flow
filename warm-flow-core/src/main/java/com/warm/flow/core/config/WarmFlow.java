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
package com.warm.flow.core.config;

import com.warm.flow.core.FlowFactory;
import com.warm.flow.core.constant.ExceptionCons;
import com.warm.flow.core.constant.FlowConfigCons;
import com.warm.flow.core.constant.FlowCons;
import com.warm.flow.core.expression.ExpressionStrategy;
import com.warm.flow.core.invoker.FrameInvoker;
import com.warm.flow.core.json.JsonConvert;
import com.warm.flow.core.utils.ExpressionUtil;
import com.warm.flow.core.utils.ObjectUtil;
import com.warm.flow.core.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * WarmFlow属性配置文件
 *
 * @author warm
 */
public class WarmFlow implements Serializable {

    private static final Logger log = LoggerFactory.getLogger(WarmFlow.class);

    /**
     * 开关
     */
    private boolean enabled = true;

    /**
     * 启动banner
     */
    private boolean banner = true;

    /**
     * id生成器类型, 不填默认为orm扩展自带生成器或者warm-flow内置的19位雪花算法, SnowId14:14位，SnowId15:15位， SnowFlake19：19位
     */
    private String keyType = FlowCons.SNOWID19;

    /**
     * 是否开启逻辑删除
     */
    private boolean logicDelete = false;

    /**
     * 逻辑删除字段值
     */
    private String logicDeleteValue = "2";

    /**
     * 逻辑未删除字段
     */
    private String logicNotDeleteValue = "0";

    /**
     * 数据填充处理类路径
     */
    private String dataFillHandlerPath;

    /**
     * 租户模式处理类路径
     */
    private String tenantHandlerPath;
    /**
     * 数据源类型, mybatis模块对orm进一步的封装, 由于各数据库分页语句存在差异,
     * 当配置此参数时, 以此参数结果为基准, 未配置时, 取DataSource中数据源类型,
     * 兜底为mysql数据库
     */
    private String dataSourceType;

    public static WarmFlow init() {
        WarmFlow flowConfig = new WarmFlow();
        // 设置banner
        String banner = FrameInvoker.getCfg(FlowConfigCons.BANNER);
        if (StringUtils.isNotEmpty(banner)) {
            flowConfig.setBanner(ObjectUtil.isStrTrue(banner));
        }

        // 设置id生成器类型
        String keyType = FrameInvoker.getCfg(FlowConfigCons.KEYTYPE);
        if (StringUtils.isNotEmpty(keyType)) {
            flowConfig.setKeyType(keyType);
        }

        // 设置逻辑删除
        setLogicDelete(flowConfig);

        // 设置租户模式
        flowConfig.setTenantHandlerPath(FrameInvoker.getCfg(FlowConfigCons.TENANTHANDLERPATH));

        // 设置数据填充处理类
        flowConfig.setDataFillHandlerPath(FrameInvoker.getCfg(FlowConfigCons.DATAFILLHANDLEPATH));

        // 设置数据源类型
        flowConfig.setDataSourceType(FrameInvoker.getCfg(FlowConfigCons.DATA_SOURCE_TYPE));
        printBanner(flowConfig);

        // 通过SPI机制
        spiLoad();
        return flowConfig;
    }
    public static void spiLoad() {
        // 通过SPI机制加载条件表达式策略实现类
        ServiceLoader<ExpressionStrategy> expressionServiceLoader = ServiceLoader.load(ExpressionStrategy.class);
        Iterator<ExpressionStrategy> expressionIterator = expressionServiceLoader.iterator();
        try {
            while (expressionIterator.hasNext()) {
                ExpressionStrategy expressionStrategy = expressionIterator.next();
                ExpressionUtil.setExpression(expressionStrategy);
            }
        } catch (Throwable t) {
            log.error(ExceptionCons.LOAD_EXPRESSION_STRATEGY_ERROR, t);
        }

        // 通过SPI机制加载json转换策略实现类
        ServiceLoader<JsonConvert> JsonConvertServiceLoader = ServiceLoader.load(JsonConvert.class);
        Iterator<JsonConvert> jsonConvertIterator = JsonConvertServiceLoader.iterator();
        try {
            if (jsonConvertIterator.hasNext()) {
                JsonConvert jsonConvert = jsonConvertIterator.next();
                FlowFactory.jsonConvert(jsonConvert);
            } else {
                log.error(ExceptionCons.LOAD_JSON_CONVERT_ERROR);
            }
        } catch (Throwable t) {
            log.error(ExceptionCons.LOAD_JSON_CONVERT_ERROR, t);
        }

    }

    private static void setLogicDelete(WarmFlow flowConfig) {
        String logicDelete = FrameInvoker.getCfg(FlowConfigCons.LOGICDELETE);
        if (ObjectUtil.isStrTrue(logicDelete)) {
            flowConfig.setLogicDelete(ObjectUtil.isStrTrue(logicDelete));
            String logicDeleteValue = FrameInvoker.getCfg(FlowConfigCons.LOGICDELETEVALUE);
            if (StringUtils.isNotEmpty(logicDeleteValue)) {
                flowConfig.setLogicDeleteValue(logicDeleteValue);
            }
            String logicNotDeleteValue = FrameInvoker.getCfg(FlowConfigCons.LOGICNOTDELETEVALUE);
            if (StringUtils.isNotEmpty(logicNotDeleteValue)) {
                flowConfig.setLogicNotDeleteValue(logicNotDeleteValue);
            }
        }
    }

    private static void printBanner(WarmFlow flowConfig) {
        if (flowConfig.isBanner()) {
            System.out.println("\n" +
                    "   ▄     ▄                      ▄▄▄▄▄▄   ▄                     \n" +
                    "   █  █  █  ▄▄▄    ▄ ▄▄  ▄▄▄▄▄  █        █     ▄▄▄  ▄     ▄    \n" +
                    "   ▀ █▀█ █ ▀   █   █▀  ▀ █ █ █  █▄▄▄▄▄   █    █▀ ▀█ ▀▄ ▄ ▄▀    \n" +
                    "    ██ ██▀ ▄▀▀▀█   █     █ █ █  █        █    █   █  █▄█▄█     \n" +
                    "    █   █  ▀▄▄▀█   █     █ █ █  █        ▀▄▄  ▀█▄█▀   █ █      \n" +
                    "\n" +
                    "\033[32m   :: Warm-Flow ::     (v" + WarmFlow.class.getPackage()
                    .getImplementationVersion() + ")\033[0m\n");
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isBanner() {
        return banner;
    }

    public void setBanner(boolean banner) {
        this.banner = banner;
    }

    public String getKeyType() {
        return keyType;
    }

    public void setKeyType(String keyType) {
        this.keyType = keyType;
    }

    public boolean isLogicDelete() {
        return logicDelete;
    }

    public void setLogicDelete(boolean logicDelete) {
        this.logicDelete = logicDelete;
    }

    public String getLogicDeleteValue() {
        return logicDeleteValue;
    }

    public void setLogicDeleteValue(String logicDeleteValue) {
        this.logicDeleteValue = logicDeleteValue;
    }

    public String getLogicNotDeleteValue() {
        return logicNotDeleteValue;
    }

    public void setLogicNotDeleteValue(String logicNotDeleteValue) {
        this.logicNotDeleteValue = logicNotDeleteValue;
    }

    public String getDataFillHandlerPath() {
        return dataFillHandlerPath;
    }

    public void setDataFillHandlerPath(String dataFillHandlerPath) {
        this.dataFillHandlerPath = dataFillHandlerPath;
    }

    public String getTenantHandlerPath() {
        return tenantHandlerPath;
    }

    public void setTenantHandlerPath(String tenantHandlerPath) {
        this.tenantHandlerPath = tenantHandlerPath;
    }

    public String getDataSourceType() {
        return dataSourceType;
    }

    public void setDataSourceType(String dataSourceType) {
        this.dataSourceType = dataSourceType;
    }
}
