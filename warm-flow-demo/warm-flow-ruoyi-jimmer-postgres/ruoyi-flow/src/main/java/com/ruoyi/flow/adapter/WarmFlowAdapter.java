package com.ruoyi.flow.adapter;

import com.ruoyi.flow.vo.WarmFlowInteractiveTypeVo;

public interface WarmFlowAdapter {
    boolean isAdapter(Integer warmFlowType);

    boolean adapter(WarmFlowInteractiveTypeVo obj);
}
