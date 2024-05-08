package com.warm.flow.jpa.sb.service;

import com.warm.flow.core.dto.FlowParams;
import com.warm.flow.core.enums.SkipType;
import com.warm.flow.core.service.InsService;
import com.warm.flow.jpa.sb.entity.FlowDefinition;
import com.warm.flow.jpa.sb.repository.YourEntityRepository;
import com.warm.tools.utils.IdUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Arrays;

@Service
public class TransactionalTest {

    @Resource
    private YourEntityRepository entityRepository;

    @Resource
    private InsService insService;

    public FlowParams getUser() {
        FlowParams flowParams = FlowParams.build().flowCode("leaveFlow-serial-test")
                .createBy("1")
                .nickName("张三")
                .skipType(SkipType.PASS.getKey())
                .permissionFlag(Arrays.asList("role:1", "role:2"));
        return flowParams;
    }

    @Transactional(rollbackFor = Exception.class)
    public void transactional() {
        entityRepository.save(new FlowDefinition()
                .setId(IdUtils.nextId())
                .setFlowCode(IdUtils.nextIdStr())
                .setFlowName(IdUtils.nextIdStr())
                .setIsPublish(1)
                .setVersion(IdUtils.nextIdStr()));
        System.out.println("已开启的流程实例id：" + insService.start("2", getUser()).getId());
        int n = 1 / 0;
    }
}
