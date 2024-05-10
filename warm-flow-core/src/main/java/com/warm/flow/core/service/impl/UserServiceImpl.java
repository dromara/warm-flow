package com.warm.flow.core.service.impl;

import com.warm.flow.core.FlowFactory;
import com.warm.flow.core.dao.FlowUserDao;
import com.warm.flow.core.dto.FlowParams;
import com.warm.flow.core.entity.HisTask;
import com.warm.flow.core.entity.Task;
import com.warm.flow.core.entity.User;
import com.warm.flow.core.enums.UserType;
import com.warm.flow.core.orm.service.impl.WarmServiceImpl;
import com.warm.flow.core.service.UserService;
import com.warm.tools.utils.CollUtil;
import com.warm.tools.utils.StreamUtils;

import java.util.List;

/**
 * 流程用户Service业务层处理
 *
 * @author xiarg
 * @date 2024/5/10 13:57
 */
public class UserServiceImpl extends WarmServiceImpl<FlowUserDao<User>, User> implements UserService {

    @Override
    public UserService setDao(FlowUserDao<User> warmDao) {
        this.warmDao = warmDao;
        return this;
    }

    @Override
    public List<User> setUser(List<HisTask> hisTasks, List<Task> addTasks, FlowParams flowParams) {
        List<User> hisTaskUser = StreamUtils.toList(hisTasks, this::hisTaskAddUser);
        List<User> taskUser = StreamUtils.toList(addTasks, this::taskAddUser);
        return CollUtil.listAddToNew(hisTaskUser, taskUser);
    }

    @Override
    public User hisTaskAddUser(HisTask hisTask) {
        User user = FlowFactory.newUser()
                        .setType(UserType.APPROVER.getKey());
        FlowFactory.dataFillHandler().idFill(user);

        return user;
    }

    @Override
    public User taskAddUser(Task task) {
        User user = FlowFactory.newUser()
                .setType(UserType.APPROVAL.getKey());
        FlowFactory.dataFillHandler().idFill(user);
        return user;
    }
}
