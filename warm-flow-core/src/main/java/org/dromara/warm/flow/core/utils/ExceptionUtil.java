package org.dromara.warm.flow.core.utils;


import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * 错误信息处理类。
 *
 * @author warm
 */
public class ExceptionUtil {
    /**
     * 获取exception的详细错误信息。
     */
    public static String getExceptionMessage(Throwable e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw, true));
        return sw.toString();
    }

    /**
     * 处理消息是否显示中文
     */
    public static String handleMsg(String msg, Exception e) {
        if (StringUtils.isEmpty(msg)) {
            return e.getMessage();
        } else {
            return msg + ": " + e.getMessage();
        }
    }
}
