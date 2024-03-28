package com.warm.flow.core.constant;

import java.util.regex.Pattern;

/**
 * warm-flow常量
 *
 * @author warm
 */
public class ListenerCons {
    /**
     * 分隔符
     */
    public static final String splitAt = "@@";

    public static final Pattern listenerPattern = Pattern.compile("^([^()]*)(.*)$");

}
