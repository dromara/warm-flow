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
package com.warm.flow.sb.test;


import com.warm.flow.core.service.DefService;
import com.warm.flow.core.service.InsService;
import com.warm.flow.core.service.TaskService;
import com.warm.flow.core.test.FlowBaseTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;


@SpringBootTest
public class FlowTest extends FlowBaseTest {

    @Resource
    private DefService defService;

    @Resource
    private InsService insService;

    @Resource
    private TaskService taskService;

    /**
     * 部署流程
     */
    @Test
    public void deployFlow() throws Exception {
        deployFlow(defService);
    }

    /**
     * 发布流程
     */
    @Test
    public void publish() {
        publish(defService);
    }

    /**
     * 取消流程
     */
    @Test
    public void unPublish() {
        unPublish(defService);
    }

    /**
     * 获取流程定义xml的字符串
     *
     * @return
     */
    @Test
    public void xmlString() {
        xmlString(defService);
    }

    /**
     * 删除流程定义
     */
    @Test
    public void removeDef() {
        removeDef(defService);
    }

    /**
     * 开启流程
     */
    @Test
    public void startFlow() {
        startFlow(insService, taskService);
    }

    /**
     * 删除流程实例
     */
    @Test
    public void removeIns() {
        removeIns(insService);
    }

    /**
     * 办理
     */
    @Test
    public void skipFlow() {
        skipFlow(insService, taskService);
    }

    /**
     * 终止流程实例
     */
    @Test
    public void termination() {
        termination(taskService);
    }

    /**
     * 跳转到指定节点 跳转到结束节点
     */
    @Test
    public void skipAnyNode() {
        skipAnyNode(taskService);
    }

    /**
     * 分页
     */
    @Test
    public void page() {
        page(defService);
    }

    /**
     * 转办
     */
    @Test
    public void transfer() {
        transfer(taskService);
    }

    /**
     * 委派
     */
    @Test
    public void depute() {
        depute(taskService);
    }

    /**
     * 加签
     */
    @Test
    public void addSignature() {
        addSignature(taskService);
    }

    /**
     * 减签
     */
    @Test
    public void reductionSignature() {
        reductionSignature(taskService);
    }
}
