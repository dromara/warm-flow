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

import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.flow.utils.PermissionsUtil;
import org.dromara.warm.flow.core.dto.FlowParams;
import org.dromara.warm.flow.core.listener.Listener;
import org.dromara.warm.flow.core.listener.ListenerVariable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DefStartListener implements Listener {


    private static final Logger log = LoggerFactory.getLogger(DefStartListener.class);

    /**
     * 在开始监听器中预先设置办理人id、所拥有的权限等操作，减少重复代码
     * 也可以放到业务代码中办理前设置，或者节点监听器
     * @param listenerVariable 监听器变量
     */
    @Override
    public void notify(ListenerVariable listenerVariable) {
        log.info("流程开始监听器......");

        /** 如果{@link com.ruoyi.system.service.impl.TestLeaveServiceImpl}中传值了，这里就不用设置*/
        FlowParams flowParams = listenerVariable.getFlowParams();
        LoginUser user = SecurityUtils.getLoginUser();
        // 设置当前办理人id
        flowParams.handler(user.getUser().getUserId().toString());

        // 设置办理人所拥有的权限，比如角色、部门、用户等
        List<String> permissionList = flowParams.getPermissionFlag();
        if (StringUtils.isEmpty(permissionList)) {
            permissionList = new ArrayList<>();
        }

        permissionList.addAll(PermissionsUtil.getPermissions());
        flowParams.permissionFlag(permissionList);

        log.info("流程开始监听器结束......");
    }
}
