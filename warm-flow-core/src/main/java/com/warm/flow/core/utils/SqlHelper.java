package com.warm.flow.core.utils;

/**
 * @author minliuhua
 * @description: SQL 辅助类
 * @date: 2023/5/3 15:22
 */
public final class SqlHelper {


    /**
     * 判断数据库操作是否成功
     *
     * @param result 数据库操作返回影响条数
     * @return boolean
     */
    public static boolean retBool(Integer result) {
        return null != result && result >= 1;
    }

    /**
     * 判断数据库操作是否成功
     *
     * @param result 数据库操作返回影响条数
     * @return boolean
     */
    public static boolean retBool(Long result) {
        return null != result && result >= 1;
    }

    /**
     * 返回SelectCount执行结果
     *
     * @param result ignore
     * @return int
     */
    public static long retCount(Long result) {
        return (null == result) ? 0 : result;
    }

}
