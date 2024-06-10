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

    /**
     * 根据(代办任务，实例，历史表，节点等)id查询权限人或者处理人
     *
     * @param associateds (代办任务，实例，历史表，节点等)id集合
     * @param types 用户表类型
     * @return
     */
    List<T> listByAssociatedAndTypes(List<Long> associateds, String[] types);
}
