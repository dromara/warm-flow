/*
 *    Copyright 2024-2025, Warm-Flow (290631660@qq.com).
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
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
public class DefFinishListener implements Listener {


    private static final Logger log = LoggerFactory.getLogger(DefFinishListener.class);

    @Resource
    private TestLeaveMapper testLeaveMapper;

    /**
     * 组件办理完成后，通常需要业务表新增或者更新操作
     * 也可以放到业务代码中办理完成后，或者节点监听器
     * @param listenerVariable 监听器变量
     */
    @Override
    public void notify(ListenerVariable listenerVariable) {
        log.info("流程完成监听器......");

        Instance instance = listenerVariable.getInstance();
        Map<String, Object> variable = listenerVariable.getVariable();
        if (StringUtils.isNotNull(variable)) {
            String businessId = instance.getBusinessId();
            Object businessType = variable.get("businessType");
//            /** 如果{@link com.ruoyi.system.service.impl.TestLeaveServiceImpl}中更新了，这里就不用更新了*/
//            // 更新业务数据
//            if ("testLeave".equals(businessType)) {
//                // 可以统一使用一个流程监听器，不同实体类，不同的操作
//                TestLeave testLeave = testLeaveMapper.selectTestLeaveById(businessId);
//                if (ObjectUtil.isNull(testLeave)) {
//                    testLeave = (TestLeave) variable.get("businessData");
//                }
//                testLeave.setNodeCode(instance.getNodeCode());
//                testLeave.setNodeName(instance.getNodeName());
//                testLeave.setNodeType(instance.getNodeType());
//                testLeave.setFlowStatus(instance.getFlowStatus());
//                // 如果没有实例id，说明是新增
//                if (ObjectUtil.isNull(testLeave.getInstanceId())) {
//                    testLeave.setInstanceId(instance.getId());
//                    testLeaveMapper.insertTestLeave(testLeave);
//                    testLeave.setCreateTime(DateUtils.getNowDate());
//                    // 新增抄送人方法，也可发送通知
//                    if (StringUtils.isNotNull(testLeave.getAdditionalHandler())) {
//                        List<User> users = FlowEngine.userService().structureUser(instance.getId()
//                                , testLeave.getAdditionalHandler(), "4");
//                        FlowEngine.userService().saveBatch(users);
//                    }
//                } else {
//                    testLeave.setUpdateTime(DateUtils.getNowDate());
//                    testLeaveMapper.updateTestLeave(testLeave);
//                }
//            }
        }

        log.info("流程完成监听器结束......");
    }
}
