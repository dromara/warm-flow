package com.ruoyi.system.listener;

import org.dromara.warm.flow.core.listener.Listener;
import org.dromara.warm.flow.core.listener.ListenerVariable;
import org.dromara.warm.flow.core.service.NodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 脚本监听器
 *
 * @author warm
 * @since 2026/3/20
 */
@Component
public class ScriptlListener implements Listener {

    @Resource
    private NodeService nodeService;

    private static final Logger log = LoggerFactory.getLogger(ScriptlListener.class);

    @Override
    public void notify(ListenerVariable listenerVariable) {
        log.info("脚本监听器开始执行......");
//        // 获取节点
//        Map<String, String> extMap = nodeService.getExt(listenerVariable.getNode());
//        // 获取超时时间
//        String script = extMap.get("script");
//        if (StringUtils.isNotEmpty(script)) {
//            // 执行脚本。。。
//            System.out.println("执行脚本：" + script);
//        }
        log.info("脚本监听器执行结束......");
    }
}
