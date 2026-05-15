package com.ruoyi.system.service.impl;

import org.dromara.warm.flow.ui.service.ListenerListService;
import org.dromara.warm.flow.ui.vo.ListenerVo;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 获取监听器列表
 *
 * @author warm
 * @since 2026/3/20
 */
@Service
public class ListenerListServiceImpl implements ListenerListService {

    @Override
    public List<ListenerVo> listenerList() {
        List<ListenerVo> listenerList = new ArrayList<>();
        listenerList.add(new ListenerVo("create", "com.ruoyi.system.listener.AutoApprovalListener", "超时自动审批监听器"));
        listenerList.add(new ListenerVo("finish", "com.ruoyi.system.listener.HttplListener", "远程请求监听器"));
        listenerList.add(new ListenerVo("finish", "com.ruoyi.system.listener.ScriptlListener", "脚本监听器"));
        listenerList.add(new ListenerVo("start", "com.ruoyi.system.listener.StartListener", "开始监听器"));
        listenerList.add(new ListenerVo("assignment", "com.ruoyi.system.listener.AssignmentListener", "分派监听器"));
        listenerList.add(new ListenerVo("finish", "com.ruoyi.system.listener.FinishListener", "完成监听器"));
        listenerList.add(new ListenerVo("creat", "com.ruoyi.system.listener.CreateListener", "创建监听器"));
        return listenerList;
    }
}
