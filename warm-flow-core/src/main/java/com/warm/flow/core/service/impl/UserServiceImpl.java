package com.warm.flow.core.service.impl;

import com.warm.flow.core.FlowFactory;
import com.warm.flow.core.constant.ExceptionCons;
import com.warm.flow.core.dao.FlowUserDao;
import com.warm.flow.core.dto.FlowParams;
import com.warm.flow.core.entity.HisTask;
import com.warm.flow.core.entity.Task;
import com.warm.flow.core.entity.User;
import com.warm.flow.core.enums.UserType;
import com.warm.flow.core.orm.service.impl.WarmServiceImpl;
import com.warm.flow.core.service.UserService;
import com.warm.flow.core.utils.AssertUtil;
import com.warm.tools.utils.CollUtil;
import com.warm.tools.utils.StreamUtils;
import com.warm.tools.utils.StringUtils;

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
        List<User> hisTaskUserList = null;
        if(CollUtil.isNotEmpty(hisTasks)){
            hisTaskUserList = StreamUtils.toList(hisTasks, hisTask -> hisTaskAddUser(hisTask.getId(), flowParams));
        }
        List<List<User>> taskUserList = null;
        if(CollUtil.isNotEmpty(addTasks)){
            taskUserList = StreamUtils.toList(addTasks, task -> taskAddUser(task, flowParams));
        }
        return CollUtil.listAddListsToNew(hisTaskUserList, taskUserList);
    }

    @Override
    public List<User> setSkipUser(List<HisTask> hisTasks, List<Task> addTasks, FlowParams flowParams, Long taskId) {
        // 删除已执行的代办任务的权限人
        delUser(CollUtil.toList(taskId));
        return setUser(hisTasks, addTasks, flowParams);
    }

    @Override
    public User hisTaskAddUser(Long hisTaskId, FlowParams flowParams) {
        // 新建流程历史的已审批人
        return getUser(hisTaskId, flowParams.getCreateBy(), UserType.APPROVER.getKey());
    }

    @Override
    public List<User> taskAddUser(Task task, FlowParams flowParams) {
        // 审批人权限不能为空
        AssertUtil.isTrue(CollUtil.isEmpty(task.getPermissionList()), ExceptionCons.LOST_NEXT_PERMISSION);
        // 遍历权限集合，生成流程节点的权限
        return StreamUtils.toList(task.getPermissionList()
                , permission -> getUser(task.getId(), permission, UserType.APPROVAL.getKey()));
    }

    @Override
    public void delUser(List<Long> ids) {
        getDao().deleteByTaskIds(ids);
    }

    @Override
    public List<String> getPermission(long id) {
        return StreamUtils.toList(list(FlowFactory.newUser().setAssociated(id)), User::getProcessedBy);
    }

    @Override
    public boolean updatePermission(Long associated, List<String> permissions, String type, boolean clear,
                                    String createBy) {
        // 判断是否clear，如果是true，则先删除当前关联id用户数据
        if(clear){
            delUser(CollUtil.toList(associated));
        }
        // 再新增权限人
        saveBatch(StreamUtils.toList(permissions, permission ->
                StringUtils.isEmpty(createBy)?
                getUser(associated, permission, type):getUser(associated, permission, type, createBy)));
        return true;
    }

    @Override
    public User getUser(Long associated, String permission, String type) {
        return getUser(associated, permission, type, null);
    }

    @Override
    public User getUser(Long associated, String permission, String type, String createBy) {
        User user = FlowFactory.newUser()
                .setType(type)
                .setProcessedBy(permission)
                .setAssociated(associated)
                .setCreateBy(createBy);
        FlowFactory.dataFillHandler().idFill(user);
        return user;
    }

    @Override
    public boolean haveDepute(long taskId) {
        List<User> userList = list(FlowFactory.newUser().setAssociated(taskId));
        for (User user : userList) {
            if(UserType.DEPUTE.getKey().equals(user.getType())){
                return true;
            }
        }
        return false;
    }
}
