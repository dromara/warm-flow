package com.warm.flow.core.listener;

import java.io.Serializable;

public interface Listener extends Serializable {

    String LISTENER_START = "start";
    String LISTENER_END = "finish";
    String LISTENER_ASSIGNMENT = "assignment";

    String LISTENER_PERMISSION = "permission";

    void notify(ListenerVariable variable);
}
