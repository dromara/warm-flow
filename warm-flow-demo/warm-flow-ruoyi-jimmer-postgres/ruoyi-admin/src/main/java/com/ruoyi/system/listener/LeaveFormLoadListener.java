package com.ruoyi.system.listener;

import com.ruoyi.system.domain.TestLeave;
import com.ruoyi.system.service.ITestLeaveService;
import org.dromara.warm.flow.core.constant.FlowCons;
import org.dromara.warm.flow.core.listener.Listener;
import org.dromara.warm.flow.core.listener.ListenerVariable;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author vanlin
 * @className LeaveFormLoadListener
 * @description 用于流程表单从业务功能加载数据演示
 * @since 2024-11-2 17:01
 */
@Component
public class LeaveFormLoadListener implements Listener {
    @Resource
    private ITestLeaveService testLeaveService;

    @Override
    public void notify(ListenerVariable listenerVariable) {
        // 获取请假详情
        // 全局表单加载器，加载表单业务数据，按map返回
        String businessId = listenerVariable.getInstance().getBusinessId();

        TestLeave testLeave = testLeaveService.selectTestLeaveById(businessId);
        final Map<String, Object> formData = new HashMap<String, Object>();
        // reason 请假原因
        // startTime 开始时间
        // endTime 结束时间
        // day 天数
        formData.put("reason", testLeave.getReason());
        List<String> time = new ArrayList<String>();
        time.add(String.valueOf(testLeave.getStartTime()));
        time.add(String.valueOf(testLeave.getEndTime()));
        formData.put("time", time);
        formData.put("day", testLeave.getDay());

        listenerVariable.getInstance().getVariableMap().put(FlowCons.FORM_DATA, formData);
    }
}
