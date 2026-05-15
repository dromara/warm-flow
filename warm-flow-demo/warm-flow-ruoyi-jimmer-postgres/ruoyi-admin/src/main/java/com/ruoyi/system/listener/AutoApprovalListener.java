package com.ruoyi.system.listener;

import org.dromara.warm.flow.core.dto.FlowParams;
import org.dromara.warm.flow.core.entity.Task;
import org.dromara.warm.flow.core.enums.SkipType;
import org.dromara.warm.flow.core.listener.Listener;
import org.dromara.warm.flow.core.listener.ListenerVariable;
import org.dromara.warm.flow.core.service.NodeService;
import org.dromara.warm.flow.core.service.TaskService;
import org.dromara.warm.flow.core.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 超时自动审批监听器
 *
 * @author warm
 * @since 2026/3/20
 */
@Component
public class AutoApprovalListener implements Listener {

    @Resource
    private TaskService taskService;

    @Resource
    private NodeService nodeService;

    @Resource
    private ScheduledExecutorService executor;

    private static final Logger log = LoggerFactory.getLogger(AutoApprovalListener.class);

    @Override
    public void notify(ListenerVariable listenerVariable) {
//        // 获取节点
//        Map<String, String> extMap = nodeService.getExt(listenerVariable.getNode());
//        // 获取超时时间
//        String autoApprovalTime = extMap.get("autoApproval");
//        if (StringUtils.isNotEmpty(autoApprovalTime)) {
//            // value为保留两位小数的时间，单位为小时，转成秒，如1.1小时转成3960秒
//            long seconds = (long) (Double.parseDouble(autoApprovalTime) * 60 * 60);
//            // 自动审批不需要校验办理人权限, 默认审批通过
//            FlowParams flowParams = FlowParams.build().ignore(true)
//                    .skipType(SkipType.PASS.getKey())
//                    .message("超时自动审批");
//            // 获取超时后执行动作，通过还是退回
//            if (!SkipType.isPass(extMap.get("autoApproval_skipType"))) {
//                flowParams.skipType(SkipType.REJECT.getKey());
//            }
//            // 通过jdk的定时任务，自动审批
//            Task task = listenerVariable.getTask();
//            executor.schedule(() -> {
//                Long instanceId = task.getInstanceId();
//                // 会签和票签中存在并发问题，添加锁解决
//                synchronized (("会签超时自动审批：" + instanceId).intern()) {
//                    log.info("超时自动审批监听器开始执行......");
//                    // 判断需要超时自动执行的任务，是否已经被主动执行，如果还存在则开始自动执行
//                    Task taskTemp = taskService.getById(task.getId());
//                    if (taskTemp != null) {
//                        taskService.skip(task.getId(), flowParams);
//                        return;
//                    }
//                    log.info("超时自动审批监听器执行结束......");
//                }
//            }, seconds, TimeUnit.SECONDS);
//        }
    }
}
