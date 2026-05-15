package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.SysRoleDept;
import com.ruoyi.system.jimmer.JimmerRepositorySupport;
import com.ruoyi.system.jimmer.model.SysRoleDeptModel;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;

/** 角色与部门关联 Jimmer 数据层 */
@Repository
public class SysRoleDeptMapper extends JimmerRepositorySupport<SysRoleDept, SysRoleDeptModel>
{
    public SysRoleDeptMapper()
    {
        super(SysRoleDept.class, SysRoleDeptModel.class, "roleDeptId");
    }

    public int deleteRoleDeptByRoleId(Long roleId)
    {
        return deleteWhere(eq("roleId", roleId));
    }

    public int deleteRoleDept(Long[] ids)
    {
        return deleteWhere(in("roleId", Arrays.asList(ids)));
    }

    public int selectCountRoleDeptByDeptId(Long deptId)
    {
        return (int) count(predicates(eq("deptId", deptId)));
    }

    public int batchRoleDept(List<SysRoleDept> roleDeptList)
    {
        return batchInsert(roleDeptList);
    }
}
