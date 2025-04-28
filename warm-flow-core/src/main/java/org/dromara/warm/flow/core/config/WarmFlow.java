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
import org.dromara.warm.flow.core.FlowEngine;
import org.dromara.warm.flow.core.enums.ChartStatus;
import org.dromara.warm.flow.core.json.JsonConvert;
import org.dromara.warm.flow.core.utils.ServiceLoaderUtil;

import java.io.Serializable;
import java.util.List;

/**
 * WarmFlow属性配置文件
 *
 * @author warm
 * @see <a href="https://warm-flow.dromara.org/master/primary/config.html">文档地址</a>
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

    /**
     * 流程状态对应的三原色
     */
    private List<String> chartStatusColor;

    public void init() {
        // 设置租户模式
        FlowEngine.initTenantHandler(this.getTenantHandlerPath());

        // 设置数据填充处理类
        FlowEngine.initDataFillHandler(this.getDataFillHandlerPath());

        // 设置办理人权限处理类
        FlowEngine.initPermissionHandler();

        // 设置全局监听器
        FlowEngine.initGlobalListener();

        // 打印banner图
        printBanner();

        // 初始化流程状态对应的自定义三原色
        ChartStatus.initCustomColor(this.getChartStatusColor());

        // 通过SPI机制
        spiLoad();

    }

    public void spiLoad() {
        // 通过SPI机制加载json转换策略实现类
        FlowEngine.jsonConvert = ServiceLoaderUtil.loadFirst(JsonConvert.class);
    }

    private void printBanner() {
        if (this.isBanner()) {
            System.out.println("\n" +
                    "   ▄     ▄                             ▄▄▄▄▄▄   ▄                     \n" +
                    "   █  █  █  ▄▄▄    ▄ ▄▄  ▄▄▄▄▄         █        █     ▄▄▄  ▄     ▄    \n" +
                    "   ▀ █▀█ █ ▀   █   █▀  ▀ █ █ █  ▄▄▄▄▄  █▄▄▄▄▄   █    █▀ ▀█ ▀▄ ▄ ▄▀    \n" +
                    "    ██ ██▀ ▄▀▀▀█   █     █ █ █         █        █    █   █  █▄█▄█     \n" +
                    "    █   █  ▀▄▄▀█   █     █ █ █         █        █▄▄  ▀█▄█▀   █ █      \n" +
                    "\n" +
                    "\033[32m   :: Warm-Flow ::     (v" + WarmFlow.class.getPackage()
                    .getImplementationVersion() + ")\033[0m\n");
        }
    }

}
