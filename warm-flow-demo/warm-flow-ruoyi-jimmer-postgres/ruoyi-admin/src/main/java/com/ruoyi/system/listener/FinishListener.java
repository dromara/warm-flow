package com.ruoyi.system.listener;

import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.mapper.TestLeaveMapper;
import org.dromara.warm.flow.core.entity.Instance;
import org.dromara.warm.flow.core.listener.Listener;
import org.dromara.warm.flow.core.listener.ListenerVariable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

@Component
public class FinishListener implements Listener {

    @Resource
    private TestLeaveMapper testLeaveMapper;

    private static final Logger log = LoggerFactory.getLogger(FinishListener.class);

    @Override
    public void notify(ListenerVariable listenerVariable) {
        log.info("完成监听器......");
        Instance instance = listenerVariable.getInstance();
        Map<String, Object> variable = listenerVariable.getVariable();
        if (StringUtils.isNotNull(variable)) {
            String businessId = instance.getBusinessId();
            Object businessType = variable.get("businessType");
        }
        log.info("完成监听器结束......");
    }
}
