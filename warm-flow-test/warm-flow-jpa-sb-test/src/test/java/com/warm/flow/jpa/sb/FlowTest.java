package com.warm.flow.jpa.sb;


import com.warm.flow.core.dto.FlowParams;
import com.warm.flow.core.entity.Definition;
import com.warm.flow.core.entity.Instance;
import com.warm.flow.core.enums.SkipType;
import com.warm.flow.core.service.DefService;
import com.warm.flow.core.service.InsService;
import com.warm.flow.core.service.TaskService;
import com.warm.flow.core.utils.page.Page;
import com.warm.flow.jpa.sb.repository.YourEntityRepository;
import com.warm.flow.jpa.sb.service.TransactionalTest;
import com.warm.flow.orm.entity.FlowDefinition;
import com.warm.flow.orm.entity.FlowInstance;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.List;


@SpringBootTest
public class FlowTest {

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


    public FlowParams getUser() {
        return FlowParams.build().flowCode("leaveFlow-serial-test")
                .createBy("user:1")
                .skipType(SkipType.PASS.getKey())
                .permissionFlag(Arrays.asList("role:1", "role:2"));
    }

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

    @Test
    public void deployFlow() throws Exception {
        String path = "/Users/minliuhua/Desktop/mdata/file/IdeaProjects/min/warm-flow/warm-flow-test/warm-flow-core-test/src/main/resources/leaveFlow-serial.xml";
        System.out.println("已部署流程的id：" + defService.importXml(new FileInputStream(path)).getId());
    }

    @Test
    public void publish() {
        defService.publish(1234277429141442560L);
    }

    @Test
    @Transactional(rollbackFor = Exception.class)
    public void startFlow() {
        System.out.println("已开启的流程实例id：" + insService.start("2", getUser()).getId());
    }

    @Test
    public void skipFlow() {
        // 通过实例id流转
        Instance instance = insService.skipByInsId(1234277672293634048L, getUser().skipType(SkipType.PASS.getKey())
                .permissionFlag(Arrays.asList("role:1", "role:2")));
        System.out.println("流转后流程实例：" + instance.toString());

//        // 通过任务id流转
//        Instance instance = taskService.skip(1219286332145274880L, getUser().skipType(SkipType.PASS.getKey())
//                .permissionFlag(Arrays.asList("role:1", "role:2")));
//        System.out.println("流转后流程实例：" + instance.toString());
    }

    @Test
    public void termination() {
        // 终止流程实例
        FlowParams flowParams = new FlowParams();
        flowParams.message("终止流程").createBy("user:1");
        taskService.termination(1232001396254052352L, flowParams);
    }

    @Test
    public void skipAnyNode() {
        // 跳转到指定节点
        Instance instance = taskService.skip(1219286332145274880L, getUser().skipType(SkipType.PASS.getKey())
                .permissionFlag(Arrays.asList("role:1", "role:2")));
        System.out.println("流转后流程实例：" + instance.toString());
    }

    @Test
    public void assignee() {
        // 转办
        System.out.println("转办：" + taskService.transfer(1239301524417548289L, getUser()));
    }

    @Test
    public void page(){
        FlowDefinition flowDefinition = new FlowDefinition();
        Page<Definition> page = Page.pageOf(1,10);
        page = defService.orderByCreateTime().desc().page(flowDefinition, page);
        List<Definition> list = page.getList();
        for (Definition definition : list) {
            System.out.println(definition);
        }
    }

    /**
     * 加减签
     */
    @Test
    public void signature(){
        FlowParams flowParams = FlowParams.build().additionalHandler(Arrays.asList("role:102", "role:1"))
                .permissionFlag(Arrays.asList("role:1", "role:2", "user:1"))
                .skipType(SkipType.PASS.getKey()).createBy("user:1");
        taskService.signature(1234425333428654080L, flowParams);
    }
    /**
     * 委派
     */
    @Test
    public void depute(){
        FlowParams flowParams = FlowParams.build().additionalHandler(Arrays.asList("role:103", "role:3"))
                .permissionFlag(Arrays.asList("role:3", "role:102", "user:1"))
                .skipType(SkipType.PASS.getKey()).createBy("user:1");
        taskService.depute(1243308524025548800L, flowParams);
    }
    /**
     * 转办
     */
    @Test
    public void transfer(){
        FlowParams flowParams = FlowParams.build().additionalHandler(Arrays.asList("role:103", "role:3"))
                .permissionFlag(Arrays.asList("role:3", "role:102", "user:1"))
                .skipType(SkipType.PASS.getKey()).createBy("user:1");
        taskService.transfer(1243308524025548800L, flowParams, false, true);
    }

}
