package com.ruoyi.web.controller.system;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.core.domain.AjaxResult;

/**
 * 首页
 *
 * @author ruoyi
 */
@Controller
public class SysIndexController
{
    /** 系统基础配置 */
    @Autowired
    private RuoYiConfig ruoyiConfig;

    /**
     * 访问首页，提示语
     */
    @GetMapping("/")
    public String index()
    {
        return "forward:/index.html";
    }

    /**
     * Runtime metadata for smoke tests and deployment probes.
     */
    @ResponseBody
    @GetMapping("/health")
    public AjaxResult health()
    {
        AjaxResult ajax = AjaxResult.success();
        ajax.put("name", "Warm-Flow Admin Jimmer");
        ajax.put("version", ruoyiConfig.getVersion());
        ajax.put("ui", "/index.html");
        ajax.put("workflowDesigner", "/warm-flow-ui/index.html");
        ajax.put("auth", "admin / admin123");
        return ajax;
    }
}
