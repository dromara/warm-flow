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

import com.warm.flow.core.service.DefService;
import com.warm.flow.core.dto.WarmFlowResult;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 流程定义Controller
 *
 * @author warm
 */
@RestController
@RequestMapping("/warm_flow/definition")
public class DefController {
    @Resource
    private DefService defService;


    @GetMapping("/saveXml")
    @Transactional(rollbackFor = Exception.class)
    public WarmFlowResult<Void> saveXml(Long id, String xmlString) throws Exception {
        defService.saveXml(id, xmlString);
        return WarmFlowResult.ok();
    }

    @GetMapping("/xmlString/{id}")
    public WarmFlowResult<String> xmlString(@PathVariable("id") Long id) {
        return WarmFlowResult.ok(defService.xmlString(id));
    }
}
