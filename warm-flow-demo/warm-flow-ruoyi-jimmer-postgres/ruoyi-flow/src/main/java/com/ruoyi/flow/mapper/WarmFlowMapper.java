package com.ruoyi.flow.mapper;

import com.ruoyi.common.core.domain.entity.SysDept;
import com.ruoyi.common.core.domain.entity.SysRole;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.jimmer.JimmerPage;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.flow.vo.FlowTaskVo;
import com.ruoyi.flow.vo.WarmFlowInteractiveTypeVo;
import com.ruoyi.system.mapper.SysDeptMapper;
import com.ruoyi.system.mapper.SysRoleMapper;
import com.ruoyi.system.mapper.SysUserMapper;
import org.babyfish.jimmer.sql.JSqlClient;
import org.babyfish.jimmer.sql.runtime.JSqlClientImplementor;
import org.dromara.warm.flow.core.entity.HisTask;
import org.dromara.warm.flow.core.entity.Task;
import org.dromara.warm.flow.orm.entity.FlowHisTask;
import org.dromara.warm.flow.orm.entity.FlowTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/** warm-flow 工作流 Jimmer/JDBC 数据层 */
@Repository
public class WarmFlowMapper
{
    @Autowired
    private JSqlClient sqlClient;

    @Autowired
    private SysUserMapper userMapper;

    @Autowired
    private SysRoleMapper roleMapper;

    @Autowired
    private SysDeptMapper deptMapper;

    public List<FlowTaskVo> toDoPage(Task task)
    {
        List<Object> params = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select distinct t.id, t.node_code, t.node_name, t.node_type, t.definition_id, t.instance_id, ")
            .append("t.create_time, t.update_time, t.tenant_id, t.flow_status, i.business_id, i.activity_status, ")
            .append("d.flow_name, t.form_custom, t.form_path, t.del_flag ")
            .append("from flow_task t ")
            .append("left join flow_user uu on uu.associated = t.id ")
            .append("left join flow_definition d on t.definition_id = d.id ")
            .append("left join flow_instance i on t.instance_id = i.id ")
            .append("where t.node_type = 1 ");
        if (task != null)
        {
            if (task.getPermissionList() != null && !task.getPermissionList().isEmpty())
            {
                sql.append("and uu.processed_by in ").append(placeholders(task.getPermissionList())).append(' ');
                params.addAll(task.getPermissionList());
            }
            if (StringUtils.isNotEmpty(task.getNodeCode()))
            {
                sql.append("and t.node_code = ? ");
                params.add(task.getNodeCode());
            }
            if (StringUtils.isNotEmpty(task.getNodeName()))
            {
                sql.append("and t.node_name like ? ");
                params.add("%" + task.getNodeName() + "%");
            }
            if (task.getInstanceId() != null)
            {
                sql.append("and t.instance_id = ? ");
                params.add(task.getInstanceId());
            }
        }
        sql.append("order by t.create_time desc");
        return queryPaged(sql.toString(), this::mapFlowTaskVo, params.toArray());
    }

    public List<FlowHisTask> donePage(HisTask hisTask)
    {
        List<Object> params = new ArrayList<>();
        StringBuilder sub = new StringBuilder("select max(id) as id from flow_his_task where 1 = 1 ");
        if (hisTask != null)
        {
            if (StringUtils.isNotEmpty(hisTask.getApprover()))
            {
                sub.append("and approver = ? ");
                params.add(hisTask.getApprover());
            }
            if (StringUtils.isNotEmpty(hisTask.getNodeCode()))
            {
                sub.append("and node_code = ? ");
                params.add(hisTask.getNodeCode());
            }
            if (StringUtils.isNotEmpty(hisTask.getNodeName()))
            {
                sub.append("and node_name like ? ");
                params.add("%" + hisTask.getNodeName() + "%");
            }
            if (hisTask.getInstanceId() != null)
            {
                sub.append("and instance_id = ? ");
                params.add(hisTask.getInstanceId());
            }
        }
        sub.append("group by instance_id");
        String sql = "select t.id, t.node_code, t.node_name, t.cooperate_type, t.approver, t.collaborator, t.node_type, "
            + "t.target_node_code, t.target_node_name, t.definition_id, t.instance_id, t.task_id, i.flow_status, "
            + "t.skip_type, t.message, t.variable, t.ext, t.create_time, t.update_time, t.tenant_id, t.del_flag, "
            + "i.business_id, t.form_custom, t.form_path, d.flow_name "
            + "from (" + sub + ") tmp "
            + "left join flow_his_task t on t.id = tmp.id "
            + "left join flow_definition d on t.definition_id = d.id "
            + "left join flow_instance i on t.instance_id = i.id "
            + "order by t.create_time desc";
        return queryPaged(sql, this::mapFlowHisTask, params.toArray());
    }

    public List<FlowHisTask> copyPage(FlowTask flowTask)
    {
        List<Object> params = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select b.id, b.node_code, b.node_name, b.node_type, b.definition_id, b.id as instance_id, ")
            .append("b.flow_status, b.business_id, a.create_time, a.update_time, b.tenant_id, b.del_flag, ")
            .append("b.form_custom, b.form_path, d.flow_name, c.nick_name as approver ")
            .append("from flow_user a ")
            .append("left join flow_instance b on a.associated = b.id ")
            .append("left join sys_user c on b.create_by = cast(c.user_id as varchar) ")
            .append("left join flow_definition d on b.definition_id = d.id ")
            .append("where a.type = '4' ");
        if (flowTask != null)
        {
            if (StringUtils.isNotEmpty(flowTask.getFlowName()))
            {
                sql.append("and c.nick_name like ? ");
                params.add("%" + flowTask.getFlowName() + "%");
            }
            if (StringUtils.isNotEmpty(flowTask.getNodeName()))
            {
                sql.append("and b.node_name like ? ");
                params.add("%" + flowTask.getNodeName() + "%");
            }
            if (flowTask.getNodeType() != null)
            {
                sql.append("and b.node_type = ? ");
                params.add(flowTask.getNodeType());
            }
        }
        sql.append("order by a.create_time desc");
        return queryPaged(sql.toString(), this::mapFlowHisTask, params.toArray());
    }

    public List<SysUser> idReverseDisplayName(Long[] ids)
    {
        return ids == null || ids.length == 0 ? new ArrayList<>() : selectUserByIds(Arrays.asList(ids));
    }

    public List<SysUser> selectNotUserIds(WarmFlowInteractiveTypeVo warmFlowInteractiveTypeVo)
    {
        SysUser condition = new SysUser();
        if (warmFlowInteractiveTypeVo != null)
        {
            condition.setDeptId(warmFlowInteractiveTypeVo.getDeptId());
            condition.getParams().putAll(warmFlowInteractiveTypeVo.getParams());
        }
        List<SysUser> users = userMapper.selectUserList(condition);
        if (warmFlowInteractiveTypeVo != null && warmFlowInteractiveTypeVo.getUserIds() != null && !warmFlowInteractiveTypeVo.getUserIds().isEmpty())
        {
            java.util.Set<Long> excluded = warmFlowInteractiveTypeVo.getUserIds().stream().filter(StringUtils::isNotEmpty).map(Long::valueOf).collect(Collectors.toSet());
            users = users.stream().filter(user -> !excluded.contains(user.getUserId())).collect(Collectors.toList());
        }
        return users;
    }

    public List<SysUser> selectUserIds(WarmFlowInteractiveTypeVo warmFlowInteractiveTypeVo)
    {
        if (warmFlowInteractiveTypeVo == null || warmFlowInteractiveTypeVo.getUserIds() == null || warmFlowInteractiveTypeVo.getUserIds().isEmpty())
        {
            return new ArrayList<>();
        }
        List<Long> ids = warmFlowInteractiveTypeVo.getUserIds().stream().filter(StringUtils::isNotEmpty).map(Long::valueOf).collect(Collectors.toList());
        List<SysUser> users = selectUserByIds(ids);
        if (warmFlowInteractiveTypeVo.getDeptId() != null && warmFlowInteractiveTypeVo.getDeptId() != 0)
        {
            users = users.stream().filter(user -> warmFlowInteractiveTypeVo.getDeptId().equals(user.getDeptId())).collect(Collectors.toList());
        }
        return users;
    }

    public List<SysRole> selectRoleByIds(List<Long> roleIds)
    {
        return roleIds == null || roleIds.isEmpty() ? new ArrayList<>() : roleMapper.selectRoleList(new SysRole()).stream().filter(role -> roleIds.contains(role.getRoleId())).collect(Collectors.toList());
    }

    public List<SysDept> selectDeptByIds(List<Long> deptIds)
    {
        return deptIds == null || deptIds.isEmpty() ? new ArrayList<>() : deptMapper.selectDeptList(new SysDept()).stream().filter(dept -> deptIds.contains(dept.getDeptId())).collect(Collectors.toList());
    }

    public List<SysUser> selectUserByIds(List<Long> userIds)
    {
        return userIds == null || userIds.isEmpty() ? new ArrayList<>() : userMapper.selectUserList(new SysUser()).stream().filter(user -> userIds.contains(user.getUserId())).collect(Collectors.toList());
    }

    public List<SysUser> queryLeaderByDeptId(Long deptId)
    {
        return query("select u.user_id, u.dept_id, u.user_name, u.nick_name from sys_user u left join sys_user_role ur on u.user_id = ur.user_id left join sys_role r on r.role_id = ur.role_id where u.dept_id = ? and r.role_key = 'leader'", rs -> {
            SysUser user = new SysUser();
            user.setUserId(rs.getLong("user_id"));
            user.setDeptId(rs.getLong("dept_id"));
            user.setUserName(rs.getString("user_name"));
            user.setNickName(rs.getString("nick_name"));
            return user;
        }, deptId);
    }

    private FlowTaskVo mapFlowTaskVo(ResultSet rs) throws SQLException
    {
        FlowTaskVo task = new FlowTaskVo();
        task.setId(getLong(rs, "id"));
        task.setNodeCode(rs.getString("node_code"));
        task.setNodeName(rs.getString("node_name"));
        task.setNodeType(getInteger(rs, "node_type"));
        task.setDefinitionId(getLong(rs, "definition_id"));
        task.setInstanceId(getLong(rs, "instance_id"));
        task.setFlowStatus(rs.getString("flow_status"));
        task.setCreateTime(rs.getTimestamp("create_time"));
        task.setUpdateTime(rs.getTimestamp("update_time"));
        task.setTenantId(rs.getString("tenant_id"));
        task.setBusinessId(rs.getString("business_id"));
        task.setFlowName(rs.getString("flow_name"));
        task.setFormCustom(rs.getString("form_custom"));
        task.setFormPath(rs.getString("form_path"));
        task.setActivityStatus(getInteger(rs, "activity_status"));
        task.setDelFlag(getStringIfPresent(rs, "del_flag"));
        return task;
    }

    private FlowHisTask mapFlowHisTask(ResultSet rs) throws SQLException
    {
        FlowHisTask task = new FlowHisTask();
        task.setId(getLong(rs, "id"));
        task.setNodeCode(getStringIfPresent(rs, "node_code"));
        task.setNodeName(getStringIfPresent(rs, "node_name"));
        task.setNodeType(getIntegerIfPresent(rs, "node_type"));
        task.setTargetNodeCode(getStringIfPresent(rs, "target_node_code"));
        task.setTargetNodeName(getStringIfPresent(rs, "target_node_name"));
        task.setApprover(getStringIfPresent(rs, "approver"));
        task.setCollaborator(getStringIfPresent(rs, "collaborator"));
        task.setDefinitionId(getLongIfPresent(rs, "definition_id"));
        task.setInstanceId(getLongIfPresent(rs, "instance_id"));
        task.setTaskId(getLongIfPresent(rs, "task_id"));
        task.setCooperateType(getIntegerIfPresent(rs, "cooperate_type"));
        task.setFlowStatus(getStringIfPresent(rs, "flow_status"));
        task.setSkipType(getStringIfPresent(rs, "skip_type"));
        task.setMessage(getStringIfPresent(rs, "message"));
        task.setVariable(getStringIfPresent(rs, "variable"));
        task.setExt(getStringIfPresent(rs, "ext"));
        task.setCreateTime(getTimestampIfPresent(rs, "create_time"));
        task.setUpdateTime(getTimestampIfPresent(rs, "update_time"));
        task.setTenantId(getStringIfPresent(rs, "tenant_id"));
        task.setDelFlag(getStringIfPresent(rs, "del_flag"));
        task.setBusinessId(getStringIfPresent(rs, "business_id"));
        task.setFormCustom(getStringIfPresent(rs, "form_custom"));
        task.setFormPath(getStringIfPresent(rs, "form_path"));
        task.setFlowName(getStringIfPresent(rs, "flow_name"));
        return task;
    }

    private <R> List<R> queryPaged(String sql, RowMapper<R> mapper, Object... params)
    {
        JimmerPage.PageState page = JimmerPage.current();
        if (page == null)
        {
            return query(sql, mapper, params);
        }
        Long total = scalar("select count(*) from (" + sql + ") page_total", Long.class, params);
        JimmerPage.total(total == null ? 0L : total);
        List<Object> paged = new ArrayList<>(Arrays.asList(params));
        paged.add(page.getPageSize());
        paged.add(page.getOffset());
        return query(sql + " limit ? offset ?", mapper, paged.toArray());
    }

    private <R> List<R> query(String sql, RowMapper<R> mapper, Object... params)
    {
        return ((JSqlClientImplementor) sqlClient).getConnectionManager().execute(connection -> {
            try (PreparedStatement ps = connection.prepareStatement(sql))
            {
                bind(ps, params);
                try (ResultSet rs = ps.executeQuery())
                {
                    List<R> rows = new ArrayList<>();
                    while (rs.next())
                    {
                        rows.add(mapper.map(rs));
                    }
                    return rows;
                }
            }
            catch (SQLException e)
            {
                throw new IllegalStateException("WarmFlow Jimmer query failed", e);
            }
        });
    }

    private <R> R scalar(String sql, Class<R> type, Object... params)
    {
        return ((JSqlClientImplementor) sqlClient).getConnectionManager().execute(connection -> {
            try (PreparedStatement ps = connection.prepareStatement(sql))
            {
                bind(ps, params);
                try (ResultSet rs = ps.executeQuery())
                {
                    if (!rs.next())
                    {
                        return null;
                    }
                    Object value = rs.getObject(1);
                    if (value == null)
                    {
                        return null;
                    }
                    if (type == Long.class)
                    {
                        return type.cast(((Number) value).longValue());
                    }
                    return type.cast(value);
                }
            }
            catch (SQLException e)
            {
                throw new IllegalStateException("WarmFlow Jimmer scalar query failed", e);
            }
        });
    }

    private static void bind(PreparedStatement ps, Object... params) throws SQLException
    {
        for (int i = 0; params != null && i < params.length; i++)
        {
            ps.setObject(i + 1, params[i]);
        }
    }

    private static String placeholders(List<?> values)
    {
        return "(" + values.stream().map(v -> "?").collect(Collectors.joining(",")) + ")";
    }

    private static Long getLong(ResultSet rs, String column) throws SQLException
    {
        long value = rs.getLong(column);
        return rs.wasNull() ? null : value;
    }

    private static Long getLongIfPresent(ResultSet rs, String column) throws SQLException
    {
        return hasColumn(rs, column) ? getLong(rs, column) : null;
    }

    private static Integer getInteger(ResultSet rs, String column) throws SQLException
    {
        int value = rs.getInt(column);
        return rs.wasNull() ? null : value;
    }

    private static Integer getIntegerIfPresent(ResultSet rs, String column) throws SQLException
    {
        return hasColumn(rs, column) ? getInteger(rs, column) : null;
    }

    private static String getStringIfPresent(ResultSet rs, String column) throws SQLException
    {
        return hasColumn(rs, column) ? rs.getString(column) : null;
    }

    private static java.util.Date getTimestampIfPresent(ResultSet rs, String column) throws SQLException
    {
        return hasColumn(rs, column) ? rs.getTimestamp(column) : null;
    }

    private static boolean hasColumn(ResultSet rs, String column) throws SQLException
    {
        ResultSet rsCheck = rs;
        for (int i = 1; i <= rsCheck.getMetaData().getColumnCount(); i++)
        {
            if (column.equalsIgnoreCase(rsCheck.getMetaData().getColumnLabel(i)))
            {
                return true;
            }
        }
        return false;
    }

    @FunctionalInterface
    private interface RowMapper<R>
    {
        R map(ResultSet rs) throws SQLException;
    }
}
