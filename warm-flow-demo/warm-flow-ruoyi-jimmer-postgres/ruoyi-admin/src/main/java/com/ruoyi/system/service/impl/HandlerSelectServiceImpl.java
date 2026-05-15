package com.ruoyi.system.service.impl;

import com.ruoyi.common.core.domain.entity.SysDept;
import com.ruoyi.common.core.domain.entity.SysRole;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.jimmer.JimmerPage;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.PageUtils;
import com.ruoyi.flow.mapper.WarmFlowMapper;
import com.ruoyi.system.mapper.SysDeptMapper;
import com.ruoyi.system.mapper.SysRoleMapper;
import com.ruoyi.system.mapper.SysUserMapper;
import org.dromara.warm.flow.core.utils.CollUtil;
import org.dromara.warm.flow.core.utils.MapUtil;
import org.dromara.warm.flow.core.utils.MathUtil;
import org.dromara.warm.flow.core.utils.StreamUtils;
import org.dromara.warm.flow.ui.dto.HandlerFunDto;
import org.dromara.warm.flow.ui.dto.HandlerQuery;
import org.dromara.warm.flow.ui.dto.TreeFunDto;
import org.dromara.warm.flow.ui.service.HandlerSelectService;
import org.dromara.warm.flow.ui.vo.HandlerFeedBackVo;
import org.dromara.warm.flow.ui.vo.HandlerSelectVo;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

import static com.ruoyi.common.utils.PageUtils.startPage;

/**
 * 流程设计器-获取办理人权限设置列表接口实现类
 *
 * @author warm
 */
@Component
public class HandlerSelectServiceImpl implements HandlerSelectService {

    @Resource
    private SysUserMapper userMapper;

    @Resource
    private SysRoleMapper roleMapper;

    @Resource
    private SysDeptMapper deptMapper;

    @Resource
    private WarmFlowMapper warmFlowMapper;

    /**
     * 获取办理人权限设置列表tabs页签，如：用户、角色和部门等，可以返回其中一种或者多种，按业务需求决定
     * @return tabs页签
     */
    @Override
    public List<String> getHandlerType() {
        return Arrays.asList("用户", "角色", "部门");
    }

    /**
     * 获取用户列表、角色列表、部门列表等，可以返回其中一种或者多种，按业务需求决定
     * @param query 查询参数
     * @return 结果
     */
    @Override
    public HandlerSelectVo getHandlerSelect(HandlerQuery query) {

        if ("角色".equals(query.getHandlerType())) {
            return getRole(query);
        }

        if ("部门".equals(query.getHandlerType())) {
            return getDept(query);
        }

        if ("用户".equals(query.getHandlerType())) {
            return getUser(query);
        }

        return new HandlerSelectVo();
    }

    @Override
    public List<HandlerFeedBackVo> handlerFeedback(List<String> storageIds) {
        List<HandlerFeedBackVo> handlerFeedBackVos = new ArrayList<>();
        if (CollUtil.isNotEmpty(storageIds)) {
            List<Long> roleIdList = new ArrayList<>();
            List<Long> deptIdList = new ArrayList<>();
            List<Long> userIdList = new ArrayList<>();

            // 分别过滤出用户、角色和部门的id，分别用集合存储
            for (String storageId : storageIds) {
                if (storageId.startsWith("role:")) {
                    roleIdList.add(Long.valueOf(storageId.replace("role:", "")));
                } else if (storageId.startsWith("dept:")) {
                    deptIdList.add(Long.valueOf(storageId.replace("dept:", "")));
                } else if (MathUtil.isNumeric(storageId)){
                    userIdList.add(Long.valueOf(storageId));
                }
            }

            Map<String, String> authMap = new HashMap<>();
            // 查询角色id对应的名称
            if (CollUtil.isNotEmpty(roleIdList)) {
                // 查询角色列表
                List<SysRole> roleList = warmFlowMapper.selectRoleByIds(roleIdList);
                authMap.putAll(StreamUtils.toMap(roleList, role -> "role:" + role.getRoleId()
                        , SysRole::getRoleName));
            }

            // 查询部门id对应的名称
            if (CollUtil.isNotEmpty(deptIdList)) {
                List<SysDept> deptList = warmFlowMapper.selectDeptByIds(deptIdList);
                authMap.putAll(StreamUtils.toMap(deptList, dept -> "dept:" + dept.getDeptId()
                        , SysDept::getDeptName));
            }

            // 查询用户id对应的名称
            if (CollUtil.isNotEmpty(userIdList)) {
                List<SysUser> userList = warmFlowMapper.selectUserByIds(userIdList);
                authMap.putAll(StreamUtils.toMap(userList, user -> String.valueOf(user.getUserId())
                        , SysUser::getNickName));
            }

            // 遍历storageIds，按照原本的顺序回显名称
            for (String storageId : storageIds) {
                handlerFeedBackVos.add(new HandlerFeedBackVo(storageId
                        , MapUtil.isEmpty(authMap) ? "": authMap.get(storageId)));
            }
        }

        return handlerFeedBackVos;
    }

    /**
     * 获取角色列表
     *
     * @param query 查询条件
     * @return HandlerSelectVo
     */
    private HandlerSelectVo getRole(HandlerQuery query) {
        startPage();
        SysRole sysRole = new SysRole();
        sysRole.setRoleKey(query.getHandlerCode());
        sysRole.setRoleName(query.getHandlerName());
        sysRole.getParams().put("beginTime", query.getBeginTime());
        sysRole.getParams().put("endTime", query.getEndTime());
        // 查询角色列表
        List<SysRole> roleList = roleMapper.selectRoleList(sysRole);
        long total = JimmerPage.totalOrSize(roleList.size());
        PageUtils.clearPage();

        // 业务系统数据，转成组件内部能够显示的数据, total是业务数据总数，用于分页显示
        HandlerFunDto<SysRole> handlerFunDto = new HandlerFunDto<>(roleList, total)
                // 以下设置获取内置变量的Function
                .setStorageId(role -> "role:" + role.getRoleId()) // 前面拼接role:  是为了防止用户、角色的主键重复
                .setHandlerCode(SysRole::getRoleKey) // 权限编码
                .setHandlerName(SysRole::getRoleName) // 权限名称
                .setCreateTime(role -> DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, role.getCreateTime()));

        return getHandlerSelectVo(handlerFunDto);
    }

    /**
     * 获取用户列表
     *
     * @param query 查询条件
     * @return HandlerSelectVo
     */
    private HandlerSelectVo getDept(HandlerQuery query) {
        startPage();
        SysDept sysDept = new SysDept();
        sysDept.setDeptName(query.getHandlerName());
        sysDept.getParams().put("beginTime", query.getBeginTime());
        sysDept.getParams().put("endTime", query.getEndTime());
        // 查询部门列表
        List<SysDept> deptList = deptMapper.selectDeptList(sysDept);
        long total = JimmerPage.totalOrSize(deptList.size());
        PageUtils.clearPage();

        // 业务系统数据，转成组件内部能够显示的数据, total是业务数据总数，用于分页显示
        HandlerFunDto<SysDept> handlerFunDto = new HandlerFunDto<>(deptList, total)
                .setStorageId(dept -> "dept:" + dept.getDeptId()) // 前面拼接dept:  是为了防止用户、部门的主键重复
                .setHandlerName(SysDept::getDeptName) // 权限名称
                .setCreateTime(dept -> DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, dept.getCreateTime()));

        return getHandlerSelectVo(handlerFunDto);

    }

    /**
     * 获取用户列表, 同时构建左侧部门树状结构
     *
     * @param query 查询条件
     * @return HandlerSelectVo
     */
    private HandlerSelectVo getUser(HandlerQuery query) {
        startPage();
        SysUser sysUser = new SysUser();
        sysUser.setUserName(query.getHandlerCode());
        sysUser.setNickName(query.getHandlerName());
        // 办理人用户选择列表，需要展示左侧树状部门，所以可能会通过部门id
        if (MathUtil.isNumeric(query.getGroupId())) {
            sysUser.setDeptId(Long.valueOf(query.getGroupId()));
        }
        sysUser.getParams().put("beginTime", query.getBeginTime());
        sysUser.getParams().put("endTime", query.getEndTime());
        // 查询用户列表
        List<SysUser> userList = userMapper.selectUserList(sysUser);
        long total = JimmerPage.totalOrSize(userList.size());
        PageUtils.clearPage();
        // 查询部门列表，构建树状结构
        List<SysDept> deptList = deptMapper.selectDeptList(new SysDept());

        // 业务系统数据，转成组件内部能够显示的数据, total是业务数据总数，用于分页显示
        HandlerFunDto<SysUser> handlerFunDto = new HandlerFunDto<>(userList, total)
                .setStorageId(user -> user.getUserId().toString())
                .setHandlerCode(SysUser::getUserName) // 权限编码
                .setHandlerName(SysUser::getNickName) // 权限名称
                .setCreateTime(user -> DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, user.getCreateTime()))
                .setGroupName(user -> user.getDept() != null ? user.getDept().getDeptName() : "");

        // 业务系统机构，转成组件内部左侧树列表能够显示的数据
        TreeFunDto<SysDept> treeFunDto = new TreeFunDto<>(deptList)
                .setId(dept -> dept.getDeptId().toString()) // 左侧树ID
                .setName(SysDept::getDeptName) // 左侧树名称
                .setParentId(dept -> dept.getParentId().toString()); // 左侧树父级ID

        return getHandlerSelectVo(handlerFunDto, treeFunDto);
    }
}
