package com.warm.flow.core.constant;

import java.util.regex.Pattern;

/**
 * warm-flow常量
 *
 * @author warm
 */
public class FlowCons {

    /**
     * 是否支持任意跳转
     */
    public static final String SKIP_ANY_Y = "Y";

    public static final String SKIP_ANY_N = "N";

    /**
     * 分隔符
     */
    public static final String splitAt = "@@";

    public static final Pattern listenerPattern = Pattern.compile("^([^()]*)(.*)$");

    /**
     * 权限标识中的发起人标识符，办理过程中进行替换
     */
    public static final String WARMFLOWINITIATOR = "warmFlowInitiator";

}
