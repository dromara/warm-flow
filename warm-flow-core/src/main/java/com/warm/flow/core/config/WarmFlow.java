package com.warm.flow.core.config;

import com.warm.flow.core.FlowFactory;
import com.warm.flow.core.constant.FlowConfigCons;
import com.warm.flow.core.invoker.FrameInvoker;
import com.warm.tools.utils.ObjectUtil;
import com.warm.tools.utils.StringUtils;

/**
 * WarmFlow属性配置文件
 *
 * @author warm
 */
public class WarmFlow {
    private boolean banner = true;

    private String dataFillHandlerPath;


    public boolean isBanner() {
        return banner;
    }

    public void setBanner(boolean banner) {
        this.banner = banner;
    }

    public String getDataFillHandlerPath() {
        return dataFillHandlerPath;
    }

    public void setDataFillHandlerPath(String dataFillHandlerPath) {
        this.dataFillHandlerPath = dataFillHandlerPath;
    }

    public static WarmFlow init() {
        WarmFlow flowConfig = new WarmFlow();
        String banner = FrameInvoker.getCfg(FlowConfigCons.BANNER);
        if (StringUtils.isNotEmpty(banner)) {
            flowConfig.setBanner(ObjectUtil.isStrTrue(banner));
        }
        flowConfig.setDataFillHandlerPath(FrameInvoker.getCfg(FlowConfigCons.DATAFILLHANDLEPATH));
        printBanner(flowConfig);
        return flowConfig;
    }

    private static void printBanner(WarmFlow flowConfig) {
        if (flowConfig.isBanner()) {
            System.out.println("\n" +
                    "▄     ▄                      ▄▄▄▄▄▄   ▄                \n" +
                    "█  █  █  ▄▄▄    ▄ ▄▄  ▄▄▄▄▄  █        █     ▄▄▄  ▄     ▄\n" +
                    "▀ █▀█ █ ▀   █   █▀  ▀ █ █ █  █▄▄▄▄▄   █    █▀ ▀█ ▀▄ ▄ ▄▀\n" +
                    " ██ ██▀ ▄▀▀▀█   █     █ █ █  █        █    █   █  █▄█▄█\n" +
                    " █   █  ▀▄▄▀█   █     █ █ █  █        ▀▄▄  ▀█▄█▀   █ █");
        }
    }
}
