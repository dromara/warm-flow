package com.ruoyi.flow.adapter;

import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.flow.utils.PermissionsUtil;
import com.ruoyi.flow.vo.WarmFlowInteractiveTypeVo;
import org.dromara.warm.flow.core.dto.FlowParams;
import org.dromara.warm.flow.core.enums.CooperateType;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 减签适配器
 */
@Component
public class ReductionSignatureAdapter extends AbstractWarmFlowAdapter implements WarmFlowAdapter {
    @Override
    public boolean isAdapter(Integer warmFlowType) {
        return Objects.equals(CooperateType.REDUCTION_SIGNATURE.getKey(), warmFlowType);
    }

    @Override
    public boolean adapter(WarmFlowInteractiveTypeVo obj) {
        Long taskId = obj.getTaskId();
        String userId = String.valueOf(SecurityUtils.getUserId());
        FlowParams flowParams = new FlowParams()
                .handler(userId)
                .permissionFlag(PermissionsUtil.getPermissions())
                .reductionHandlers(obj.getAddHandlers())
                .message(type(obj.getOperatorType()));

        return super.taskService.reductionSignature(taskId, flowParams);
    }
}
