package com.warm.flow.core.config;

import com.warm.flow.core.constant.FlowConfigCons;
import com.warm.flow.core.invoker.FrameInvoker;
import com.warm.tools.utils.ObjectUtil;
import com.warm.tools.utils.StringUtils;

/**
 * WarmFlow属性配置文件
 *
 * @author warm
 */
public class WarmFlowConfig {
    private boolean banner = true;


    public boolean isBanner() {
        return banner;
    }

    public WarmFlowConfig setBanner(boolean banner) {
        this.banner = banner;
        return this;
    }

    public static WarmFlowConfig init() {
        WarmFlowConfig flowConfig = new WarmFlowConfig();
        String banner = FrameInvoker.getCfg(FlowConfigCons.BANNER);
        if (StringUtils.isNotEmpty(banner)) {
            flowConfig.setBanner(ObjectUtil.isStrTrue(banner));
        }
        printBanner(flowConfig);
        return flowConfig;
    }

    private static void printBanner(WarmFlowConfig flowConfig) {
        if (flowConfig.isBanner()) {
            System.out.println("▄     ▄                      ▄▄▄▄▄▄ ▀▀█                 \n" +
                    "█  █  █  ▄▄▄    ▄ ▄▄  ▄▄▄▄▄  █        █     ▄▄▄  ▄     ▄\n" +
                    "▀ █▀█ █ ▀   █   █▀  ▀ █ █ █  █▄▄▄▄▄   █    █▀ ▀█ ▀▄ ▄ ▄▀\n" +
                    " ██ ██▀ ▄▀▀▀█   █     █ █ █  █        █    █   █  █▄█▄█\n" +
                    " █   █  ▀▄▄▀█   █     █ █ █  █        ▀▄▄  ▀█▄█▀   █ █");
        }
    }
}
