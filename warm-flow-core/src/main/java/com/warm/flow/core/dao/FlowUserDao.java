package com.warm.flow.core.dao;

import com.warm.flow.core.entity.User;

/**
 * 流程用户Mapper接口
 *
 * @author xiarg
 * @date 2024/5/10 11:15
 */
public interface FlowUserDao<T extends User> extends WarmDao<T> {

    /**
     * 根据taskId删除
     *
     * @param taskId 代办任务主键
     * @return 结果
     * @author xiarg
     * @date 2024/5/10 11:19
     */
    int deleteByTaskId(Long taskId);

}
