package com.warm.flow.ui.controller;

import com.warm.flow.core.service.DefService;
import com.warm.flow.ui.core.WarmFlowResult;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 流程定义Controller
 *
 * @author hh
 * @date 2023-04-11
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
