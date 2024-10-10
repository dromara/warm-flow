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
package com.warm.flow.ui.controller;

import com.warm.flow.core.dto.ApiResult;
import com.warm.flow.core.invoker.FrameInvoker;
import com.warm.flow.core.service.DefService;
import com.warm.flow.ui.dto.DefDto;
import com.warm.flow.ui.entity.SelectGroup;
import com.warm.flow.ui.service.SelectGroupHandler;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 设计器Controller
 *
 * @author warm
 */
@RestController
@RequestMapping("/warmjars/flow/definition")
public class DefController {
    @Resource
    private DefService defService;


    /**
     * 保存流程xml字符串
     * @param defDto 流程定义dto
     * @return ApiResult<Void>
     * @throws Exception 异常
     */
    @PostMapping("/save-xml")
    @Transactional(rollbackFor = Exception.class)
    public ApiResult<Void> saveXml(@RequestBody DefDto defDto) throws Exception {
        defService.saveXml(defDto.getId(), defDto.getXmlString());
        return ApiResult.ok();
    }

    /**
     * 获取流程xml字符串
     * @param id 流程定义id
     * @return ApiResult<String>
     */
    @GetMapping("/xml-string/{id}")
    public ApiResult<String> xmlString(@PathVariable("id") Long id) {
        return ApiResult.ok(defService.xmlString(id));
    }

    /**
     * 获取设计器办理人组选择框数据
     * @return List<SelectGroup>
     */
    @GetMapping("/select-group")
    public ApiResult<List<SelectGroup>> selectGroup() {
        // 需要业务系统实现该接口
        SelectGroupHandler selectGroupHandler = FrameInvoker.getBean(SelectGroupHandler.class);
        if (selectGroupHandler == null) {
            return ApiResult.ok(new ArrayList<>());
        }
        List<SelectGroup> options = selectGroupHandler.getHandlerSelectGroup();
        return ApiResult.ok(options);
    }
}
