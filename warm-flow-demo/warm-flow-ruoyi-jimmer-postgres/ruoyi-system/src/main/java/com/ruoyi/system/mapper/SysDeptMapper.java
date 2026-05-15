package com.ruoyi.system.mapper;

import com.ruoyi.common.core.domain.entity.SysDept;
import com.ruoyi.system.jimmer.JimmerRepositorySupport;
import com.ruoyi.system.jimmer.model.SysDeptModel;
import org.babyfish.jimmer.sql.ast.Predicate;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;

/** 部门 Jimmer 数据层 */
@Repository
public class SysDeptMapper extends JimmerRepositorySupport<SysDept, SysDeptModel>
{
    public SysDeptMapper()
    {
        super(SysDept.class, SysDeptModel.class, "deptId");
    }

    public List<SysDept> selectDeptList(SysDept dept)
    {
        return list(deptPredicates(dept), orders("parentId", "orderNum", "deptId"));
    }

    public List<Long> selectDeptListByRoleId(Long roleId, boolean deptCheckStrictly)
    {
        String strict = deptCheckStrictly ? " and d.dept_id not in (select d2.parent_id from sys_dept d2 inner join sys_role_dept rd2 on d2.dept_id = rd2.dept_id and rd2.role_id = ?)" : "";
        return sqlClient.createQuery(com.ruoyi.system.jimmer.model.SysDeptModelTable.$)
            .where(sql("dept_id in (select d.dept_id from sys_dept d inner join sys_role_dept rd on d.dept_id = rd.dept_id where rd.role_id = ?" + strict + ")", deptCheckStrictly ? new Object[]{roleId, roleId} : new Object[]{roleId}))
            .select(com.ruoyi.system.jimmer.model.SysDeptModelTable.$.deptId())
            .execute();
    }

    public SysDept selectDeptById(Long deptId)
    {
        return selectById(deptId);
    }

    public List<SysDept> selectChildrenDeptById(Long deptId)
    {
        return list(predicates(sql("? = any(string_to_array(ancestors, ',')::bigint[]) or dept_id = ?", deptId, deptId)), orders("parentId", "orderNum", "deptId"));
    }

    public int selectNormalChildrenDeptById(Long deptId)
    {
        return (int) count(predicates(eq("status", "0"), eq("delFlag", "0"), sql("? = any(string_to_array(ancestors, ',')::bigint[])", deptId)));
    }

    public int hasChildByDeptId(Long deptId)
    {
        return (int) count(predicates(eq("parentId", deptId), eq("delFlag", "0")));
    }

    public int checkDeptExistUser(Long deptId)
    {
        return (int) sqlClient.createQuery(com.ruoyi.system.jimmer.model.SysUserModelTable.$)
            .where(com.ruoyi.system.jimmer.model.SysUserModelTable.$.deptId().eq(deptId), com.ruoyi.system.jimmer.model.SysUserModelTable.$.delFlag().eq("0"))
            .selectCount()
            .fetchOne()
            .longValue();
    }

    public SysDept checkDeptNameUnique(String deptName, Long parentId)
    {
        return findOne(predicates(eq("deptName", deptName), eq("parentId", parentId), eq("delFlag", "0")), null);
    }

    public int insertDept(SysDept dept)
    {
        return insert(dept);
    }

    public int updateDept(SysDept dept)
    {
        return updateById(dept);
    }

    public void updateDeptStatusNormal(Long[] deptIds)
    {
        updateWhere(predicates(in("deptId", Arrays.asList(deptIds))), mapOf("status", "0"));
    }

    public int updateDeptChildren(List<SysDept> depts)
    {
        int rows = 0;
        if (depts != null)
        {
            for (SysDept dept : depts)
            {
                rows += updateFieldsById(dept.getDeptId(), mapOf("ancestors", dept.getAncestors()));
            }
        }
        return rows;
    }

    public int deleteDeptById(Long deptId)
    {
        return softDeleteById(deptId);
    }

    private List<Predicate> deptPredicates(SysDept dept)
    {
        List<Predicate> predicates = predicates(eq("delFlag", "0"));
        if (dept != null)
        {
            predicates.add(eq("deptId", dept.getDeptId()));
            predicates.add(eq("parentId", dept.getParentId()));
            predicates.add(like("deptName", dept.getDeptName()));
            predicates.add(eq("status", dept.getStatus()));
            predicates.add(dataScope(dept));
        }
        return predicates;
    }
}
