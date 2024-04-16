package com.warm.flow.core.handler;

import com.warm.flow.core.entity.RootEntity;
import com.warm.tools.utils.IdUtils;
import com.warm.tools.utils.ObjectUtil;

import java.util.Date;
import java.util.Objects;

/**
 * @author minliuhua
 * @description: 数据填充handler
 * @date: 2023/4/1 15:37
 */
public class DefaultDataFillHandler implements DataFillHandler {

    @Override
    public void idFill(Object object) {
        RootEntity entity = (RootEntity) object;
        if (ObjectUtil.isNotNull(entity)) {
            if (Objects.isNull(entity.getId())) {
                entity.setId(IdUtils.nextId());
            }
        }
    }

    @Override
    public void insertFill(Object object) {
        RootEntity entity = (RootEntity) object;
        if (ObjectUtil.isNotNull(entity)) {
            Date date = ObjectUtil.isNotNull(entity.getCreateTime())
                    ? entity.getCreateTime() : new Date();
            entity.setCreateTime(date);
            entity.setUpdateTime(date);
        }
    }

    @Override
    public void updateFill(Object object) {
        RootEntity entity = (RootEntity) object;
        if (ObjectUtil.isNotNull(entity)) {
            entity.setUpdateTime(new Date());
        }
    }
}
