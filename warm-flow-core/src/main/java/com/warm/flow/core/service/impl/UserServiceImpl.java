package com.warm.flow.core.service.impl;

import com.warm.flow.core.FlowFactory;
import com.warm.flow.core.constant.ExceptionCons;
import com.warm.flow.core.dao.FlowUserDao;
import com.warm.flow.core.entity.Task;
import com.warm.flow.core.entity.User;
import com.warm.flow.core.enums.UserType;
import com.warm.flow.core.orm.service.impl.WarmServiceImpl;
import com.warm.flow.core.service.UserService;
import com.warm.flow.core.utils.ArrayUtil;
import com.warm.flow.core.utils.AssertUtil;
import com.warm.flow.core.utils.CollUtil;
import com.warm.flow.core.utils.StreamUtils;

import java.util.ArrayList;
import java.util.Collections;
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
    public List<User> taskAddUsers(List<Task> addTasks) {
        List<User> taskUserList = new ArrayList<>();
        if(CollUtil.isNotEmpty(addTasks)){
            StreamUtils.toList(addTasks, task -> taskUserList.addAll(taskAddUser(task)));
        }
        return taskUserList;
    }

    @Override
    public List<User> setSkipUser(List<Task> addTasks, Long taskId) {
        // 删除已执行的代办任务的权限人
        deleteByTaskIds(CollUtil.toList(taskId));
        return taskAddUsers(addTasks);
    }

    @Override
    public List<User> taskAddUser(Task task) {
        // 审批人权限不能为空
        AssertUtil.isTrue(CollUtil.isEmpty(task.getPermissionList()), ExceptionCons.LOST_NEXT_PERMISSION);
        // 遍历权限集合，生成流程节点的权限
        return StreamUtils.toList(task.getPermissionList()
                , permission -> structureUser(task.getId(), permission, UserType.APPROVAL.getKey()));
    }

    @Override
    public void deleteByTaskIds(List<Long> ids) {
        getDao().deleteByTaskIds(ids);
    }

    @Override
    public List<String> getPermission(Long associated, String... types) {
        if (ArrayUtil.isEmpty(types)) {
            return StreamUtils.toList(list(FlowFactory.newUser().setAssociated(associated)), User::getProcessedBy);
        }
        if (types.length == 1) {
            return StreamUtils.toList(list(FlowFactory.newUser().setAssociated(associated).setType(types[0]))
                    , User::getProcessedBy);
        }
        return StreamUtils.toList(getDao().listByAssociatedAndTypes(Collections.singletonList(associated), types)
                , User::getProcessedBy);
    }

    @Override
    public List<User> listByAssociatedAndTypes(Long associated, String... types) {
        if (ArrayUtil.isEmpty(types)) {
            return list(FlowFactory.newUser().setAssociated(associated));
        }
        if (types.length == 1) {
            return list(FlowFactory.newUser().setAssociated(associated).setType(types[0]));
        }
        return getDao().listByAssociatedAndTypes(Collections.singletonList(associated), types);
    }

    @Override
    public List<User> getByAssociateds(List<Long> associateds, String... types) {
        if (CollUtil.isEmpty(associateds)) {
            return Collections.emptyList();
        }
        if (associateds.size() == 1) {
            return listByAssociatedAndTypes(associateds.get(0), types);
        }
        return getDao().listByAssociatedAndTypes(associateds, types);
    }


    @Override
    public boolean updatePermission(Long associated, List<String> permissions, String type, boolean clear,
                                    String handler) {
        // 判断是否clear，如果是true，则先删除当前关联id用户数据
        if(clear){
            getDao().delete(FlowFactory.newUser().setAssociated(associated).setCreateBy(handler));
        }
        // 再新增权限人
        saveBatch(StreamUtils.toList(permissions, permission -> structureUser(associated, permission, type, handler)));
        return true;
    }

    @Override
    public List<User> structureUser(Long associated, List<String> permissionList, String type) {
        return StreamUtils.toList(permissionList, permission -> structureUser(associated, permission, type, null));
    }

    @Override
    public User structureUser(Long associated, String permission, String type) {
        return structureUser(associated, permission, type, null);
    }

    @Override
    public List<User> structureUser(Long associated, List<String> permissionList, String type, String handler) {
        return StreamUtils.toList(permissionList, permission -> structureUser(associated, permission, type, handler));
    }

    @Override
    public User structureUser(Long associated, String permission, String type, String handler) {
        User user = FlowFactory.newUser()
                .setType(type)
                .setProcessedBy(permission)
                .setAssociated(associated)
                .setCreateBy(handler);
        FlowFactory.dataFillHandler().idFill(user);
        return user;
    }

}