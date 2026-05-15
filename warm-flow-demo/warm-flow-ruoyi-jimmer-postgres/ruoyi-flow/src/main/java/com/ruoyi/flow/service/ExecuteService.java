package com.ruoyi.flow.service;

import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.flow.vo.WarmFlowInteractiveTypeVo;
import com.ruoyi.flow.vo.FlowTaskVo;
import org.dromara.warm.flow.core.entity.HisTask;
import org.dromara.warm.flow.core.entity.Task;
import org.dromara.warm.flow.orm.entity.FlowHisTask;
import org.dromara.warm.flow.orm.entity.FlowTask;

import java.util.List;
import java.util.Map;

/**
 * 流程执行service
 *
 * @author warm
 * @since 2023/5/29 13:09
 */
public interface ExecuteService {

    /**
     * 分页查询待办任务
     *
     * @param task 条件实体
     * @return
     */
    List<FlowTaskVo> toDoPage(Task task);

    /**
     * 获取已办任务
     *
     * @param hisTask
     * @return
     */
    List<FlowHisTask> donePage(HisTask hisTask);

    List<FlowHisTask> copyPage(FlowTask flowTask);

    /**
     * 根据ID反显姓名
     *
     * @param ids 需要反显姓名的用户ID
     * @return {@link List<SysUser>}
     * @author liangli
     * @date 2024/8/21 17:11
     **/
    List<SysUser> idReverseDisplayName(Long[] ids);

    /**
     * 根据条件分页查询不等于用户列表的所有用户
     *
     * @param warmFlowInteractiveTypeVo 用户编号集合
     * @return 用户信息集合信息
     */
    public List<SysUser> selectNotUserList(WarmFlowInteractiveTypeVo warmFlowInteractiveTypeVo);

    /**
     * 根据条件分页查询不等于用户列表的所有用户
     *
     * @param warmFlowInteractiveTypeVo 用户编号集合
     * @return 用户信息集合信息
     */
    public List<SysUser> selectUserList(WarmFlowInteractiveTypeVo warmFlowInteractiveTypeVo);

    String getName(String id);

    public Map<String, String> getNameMap(List<String> ids);
}
