package com.warm.flow.mybatisplus.sb;


import com.warm.flow.core.dto.FlowParams;
import com.warm.flow.core.entity.Instance;
import com.warm.flow.core.enums.SkipType;
import com.warm.flow.core.service.DefService;
import com.warm.flow.core.service.InsService;
import com.warm.flow.core.service.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.FileInputStream;
import java.util.Arrays;


@SpringBootTest
public class FlowTest {

    @Resource
    private DefService defService;

    @Resource
    private InsService insService;

    @Resource
    private TaskService taskService;

    public FlowParams getUser() {
        return FlowParams.build().flowCode("leaveFlow-serial1")
                .createBy("1")
                .nickName("张三")
                .skipType(SkipType.PASS.getKey())
                .assigneePermission(Arrays.asList("role:100", "role:101"))
                .permissionFlag(Arrays.asList("role:1", "role:2"));
    }

    @Test
    public void deployFlow() throws Exception {
        String path = "D:\\java\\warm-flow\\warm-flow-doc\\leaveFlow-serial1_1.0.xml";
        System.out.println("已部署流程的id：" + defService.importXml(new FileInputStream(path)).getId());
    }

    @Test
    public void publish() {
        defService.publish(1239301388006199314L);
    }

    @Test
    public void startFlow() {
        System.out.println("已开启的流程实例id：" + insService.start("2", getUser()).getId());
    }

    @Test
    public void skipFlow() {
        // 通过实例id流转
        Instance instance = insService.skipByInsId(1239301524400771072L, getUser().skipType(SkipType.PASS.getKey())
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
        flowParams.message("终止流程").createBy("1");
        taskService.termination(1239217703449923584L, flowParams);
    }

    @Test
    public void skipAnyNode() {
        // 跳转到指定节点 跳转到结束节点
        Instance instance = taskService.skip(1239216494752174080L, getUser().skipType(SkipType.PASS.getKey())
                .permissionFlag(Arrays.asList("role:1", "role:2")).nodeCode("9edc9b26-cab4-4fd4-9a30-c89f11626911"));
        System.out.println("流转后流程实例：" + instance.toString());
    }
    @Test
    public void assignee() {
        // 转办
        System.out.println("转办：" + taskService.transfer(1239301524417548289L, getUser()));
    }
}
