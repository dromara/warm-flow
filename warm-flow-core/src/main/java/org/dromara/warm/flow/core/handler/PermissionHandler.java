package org.dromara.warm.flow.core.handler;

import org.dromara.warm.flow.core.dto.FlowParams;

import java.util.List;

/**
 * 办理人权限处理器
 * 用户获取工作流中用到的permissionFlag和handler
 * permissionFlag: 办理人权限标识，比如用户，角色，部门等，用于校验是否有权限办理任务
 * handler: 办理人唯一标识，如用户id，用于记录历史表
 *
 * @author shadow
 * @see <a href="https://warm-flow.dromara.org/master/advanced/permission_handler.html">文档地址</a>
 */
public interface PermissionHandler {

    /**
     * 办理人权限标识，比如用户，角色，部门等，用于校验是否有权限办理任务
     * 后续在{@link FlowParams#getPermissionFlag}  中获取
     * 返回当前用户权限集合
     *
     */
    List<String> permissions();

    /**
     * 获取当前办理人
     * 后续在{@link FlowParams#getHandler()}  中获取
     * @return 当前办理人
     */
    String getHandler();

}
