package com.ruoyi.system.service.impl;

import org.dromara.warm.flow.core.dto.DefJson;
import org.dromara.warm.flow.core.dto.PromptContent;
import org.dromara.warm.flow.core.utils.MapUtil;
import org.dromara.warm.flow.ui.service.ChartExtService;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 流程图提示信息
 *
 * @author warm
 */
@Component
public class ChartExtServiceImpl implements ChartExtService {

    /**
     * 扩展流程图
     * @param defJson 流程定义json对象
     */
    @Override
    public void execute(DefJson defJson) {
        defJson.getNodeList().forEach(nodeJson -> {
            Map<String, Object> extMap = nodeJson.getExtMap();
            if (MapUtil.isNotEmpty(extMap)) {
                for(Map.Entry<String, Object> entry : extMap.entrySet()){
                    // 添加第二个条目
                    PromptContent.InfoItem item2 = new PromptContent.InfoItem();
                    item2.setPrefix(entry.getKey() + ": ");
                    item2.setContent((String) entry.getValue());
                    nodeJson.getPromptContent().getInfo().add(item2);
                }
            }
        });
    }

}
