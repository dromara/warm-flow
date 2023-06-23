package com.monkey.flow.core.handler;

import com.monkey.mybatis.core.entity.FlowEntity;
import com.monkey.mybatis.core.handler.DataFillHandler;
import com.monkey.tools.utils.IdUtils;
import com.monkey.tools.utils.ObjectUtil;

import java.util.Date;
import java.util.Objects;

/**
 * @description:  数据填充handler
 * @author minliuhua
 * @date: 2023/4/1 15:37
 */
public class DataFillHandlerImpl implements DataFillHandler {

    @Override
    public void insertFill(Object object) {
        FlowEntity entity = (FlowEntity) object;
        if (ObjectUtil.isNotNull(entity)) {
            if (Objects.isNull(entity.getId()))
            {
                entity.setId(IdUtils.nextId());
            }
            Date date = ObjectUtil.isNotNull(entity.getCreateTime())
                    ? entity.getCreateTime() : new Date();
            entity.setCreateTime(date);
            entity.setUpdateTime(date);
        }
    }

    @Override
    public void updateFill(Object object) {
        FlowEntity entity = (FlowEntity) object;
        if (ObjectUtil.isNotNull(entity)) {
            entity.setUpdateTime(new Date());
        }
    }
}
