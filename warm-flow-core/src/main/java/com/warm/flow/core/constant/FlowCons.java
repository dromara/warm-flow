package com.warm.flow.core.constant;

import java.util.regex.Pattern;

/**
 * warm-flow常量
 *
 * @author warm
 */
public class FlowCons {

    /** 是否支持任意跳转 */
    public static final String SKIP_ANY_Y = "Y";

    public static final String SKIP_ANY_N = "N";

    /**
     * 分隔符
     */
    public static final String splitAt = "@@";

    public static final Pattern listenerPattern = Pattern.compile("^([^()]*)(.*)$");

}
