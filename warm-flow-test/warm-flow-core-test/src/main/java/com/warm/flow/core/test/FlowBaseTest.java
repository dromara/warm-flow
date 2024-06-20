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
import java.util.Arrays;
import java.util.List;


public class FlowBaseTest {

    public FlowParams getUser() {
        return FlowParams.build().flowCode("leaveFlow-serial1")
                .handler("1")
                .skipType(SkipType.PASS.getKey())
                .additionalHandler(Arrays.asList("role:100", "role:101"))
                .permissionFlag(Arrays.asList("role:1", "role:2"));
    }

    /**
     * 部署流程
     */
    public void deployFlow(DefService defService) throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();
        URL resource = classLoader.getResource("");
        String path = resource.getPath();
        int i = path.indexOf("/warm-flow-test/");
        String newPath = path.substring(0, i + 16) + "warm-flow-core-test/src/main/resources/leaveFlow-serial1.xml";

        System.out.println("已部署流程的id：" + defService.importXml(new FileInputStream(newPath)).getId());
    }

    /**
     * 发布流程
     */
    public void publish(DefService defService) {
        defService.publish(1253087350804844550L);
    }

    /**
     * 开启流程
     */
    public void startFlow(InsService insService) {
        System.out.println("已开启的流程实例id：" + insService.start("2", getUser()).getId());
    }

    /**
     * 办理
     */
    public void skipFlow(InsService insService, TaskService taskService) {
        // 通过实例id流转
        Instance instance = insService.skipByInsId(1239250784500191232L, getUser().skipType(SkipType.PASS.getKey())
                .permissionFlag(Arrays.asList("role:1", "role:2")));
        System.out.println("流转后流程实例：" + instance.toString());

//        // 通过任务id流转
//        Instance instance = taskService.skip(1219286332145274880L, getUser().skipType(SkipType.PASS.getKey())
//                .permissionFlag(Arrays.asList("role:1", "role:2")));
//        System.out.println("流转后流程实例：" + instance.toString());
    }

    /**
     * 终止流程实例
     */
    public void termination(TaskService taskService) {
        FlowParams flowParams = new FlowParams();
        flowParams.message("终止流程").handler("1");
        taskService.termination(1239251891997773824L, flowParams);
    }

    /**
     * 跳转到指定节点 跳转到结束节点
     */
    public void skipAnyNode(TaskService taskService) {
        Instance instance = taskService.skip(1239251185018474496L, getUser().skipType(SkipType.PASS.getKey())
                .permissionFlag(Arrays.asList("role:1", "role:2")).nodeCode("9edc9b26-cab4-4fd4-9a30-c89f11626911"));
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
         taskService.transfer(1239301524417548289L
                 , "1"
                 , Arrays.asList("role:1", "role:2", "user:1")
                 , Arrays.asList("2", "3")
                 ,"转办");
    }

    /**
     * 委派
     */
    public void depute(TaskService taskService){
        taskService.transfer(1243308524025548800L
                , "1"
                , Arrays.asList("role:1", "role:2", "user:1")
                , Arrays.asList("2", "3")
                ,"委派");
    }

    /**
     * 加签
     */
    public void addSignature(TaskService taskService){
        taskService.transfer(1234425333428654080L
                , "1"
                , Arrays.asList("role:1", "role:2", "user:1")
                , Arrays.asList("2", "3")
                ,"加签");
    }

    /**
     * 减签
     */
    public void reductionSignature(TaskService taskService){
        taskService.transfer(1234425333428654080L
                , "1"
                , Arrays.asList("role:1", "role:2", "user:1")
                , Arrays.asList("2", "3")
                ,"减签");
    }

}
