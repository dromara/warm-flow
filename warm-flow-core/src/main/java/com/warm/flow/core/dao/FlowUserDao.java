package com.warm.flow.core.dao;

import com.warm.flow.core.entity.User;

import java.util.List;

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
     * @param taskIdList 代办任务主键集合
     * @return 结果
     * @author xiarg
     * @date 2024/5/10 11:19
     */
    int deleteByTaskIds(List<Long> taskIdList);

}
