/*
 *    Copyright 2024-2025, Warm-Flow (290631660@qq.com).
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.warm.flow.core.handler;

import com.warm.flow.core.entity.RootEntity;
import com.warm.flow.core.utils.IdUtils;
import com.warm.flow.core.utils.ObjectUtil;

import java.util.Date;
import java.util.Objects;

/**
 * @author warm
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
            entity.setCreateTime(ObjectUtil.isNotNull(entity.getCreateTime()) ? entity.getCreateTime() : new Date());
            entity.setUpdateTime(ObjectUtil.isNotNull(entity.getUpdateTime()) ? entity.getCreateTime() : new Date());
        }
    }

    @Override
    public void updateFill(Object object) {
        RootEntity entity = (RootEntity) object;
        if (ObjectUtil.isNotNull(entity)) {
            entity.setUpdateTime(ObjectUtil.isNotNull(entity.getUpdateTime()) ? entity.getCreateTime() : new Date());
        }
    }
}
