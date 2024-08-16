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
package com.warm.flow.core.test.Listener;

import com.warm.flow.core.entity.Instance;
import com.warm.flow.core.listener.Listener;
import com.warm.flow.core.listener.ListenerVariable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class PermissionListener implements Listener {

    private static final Logger log = LoggerFactory.getLogger(PermissionListener.class);

    @Override
    public void notify(ListenerVariable variable) {
        log.info("节点权限监听器开始");
        Instance instance = variable.getInstance();
//        List<NodePermission> nodePermissionList = new ArrayList<>();
//        // 动态传入组件权限标识
//        NodePermission nodePermission1 = new NodePermission();
//        NodePermission nodePermission2 = new NodePermission();
//        NodePermission nodePermission3 = new NodePermission();
//        nodePermission1.setNodeCode("1");
//        nodePermission1.setPermissionFlag("role:1,role:2,role:100");
//        nodePermission2.setNodeCode("2");
//        nodePermission2.setPermissionFlag("role:1,role:2,role:100");
//        nodePermission3.setNodeCode("3");
//        nodePermission3.setPermissionFlag("role:1,role:2,role:100,role:101");
//
//        nodePermissionList.add(nodePermission1);
//        nodePermissionList.add(nodePermission2);
//        nodePermissionList.add(nodePermission3);
//        variable.setNodePermissionList(nodePermissionList);
        Map<String, Object> variableMap = variable.getVariable();
        log.info("节点权限监听器结束");
    }
}
