package org.dromara.warm.flow.core.handler;

import org.dromara.warm.flow.core.dto.FlowParams;

import java.util.List;

/**
 * 办理人权限处理器
 *
 * @author shadow
 */
public interface PermissionHandler {

    /**
     * 审批前获取当前办理人，办理时会校验的该权限集合
     * 后续在{@link FlowParams#getPermissionFlag}  中获取
     * 返回当前用户权限集合
     *
     */
    List<String> permissions();

    /**
     * 获取当前办理人
     * @return 当前办理人
     */
    String getHandler();

}
