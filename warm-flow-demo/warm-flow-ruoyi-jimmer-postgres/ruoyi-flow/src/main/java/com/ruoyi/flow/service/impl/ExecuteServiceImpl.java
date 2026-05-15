package com.ruoyi.flow.service.impl;

import com.ruoyi.common.core.domain.entity.SysDept;
import com.ruoyi.common.core.domain.entity.SysRole;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.flow.mapper.WarmFlowMapper;
import com.ruoyi.flow.service.ExecuteService;
import com.ruoyi.flow.vo.FlowTaskVo;
import com.ruoyi.flow.vo.WarmFlowInteractiveTypeVo;
import com.ruoyi.system.mapper.SysDeptMapper;
import com.ruoyi.system.mapper.SysRoleMapper;
import com.ruoyi.system.mapper.SysUserMapper;
import org.dromara.warm.flow.core.entity.HisTask;
import org.dromara.warm.flow.core.entity.Task;
import org.dromara.warm.flow.core.utils.CollUtil;
import org.dromara.warm.flow.core.utils.MapUtil;
import org.dromara.warm.flow.core.utils.StreamUtils;
import org.dromara.warm.flow.orm.entity.FlowHisTask;
import org.dromara.warm.flow.orm.entity.FlowTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 流程执行SERVICEIMPL
 *
 * @author WARM
 * @since 2023/5/29 13:09
 */
@Service
public class ExecuteServiceImpl implements ExecuteService {

    @Resource
    private WarmFlowMapper flowMapper;

    @Autowired
    private SysUserMapper userMapper;

    @Autowired
    private SysDeptMapper deptMapper;

    @Autowired
    private SysRoleMapper roleMapper;

    @Override
    public List<FlowTaskVo> toDoPage(Task task) {
        return flowMapper.toDoPage(task);
    }

    @Override
    public List<FlowHisTask> donePage(HisTask hisTask) {
        return flowMapper.donePage(hisTask);
    }

    @Override
    public List<FlowHisTask> copyPage(FlowTask flowTask) {
        return flowMapper.copyPage(flowTask);
    }

    /**
     * 根据ID反显姓名
     *
     * @param ids 需要反显姓名的用户ID
     * @return {@link List<SysUser>}
     * @author liangli
     * @date 2024/8/21 17:11
     **/
    @Override
    public List<SysUser> idReverseDisplayName(Long[] ids) {
        if (Objects.isNull(ids) || ids.length == 0) {
            return null;
        }
        return flowMapper.idReverseDisplayName(ids);
    }

    /**
     * 查询不包含输入的所有用户
     *
     * @param warmFlowInteractiveTypeVo 输入用户编号集合
     * @return 用户列表
     */
    @Override
    public List<SysUser> selectNotUserList(WarmFlowInteractiveTypeVo warmFlowInteractiveTypeVo) {
        return flowMapper.selectNotUserIds(warmFlowInteractiveTypeVo);
    }

    /**
     * 查询包含输入的所有用户
     *
     * @param warmFlowInteractiveTypeVo 输入用户编号集合
     * @return 用户列表
     */
    @Override
    public List<SysUser> selectUserList(WarmFlowInteractiveTypeVo warmFlowInteractiveTypeVo) {
        return flowMapper.selectUserIds(warmFlowInteractiveTypeVo);
    }

    /**
     * 根据ID查询名称
     */
    @Override
    public String getName(String id) {
        Map<Long, String> userMap = StreamUtils.toMap(userMapper.selectUserList(new SysUser())
                , SysUser::getUserId, SysUser::getNickName);
        Map<Long, String> deptMap = StreamUtils.toMap(deptMapper.selectDeptList(new SysDept())
                , SysDept::getDeptId, SysDept::getDeptName);
        Map<Long, String> roleMap = StreamUtils.toMap(roleMapper.selectRoleAll()
                , SysRole::getRoleId, SysRole::getRoleName);
        if (StringUtils.isNotNull(id)) {
            if (id.contains("user:")) {
                String name = userMap.get(Long.valueOf(id.replace("user:", "")));
                if (StringUtils.isNotEmpty(name)) {
                    return "用户:" + name;
                }
            } else if (id.contains("dept:")) {
                String name = deptMap.get(Long.valueOf(id.replace("dept:", "")));
                if (StringUtils.isNotEmpty(name)) {
                    return "部门:" + name;
                }
            } else if (id.contains("role")) {
                String name = roleMap.get(Long.valueOf(id.replace("role:", "")));
                if (StringUtils.isNotEmpty(name)) {
                    return "角色:" + name;
                }
            } else {
                try {
                    long parseLong = Long.parseLong(id);
                    String name = userMap.get(parseLong);
                    if (StringUtils.isNotEmpty(name)) {
                        return "用户:" + name;
                    }
                } catch (NumberFormatException e) {
                    return id;
                }

            }
        }
        return "";
    }

    /**
     * 根据ID查询名称
     */
    @Override
    public Map<String, String> getNameMap(List<String> ids) {
        Map<Long, String> userMap = StreamUtils.toMap(userMapper.selectUserList(new SysUser())
                , SysUser::getUserId, SysUser::getNickName);
        Map<Long, String> deptMap = StreamUtils.toMap(deptMapper.selectDeptList(new SysDept())
                , SysDept::getDeptId, SysDept::getDeptName);
        Map<Long, String> roleMap = StreamUtils.toMap(roleMapper.selectRoleAll()
                , SysRole::getRoleId, SysRole::getRoleName);
        Map<String, String> map = new HashMap<>();
        if (CollUtil.isNotEmpty(ids)) {
            for (String id : ids) {
                if (id.contains("user:")) {
                    String name = userMap.get(Long.valueOf(id.replace("user:", "")));
                    if (StringUtils.isNotEmpty(name)) {
                        map.put("用户", name);
                    }
                } else if (id.contains("dept:")) {
                    String name = deptMap.get(Long.valueOf(id.replace("dept:", "")));
                    if (StringUtils.isNotEmpty(name)) {
                        map.put("部门", name);
                    }
                } else if (id.contains("role")) {
                    String name = roleMap.get(Long.valueOf(id.replace("role:", "")));
                    if (StringUtils.isNotEmpty(name)) {
                        map.put("角色", name);
                    }
                } else {
                    try {
                        long parseLong = Long.parseLong(id);
                        String name = userMap.get(parseLong);
                        if (StringUtils.isNotEmpty(name)) {
                            map.put("用户", name);
                        }
                    } catch (NumberFormatException e) {
                        return MapUtil.newAndPut(id, id);
                    }

                }
            }
        }
        return map;
    }
}
