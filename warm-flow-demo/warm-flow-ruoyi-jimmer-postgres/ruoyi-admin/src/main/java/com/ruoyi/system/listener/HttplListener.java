package com.ruoyi.system.listener;

import org.dromara.warm.flow.core.listener.Listener;
import org.dromara.warm.flow.core.listener.ListenerVariable;
import org.dromara.warm.flow.core.service.NodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 远程请求监听器
 *
 * @author warm
 * @since 2026/3/20
 */
@Component
public class HttplListener implements Listener {

    @Resource
    private NodeService nodeService;

    private static final Logger log = LoggerFactory.getLogger(HttplListener.class);

    @Override
    public void notify(ListenerVariable listenerVariable) {
        log.info("远程请求监听器开始执行......");
//        // 获取节点
//        Map<String, String> extMap = nodeService.getExt(listenerVariable.getNode());
//        // 获取超时时间
//        String httpUrl = extMap.get("httpUrl");
//        if (StringUtils.isNotEmpty(httpUrl)) {
//            String result = HttpUtils.sendGet(httpUrl);
//            if (StringUtils.isNotEmpty(result)) {
//                log.info("请求结果：{}", result);
//            }
//        }
        log.info("远程请求监听器执行结束......");
    }
}
