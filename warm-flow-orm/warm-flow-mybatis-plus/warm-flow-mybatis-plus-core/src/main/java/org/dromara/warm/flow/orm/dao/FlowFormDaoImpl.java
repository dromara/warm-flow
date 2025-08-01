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
package org.dromara.warm.flow.orm.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.dromara.warm.flow.core.orm.dao.FlowFormDao;
import org.dromara.warm.flow.core.invoker.FrameInvoker;
import org.dromara.warm.flow.orm.entity.FlowForm;
import org.dromara.warm.flow.orm.mapper.FlowFormMapper;
import org.dromara.warm.flow.orm.mapper.WarmMapper;

import java.util.List;

/**
 * @author vanlin
 * @className FlowFormDaoImpl
 * @description
 * @since 2024-12-9 15:54
 */
public class FlowFormDaoImpl extends WarmDaoImpl<FlowForm> implements FlowFormDao<FlowForm> {
    @Override
    public WarmMapper<FlowForm> getMapper() {
        return FrameInvoker.getBean(FlowFormMapper.class);
    }

    @Override
    public List<FlowForm> queryByCodeList(List<String> formCodeList) {
        LambdaQueryWrapper<FlowForm> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(FlowForm::getFormCode, formCodeList);
        return getMapper().selectList(queryWrapper);
    }


    @Override
    public FlowForm newEntity() {
        return new FlowForm();
    }
}
