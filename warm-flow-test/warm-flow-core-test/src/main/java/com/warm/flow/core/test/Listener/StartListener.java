package com.warm.flow.core.test.Listener;

import com.warm.flow.core.entity.Instance;
import com.warm.flow.core.listener.Listener;
import com.warm.flow.core.listener.ListenerVariable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class StartListener implements Listener {


    private static final Logger log = LoggerFactory.getLogger(StartListener.class);

    @Override
    public void notify(ListenerVariable variable) {
        log.info("开始监听器");
        Instance instance = variable.getInstance();
        Map<String, Object> variableMap = variable.getVariable();
        log.info("开始监听器结束;{}", "开启流程完成");
    }
}
