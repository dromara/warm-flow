package com.warm.flow.mybatis.sb;


import com.warm.flow.core.listener.Listener;
import com.warm.flow.core.listener.ListenerVariable;
import com.warm.flow.core.service.DefService;
import com.warm.flow.core.service.InsService;
import com.warm.flow.core.service.TaskService;
import com.warm.flow.core.test.FlowBaseTest;
import com.warm.flow.core.utils.ListenerUtil;
import com.warm.flow.orm.entity.FlowNode;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.HashMap;


@SpringBootTest
public class FlowTest extends FlowBaseTest{

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
     * 开启流程
     */
    @Test
    public void startFlow() {
        startFlow(insService);
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
    public void page(){
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
    public void depute(){
        depute(taskService);
    }

    /**
     * 加签
     */
    @Test
    public void addSignature(){
        addSignature(taskService);
    }

    /**
     * 减签
     */
    @Test
    public void reductionSignature(){
        reductionSignature(taskService);
    }

    /**
     * 测试监听器参数
     */
    @Test
    public void testListener() {
        String lisType = Listener.LISTENER_PERMISSION;
        FlowNode flowNode = new FlowNode();
        flowNode.setListenerType(lisType);
        // 流程设计时传入的参数, 应该不会用到。防止影响到之前发布的版本, 顺带加上
        flowNode.setListenerPath("com.warm.flow.core.test.Listener.PermissionListener({\\\"name\\\": \\\"John Doe\\\", \\\"age\\\": 30})");
        HashMap<String, Object> variable = new HashMap<>();
        variable.put("123", "{\\\"name\\\": \\\"John Doe\\\", \\\"age\\\": 30}");
        ListenerVariable listenerVariable = new ListenerVariable();
        listenerVariable.setNode(flowNode);
        listenerVariable.setVariable(variable);
        ListenerUtil.executeListener(listenerVariable, lisType);
    }

}
