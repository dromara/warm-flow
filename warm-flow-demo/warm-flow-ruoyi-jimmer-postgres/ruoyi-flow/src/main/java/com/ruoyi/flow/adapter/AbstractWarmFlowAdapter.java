package com.ruoyi.flow.adapter;

import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.flow.utils.PermissionsUtil;
import org.dromara.warm.flow.core.enums.CooperateType;
import org.dromara.warm.flow.core.service.TaskService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public abstract class AbstractWarmFlowAdapter {
    @Resource
    protected TaskService taskService;

    /**
     * 获取权限
     *
     * @return 权限列表
     */
    protected List<String> permissionList(SysUser sysUser, String userId) {
        return PermissionsUtil.getPermissions();
    }

    /**
     * 根据类型获取描述
     *
     * @param type 流程类型
     * @return value
     */
    protected String type(Integer type) {
        return CooperateType.getValueByKey(type);
    }
}
