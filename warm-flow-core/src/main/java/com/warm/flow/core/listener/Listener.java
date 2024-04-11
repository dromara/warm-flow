package com.warm.flow.core.listener;

import java.io.Serializable;

public interface Listener extends Serializable {

    /**
     * 创建监听器，任务创建时执行
     */
    String LISTENER_CREATE = "create";

    /**
     * 开始监听器，任务开始办理时执行
     */
    String LISTENER_START = "start";

    /**
     * 结束监听器，当前任务完成后执行
     */
    String LISTENER_END = "finish";

    /**
     * 分派办理人监听器，分派后执行
     */
    String LISTENER_ASSIGNMENT = "assignment";

    /**
     * 权限监听器，办理任务动态设置权限
     */
    String LISTENER_PERMISSION = "permission";

    void notify(ListenerVariable variable);
}
