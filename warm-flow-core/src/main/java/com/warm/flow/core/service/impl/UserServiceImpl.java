package com.warm.flow.core.service.impl;

import com.warm.flow.core.FlowFactory;
import com.warm.flow.core.constant.ExceptionCons;
import com.warm.flow.core.dao.FlowUserDao;
import com.warm.flow.core.entity.Task;
import com.warm.flow.core.entity.User;
import com.warm.flow.core.enums.UserType;
import com.warm.flow.core.orm.service.impl.WarmServiceImpl;
import com.warm.flow.core.service.UserService;
import com.warm.flow.core.utils.AssertUtil;
import com.warm.flow.core.utils.CollUtil;
import com.warm.flow.core.utils.StreamUtils;

import java.util.ArrayList;
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
    public List<String> getPermission(long id) {
        return StreamUtils.toList(list(FlowFactory.newUser().setAssociated(id)), User::getProcessedBy);
    }

    @Override
    public List<String> getPermission(long id, String type) {
        return StreamUtils.toList(list(FlowFactory.newUser().setAssociated(id).setType(type))
                , User::getProcessedBy);
    }

    @Override
    public boolean updatePermission(Long associated, List<String> permissions, String type, boolean clear,
                                    String createBy) {
        // 判断是否clear，如果是true，则先删除当前关联id用户数据
        if(clear){
            getDao().delete(FlowFactory.newUser().setAssociated(associated).setType(UserType.APPROVAL.getKey()));
        }
        // 再新增权限人
        saveBatch(StreamUtils.toList(permissions, permission -> structureUser(associated, permission, type, createBy)));
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
    public List<User> structureUser(Long associated, List<String> permissionList, String type, String createBy) {
        return StreamUtils.toList(permissionList, permission -> structureUser(associated, permission, type, createBy));
    }

    @Override
    public User structureUser(Long associated, String permission, String type, String createBy) {
        User user = FlowFactory.newUser()
                .setType(type)
                .setProcessedBy(permission)
                .setAssociated(associated)
                .setCreateBy(createBy);
        FlowFactory.dataFillHandler().idFill(user);
        return user;
    }

    @Override
    public boolean haveDepute(Long taskId, String createBy) {
        List<User> userList = list(FlowFactory.newUser().setAssociated(taskId).setCreateBy(createBy)
                .setType(UserType.DEPUTE.getKey()));
        return CollUtil.isNotEmpty(userList);
    }
}
