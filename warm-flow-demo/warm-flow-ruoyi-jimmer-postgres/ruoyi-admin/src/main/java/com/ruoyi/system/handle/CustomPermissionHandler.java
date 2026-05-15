package com.ruoyi.system.handle;

import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.flow.utils.PermissionsUtil;
import org.dromara.warm.flow.core.handler.PermissionHandler;

import java.util.List;

/**
 * 办理人权限处理器（可通过配置文件注入，也可用@Bean/@Component方式）
 *
 * @author shadow
 */
public class CustomPermissionHandler implements PermissionHandler {

    /**
     * 获取当前操作用户所有权限
     */
    @Override
    public List<String> permissions() {
        try {
            return PermissionsUtil.getPermissions();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取当前办理人
     * @return 当前办理人
     */
    @Override
    public String getHandler() {
        SysUser sysUser;
        try {
            sysUser = SecurityUtils.getLoginUser().getUser();
        } catch (Exception e) {
            return null;
        }
        if (sysUser != null && sysUser.getUserId() != null) {
            return String.valueOf(sysUser.getUserId());
        }
        return null;
    }

}
