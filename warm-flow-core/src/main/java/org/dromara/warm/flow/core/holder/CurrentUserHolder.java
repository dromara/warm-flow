package org.dromara.warm.flow.core.holder;

import org.dromara.warm.flow.core.model.WfUser;

/**
 * 当前用户
 *
 * @author
 * @since 2025/8/7 10:09
 */
public class CurrentUserHolder {
    private static final ThreadLocal<WfUser> userHolder = new ThreadLocal<>();

    public static void setCurrentUser(WfUser user) {
        userHolder.set(user);
    }

    public static WfUser getCurrentUser() {
        return userHolder.get();
    }

    public static void clear() {
        userHolder.remove();
    }

}
