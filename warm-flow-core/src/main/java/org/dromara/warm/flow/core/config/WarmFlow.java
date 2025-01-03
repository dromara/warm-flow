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
package org.dromara.warm.flow.core.config;

import lombok.Getter;
import lombok.Setter;
import org.dromara.warm.flow.core.FlowFactory;
import org.dromara.warm.flow.core.constant.FlowConfigCons;
import org.dromara.warm.flow.core.invoker.FrameInvoker;
import org.dromara.warm.flow.core.json.JsonConvert;
import org.dromara.warm.flow.core.utils.ObjectUtil;
import org.dromara.warm.flow.core.utils.ServiceLoaderUtil;
import org.dromara.warm.flow.core.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 * WarmFlow属性配置文件
 *
 * @author warm
 */
@Getter
@Setter
public class WarmFlow implements Serializable {

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
    private String keyType;

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

    /**
     * ui开关
     */
    private boolean ui = true;

    /**
     * 如果需要工作流共享业务系统权限，默认Authorization，如果有多个token，用逗号分隔
     */
    private String tokenName = "Authorization";

    public static WarmFlow init() {
        WarmFlow flowConfig = new WarmFlow();
        // 设置banner
        String banner = FrameInvoker.getCfg(FlowConfigCons.BANNER);
        if (StringUtils.isNotEmpty(banner)) {
            flowConfig.setBanner(ObjectUtil.isStrTrue(banner));
        }

        // 设置ui开关
        String ui = FrameInvoker.getCfg(FlowConfigCons.UI);
        if (StringUtils.isNotEmpty(ui)) {
            flowConfig.setUi(ObjectUtil.isStrTrue(ui));
        }

        // 设置TOKEN_NAME开关
        String tokenName = FrameInvoker.getCfg(FlowConfigCons.TOKEN_NAME);
        if (StringUtils.isNotEmpty(tokenName)) {
            flowConfig.setTokenName(tokenName);
        }

        // 设置id生成器类型
        String keyType = FrameInvoker.getCfg(FlowConfigCons.KEY_TYPE);
        if (StringUtils.isNotEmpty(keyType)) {
            flowConfig.setKeyType(keyType);
        }

        // 设置逻辑删除
        calLogicDelete(flowConfig);

        // 设置租户模式
        flowConfig.setTenantHandlerPath(FrameInvoker.getCfg(FlowConfigCons.TENANT_HANDLER_PATH));
        FlowFactory.initTenantHandler(flowConfig.getTenantHandlerPath());

        // 设置数据填充处理类
        flowConfig.setDataFillHandlerPath(FrameInvoker.getCfg(FlowConfigCons.DATA_FILL_HANDLE_PATH));
        FlowFactory.initDataFillHandler(flowConfig.getDataFillHandlerPath());

        // 设置办理人权限处理类
        FlowFactory.initPermissionHandler();

        // 设置全局监听器
        FlowFactory.initGlobalListener();

        // 设置数据源类型
        flowConfig.setDataSourceType(FrameInvoker.getCfg(FlowConfigCons.DATA_SOURCE_TYPE));
        printBanner(flowConfig);

        // 通过SPI机制
        spiLoad();
        return flowConfig;
    }

    public static void spiLoad() {
        // 通过SPI机制加载json转换策略实现类
        FlowFactory.jsonConvert(ServiceLoaderUtil.loadFirst(JsonConvert.class));
    }

    private static void calLogicDelete(WarmFlow flowConfig) {
        String logicDelete = FrameInvoker.getCfg(FlowConfigCons.LOGIC_DELETE);
        if (ObjectUtil.isStrTrue(logicDelete)) {
            flowConfig.setLogicDelete(ObjectUtil.isStrTrue(logicDelete));
            String logicDeleteValue = FrameInvoker.getCfg(FlowConfigCons.LOGIC_DELETE_VALUE);
            if (StringUtils.isNotEmpty(logicDeleteValue)) {
                flowConfig.setLogicDeleteValue(logicDeleteValue);
            }
            String logicNotDeleteValue = FrameInvoker.getCfg(FlowConfigCons.LOGIC_NOT_DELETE_VALUE);
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

}
