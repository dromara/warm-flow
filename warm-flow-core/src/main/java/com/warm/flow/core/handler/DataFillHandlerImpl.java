package com.warm.flow.core.handler;

import com.warm.mybatis.core.entity.FlowEntity;
import com.warm.mybatis.core.handler.DataFillHandler;
import com.warm.tools.utils.IdUtils;
import com.warm.tools.utils.ObjectUtil;

import java.util.Date;
import java.util.Objects;

/**
 * @author minliuhua
 * @description: 数据填充handler
 * @date: 2023/4/1 15:37
 */
public class DataFillHandlerImpl implements DataFillHandler {

    @Override
    public void insertFill(Object object) {
        FlowEntity entity = (FlowEntity) object;
        if (ObjectUtil.isNotNull(entity)) {
            if (Objects.isNull(entity.getId())) {
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
