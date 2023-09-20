package com.warm.flow.core.enums;

import com.warm.tools.utils.ObjectUtil;

/**
 * @author minliuhua
 * @description: 结点类型
 * @date: 2023/3/31 12:16
 */
public enum NodeType {
    /**
     * 开始结点
     */
    START(0, "start"),
    /**
     * 中间结点
     */
    BETWEEN(1, "between"),
    /**
     * 结束结点
     */
    END(2, "end"),

    /**
     * 互斥网关
     */
    SERIAL(3, "serial"),

    /**
     * 并行网关
     */
    PARALLEL(4, "parallel");

    private Integer key;
    private String value;

    private NodeType(Integer key, String value) {
        this.key = key;
        this.value = value;
    }

    public Integer getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public static Integer getKeyByValue(String value) {
        for (NodeType item : NodeType.values()) {
            if (item.getValue().equals(value)) {
                return item.getKey();
            }
        }
        return null;
    }

    public static String getValueByKey(Integer Key) {
        for (NodeType item : NodeType.values()) {
            if (item.getKey().equals(Key)) {
                return item.getValue();
            }
        }
        return null;
    }

    /**
     * 判断是否开始节点
     *
     * @param Key
     * @return
     */
    public static Boolean isStart(Integer Key) {
        return ObjectUtil.isNotNull(Key) && (NodeType.START.getKey().equals(Key));
    }

    /**
     * 判断是否中间节点
     *
     * @param Key
     * @return
     */
    public static Boolean isBetween(Integer Key) {
        return ObjectUtil.isNotNull(Key) && (NodeType.BETWEEN.getKey().equals(Key));
    }

    /**
     * 判断是否结束节点
     *
     * @param Key
     * @return
     */
    public static Boolean isEnd(Integer Key) {
        return ObjectUtil.isNotNull(Key) && (NodeType.END.getKey().equals(Key));
    }

    /**
     * 判断是否网关节点
     *
     * @param Key
     * @return
     */
    public static Boolean isGateWay(Integer Key) {
        return ObjectUtil.isNotNull(Key) && (NodeType.SERIAL.getKey().equals(Key)
                || NodeType.PARALLEL.getKey().equals(Key));
    }

    /**
     * 判断是否串行网关节点
     *
     * @param Key
     * @return
     */
    public static Boolean isGateWaySerial(Integer Key) {
        return ObjectUtil.isNotNull(Key) && NodeType.SERIAL.getKey().equals(Key);
    }

    /**
     * 判断是否并行网关节点
     *
     * @param Key
     * @return
     */
    public static Boolean isGateWayParallel(Integer Key) {
        return ObjectUtil.isNotNull(Key) && NodeType.PARALLEL.getKey().equals(Key);
    }
}
