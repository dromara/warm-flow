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
package com.warm.flow.core.test;


import com.warm.flow.core.FlowFactory;
import com.warm.flow.core.dto.FlowParams;
import com.warm.flow.core.entity.Definition;
import com.warm.flow.core.entity.Instance;
import com.warm.flow.core.enums.SkipType;
import com.warm.flow.core.service.DefService;
import com.warm.flow.core.service.InsService;
import com.warm.flow.core.service.TaskService;
import com.warm.flow.core.utils.page.Page;

import java.io.FileInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class FlowBaseTest {

    public String getFlowCode() {
        return "serial55";
    }

    public Long getInsId() {
        return 1272533054795157504L;
    }

    public Long getTaskId() {
        return 1266424585176354816L;
    }

    public String getBusinessId() {
        return "3";
    }

    public FlowParams getUser() {
        return FlowParams.build().flowCode(getFlowCode())
                .handler("1")
                .skipType(SkipType.PASS.getKey())
                .permissionFlag(Arrays.asList("role:1", "role:2"));
    }

    /**
     * 部署流程
     */
    public void deployFlow(DefService defService) throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();
        URL resource = classLoader.getResource(".");
        String path = resource.getPath();
        int i = path.indexOf("/warm-flow-test/");
        String newPath = path.substring(0, i + 16) + "warm-flow-core-test/src/main/resources/leaveFlow-serial-内部测试用.xml";

        System.out.println("已部署流程的id：" + defService.importXml(new FileInputStream(newPath)).getId());
    }

    public Long getTestDefId(DefService defService) {
        ArrayList<String> flowCodeList = new ArrayList<>();
        flowCodeList.add(getFlowCode());
        return defService.queryByCodeList(flowCodeList).stream().findFirst().map(Definition::getId).orElse(0L);
    }

    /**
     * 发布流程
     */
    public void publish(DefService defService) {

        defService.publish(getTestDefId(defService));
    }

    /**
     * 激活流程
     */
    public void active(DefService defService) {
        defService.active(getTestDefId(defService));
    }

    /**
     * 挂起流程
     */
    public void unActive(DefService defService){
        defService.unActive(getTestDefId(defService));
    }

    /**
     * 取消流程
     */
    public void unPublish(DefService defService) {
        defService.unPublish(getTestDefId(defService));
    }

    /**
     * 获取流程定义xml的字符串
     */
    public void xmlString(DefService defService) {
        System.out.println("流程定义xml的字符串：" + defService.xmlString(getTestDefId(defService)));
    }

    /**
     * 删除流程定义
     */
    public void removeDef(DefService defService) {
        defService.removeDef(Collections.singletonList(1270719853770182658L));
    }

    /**
     * 开启流程
     */
    public void startFlow(InsService insService, TaskService taskService) {
        Instance instance = insService.start(getBusinessId(), getUser());
        System.out.println("已开启的流程实例id：" + instance.getId());
        taskService.list(FlowFactory.newTask().setInstanceId(instance.getId()))
                .forEach(task -> System.out.println("流转后任务id实例：" + task.getId()));
    }

    /**
     * 删除流程实例
     */
    public void removeIns(InsService insService) {
        insService.remove(Collections.singletonList(2L));
    }

    /**
     * 激活流程实例
     */
    public void activeIns(InsService insService) {
        insService.active(getInsId());
    }

    /**
     * 挂起流程实例
     */
    public void unActiveIns(InsService insService) {
        insService.unActive(getInsId());
    }

    /**
     * 办理
     */
    public void skipFlow(InsService insService, TaskService taskService) {
        // 通过实例id流转
        Instance instance = insService.skipByInsId(getInsId(), getUser().skipType(SkipType.PASS.getKey())
                .permissionFlag(Arrays.asList("role:1", "role:2")));
        System.out.println("流转后流程实例：" + instance.toString());

//        // 通过任务id流转
//        Instance instance = taskService.skip(getTaskId(), getUser().skipType(SkipType.PASS.getKey())
//                .permissionFlag(Arrays.asList("role:1", "role:2")));
        System.out.println("流转后流程实例：" + instance.toString());
        taskService.list(FlowFactory.newTask().setInstanceId(instance.getId()))
                .forEach(task -> System.out.println("流转后任务id实例：" + task.getId()));
    }

    /**
     * 终止流程实例
     */
    public void termination(TaskService taskService) {

        FlowParams flowParams = new FlowParams();
        flowParams.message("终止流程").handler("1");
        taskService.termination(1260200517360029696L, flowParams);
    }

    /**
     * 跳转到指定节点 跳转到结束节点
     */
    public void skipAnyNode(TaskService taskService) {
        Instance instance = taskService.skip(1260200765054652416L, getUser().skipType(SkipType.PASS.getKey())
                .permissionFlag(Arrays.asList("role:1", "role:2")).nodeCode("5"));
        System.out.println("流转后流程实例：" + instance.toString());
    }

    /**
     * 分页
     */
    public void page(DefService defService){
        Definition flowDefinition = FlowFactory.newDef();
        Page<Definition> page = Page.pageOf(1, 10);
        page = defService.orderByCreateTime().desc().page(flowDefinition, page);
        List<Definition> list = page.getList();
        list.forEach(System.out::println);
    }

    /**
     * 转办
     */
    public void transfer(TaskService taskService) {
         taskService.transfer(getTaskId()
                 , "1"
                 , Arrays.asList("role:1", "role:2", "user:1")
                 , Arrays.asList("2", "3")
                 ,"转办");
    }

    /**
     * 委派
     */
    public void depute(TaskService taskService){
        taskService.transfer(getTaskId()
                , "1"
                , Arrays.asList("role:1", "role:2", "user:1")
                , Arrays.asList("2", "3")
                ,"委派");
    }

    /**
     * 加签
     */
    public void addSignature(TaskService taskService){
        taskService.addSignature(getTaskId()
                , "1"
                , Arrays.asList("role:1", "role:2", "user:1")
                , Arrays.asList("2", "3")
                ,"加签");
    }

    /**
     * 减签
     */
    public void reductionSignature(TaskService taskService){
        taskService.reductionSignature(getTaskId()
                , "1"
                , Arrays.asList("role:1", "role:2", "user:1")
                , Arrays.asList("2", "3")
                ,"减签");
    }

}
