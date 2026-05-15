package com.ruoyi.web.controller.monitor;

import com.ruoyi.common.config.RuoYiConfig;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.framework.web.domain.Server;

import javax.annotation.Resource;

/**
 * 服务器监控
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/monitor/server")
public class ServerController
{
    @Resource
    private RuoYiConfig ruoYiConfig;
    @PreAuthorize("@ss.hasPermi('monitor:server:list')")
    @GetMapping()
    public AjaxResult getInfo() throws Exception
    {
        if (ruoYiConfig.isDemoEnabled()) {
            return AjaxResult.success(null);
        }
        Server server = new Server();
        server.copyTo();
        return AjaxResult.success(server);
    }
}
