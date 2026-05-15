package com.ruoyi.system.listener;

import org.dromara.warm.flow.core.entity.Instance;
import org.dromara.warm.flow.core.listener.Listener;
import org.dromara.warm.flow.core.listener.ListenerVariable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class StartListener implements Listener {

    private static final Logger log = LoggerFactory.getLogger(StartListener.class);

    @Override
    public void notify(ListenerVariable listenerVariable) {
        log.info("开始监听器......");
        Instance instance = listenerVariable.getInstance();
        Map<String, Object> variableMap = listenerVariable.getVariable();

        log.info("开始监听器结束......");
    }
}
