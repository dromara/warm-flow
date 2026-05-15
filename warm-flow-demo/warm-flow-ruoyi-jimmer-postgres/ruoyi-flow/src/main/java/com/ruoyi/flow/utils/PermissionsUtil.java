package com.ruoyi.flow.utils;

import com.ruoyi.common.core.domain.entity.SysRole;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.utils.SecurityUtils;
import org.dromara.warm.flow.core.utils.StreamUtils;

import java.util.List;

/**
 * 获取当前操作用户所有权限
 *
 * @author warm
 */
public class PermissionsUtil {

    /**
     * 获取当前操作用户所有权限
     */
    public static List<String> getPermissions() {
        // 办理人权限标识，比如用户，角色，部门等, 流程设计时未设置办理人或者ignore为true可不传 [按需传输]
        SysUser sysUser = SecurityUtils.getLoginUser().getUser();
        List<SysRole> roles = sysUser.getRoles();
        List<String> permissionList = StreamUtils.toList(roles, role -> "role:" + role.getRoleId());
        if (sysUser.getUserId() != null) {
            permissionList.add(String.valueOf(sysUser.getUserId()));
        }
        if (sysUser.getDeptId() != null) {
            permissionList.add("dept:" + sysUser.getDeptId());
        }
        return permissionList;
    }
}
