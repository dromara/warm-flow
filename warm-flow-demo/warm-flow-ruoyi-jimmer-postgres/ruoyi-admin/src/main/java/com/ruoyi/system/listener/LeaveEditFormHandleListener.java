package com.ruoyi.system.listener;

import com.ruoyi.system.domain.TestLeave;
import com.ruoyi.system.service.ITestLeaveService;
import org.apache.commons.lang3.time.DateUtils;
import org.dromara.warm.flow.core.listener.Listener;
import org.dromara.warm.flow.core.listener.ListenerVariable;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.ParseException;
import java.util.Map;

/**
 * @author vanlin
 * @className LeaveEditFormHandleListener
 * @description 用于流程表单项目保存数据演示  属于 完成监听器
 * @since 2024-11-2 17:01
 */
@Component
public class LeaveEditFormHandleListener implements Listener {
    @Resource
    private ITestLeaveService testLeaveService;

    @Override
    public void notify(ListenerVariable listenerVariable) {
        // 获取请假详情
        // 表单处理监听器，修改业务参数
        String businessId = listenerVariable.getInstance().getBusinessId();

        TestLeave testLeave = testLeaveService.selectTestLeaveById(businessId);

        Map<String, Object> formData = listenerVariable.getVariable();
        // reason 请假原因
        // startTime 开始时间
        // endTime 结束时间
        // day 天数
        try {
            testLeave.setReason(formData.get("reason").toString());
            testLeave.setStartTime(DateUtils.parseDate(formData.get("startTime").toString(), "yyyy-MM-dd"));
            testLeave.setEndTime(DateUtils.parseDate(formData.get("endTime").toString(), "yyyy-MM-dd"));
            testLeave.setDay(Long.valueOf(formData.get("day").toString()));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        // 更新请假表,以下字段需要在 formHandleAfter处理？？？
//        testLeave.setNodeCode(listenerVariable.getInstance().getNodeCode());
//        testLeave.setNodeName(listenerVariable.getInstance().getNodeName());
//        testLeave.setNodeType(listenerVariable.getInstance().getNodeType());
//        testLeave.setFlowStatus(listenerVariable.getInstance().getFlowStatus());
        testLeaveService.updateTestLeave(testLeave);

        // 流程变量传递业务数据，按实际业务需求传递  【按需传】
        listenerVariable.getFlowParams().getVariable().put("businessData", testLeave);
        // 办理人表达式替换  【按需传】
        listenerVariable.getFlowParams().getVariable().put("flag", String.valueOf(testLeave.getDay()));
    }
}
