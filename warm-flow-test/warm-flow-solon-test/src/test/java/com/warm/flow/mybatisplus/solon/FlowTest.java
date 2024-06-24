package com.warm.flow.mybatisplus.solon;


import com.warm.flow.core.service.DefService;
import com.warm.flow.core.service.InsService;
import com.warm.flow.core.service.TaskService;
import com.warm.flow.core.test.FlowBaseTest;
import org.junit.jupiter.api.Test;
import org.noear.solon.annotation.Inject;
import org.noear.solon.test.SolonTest;

@SolonTest(value = App.class)
public class FlowTest extends FlowBaseTest {

    @Inject
    private DefService defService;

    @Inject
    private InsService insService;

    @Inject
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
