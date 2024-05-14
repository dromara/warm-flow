package com.warm.flow.core.config;

import com.warm.flow.core.constant.FlowConfigCons;
import com.warm.flow.core.constant.FlowCons;
import com.warm.flow.core.invoker.FrameInvoker;
import com.warm.tools.utils.ObjectUtil;
import com.warm.tools.utils.StringUtils;

import java.util.List;

/**
 * WarmFlow属性配置文件
 *
 * @author warm
 */
public class WarmFlow {
    /**
     * 启动banner
     */
    private boolean banner = true;

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
     * 数据库类型
     */
    private String dataSourceType;

    public static WarmFlow init() {
        WarmFlow flowConfig = new WarmFlow();
        // 设置banner
        String banner = FrameInvoker.getCfg(FlowConfigCons.BANNER);
        if (StringUtils.isNotEmpty(banner)) {
            flowConfig.setBanner(ObjectUtil.isStrTrue(banner));
        }

        // 设置逻辑删除
        setLogicDelete(flowConfig);

        // 设置租户模式
        flowConfig.setTenantHandlerPath(FrameInvoker.getCfg(FlowConfigCons.TENANTHANDLERPATH));

        // 设置数据填充处理类
        flowConfig.setDataFillHandlerPath(FrameInvoker.getCfg(FlowConfigCons.DATAFILLHANDLEPATH));

        // 设置数据库类型
        setDataSourceType(flowConfig);
        printBanner(flowConfig);
        return flowConfig;
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

    private static void setDataSourceType(WarmFlow flowConfig) {
        String jdbcUrl = FrameInvoker.getCfg(FlowCons.JDBC_URL);

        if (StringUtils.isEmpty(jdbcUrl)) {
            flowConfig.setDataSourceType("h2");
        } else {
            List<Integer> indexList = StringUtils.findStrIndex(jdbcUrl, FlowCons.COLON);
            String dataSourceType = jdbcUrl.substring(indexList.get(0) + 1, indexList.get(1));
            flowConfig.setDataSourceType(dataSourceType);
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

    public boolean isBanner() {
        return banner;
    }

    public void setBanner(boolean banner) {
        this.banner = banner;
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
