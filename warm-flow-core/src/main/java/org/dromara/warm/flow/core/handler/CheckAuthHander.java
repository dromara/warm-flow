package org.dromara.warm.flow.core.handler;

import java.util.Collection;

/**
 * 用户审批权限校验
 *
 * @author shadow
 */
public interface CheckAuthHander {

    /**
     * 审批流程id 或 审批任务id 必须传一个
     *
     * @param instanceId     审批流程id
     * @param taskId         审批任务id
     * @param permissionList 当前登录用户权限集合 非必传
     */
    boolean checkAuth(Long instanceId, Long taskId, String... permissionList);

    /**
     * 返回当前用户权限集合
     *
     * @param entity 传入自己所需获取当前用户权限的条件 非必传
     */
    <T> Collection<String> currentUserPermissions(T entity);
}
