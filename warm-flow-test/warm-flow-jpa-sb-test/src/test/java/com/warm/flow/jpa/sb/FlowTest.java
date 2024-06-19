package com.warm.flow.jpa.sb;


import com.warm.flow.core.service.DefService;
import com.warm.flow.core.service.InsService;
import com.warm.flow.core.service.TaskService;
import com.warm.flow.core.test.FlowBaseTest;
import com.warm.flow.jpa.sb.repository.YourEntityRepository;
import com.warm.flow.jpa.sb.service.TransactionalTest;
import com.warm.flow.orm.entity.FlowInstance;
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

    @Resource
    private YourEntityRepository entityRepository;

    @Resource
    private TransactionalTest transactionalTest;


    @Test
    public void transactionalTest() {
        System.out.println("新增前数量：" + entityRepository.findAll().size());
        System.out.println("新增前数量：" + insService.list(new FlowInstance()).size());
        try {
            transactionalTest.transactional();
        } catch (Exception e) {
            System.out.println("异常：" + e);
        }
        System.out.println("新增后数量：" + entityRepository.findAll().size());
        System.out.println("新增后数量：" + insService.list(new FlowInstance()).size());
    }

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
