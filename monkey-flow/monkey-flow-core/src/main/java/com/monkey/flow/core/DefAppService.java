package com.monkey.flow.core;

import com.monkey.flow.core.domain.entity.FlowDefinition;
import com.monkey.flow.core.service.IFlowDefinitionService;
import org.dom4j.Document;

import java.io.InputStream;

/**
 * @author minliuhua
 * @description: 流程定义对外提供
 * @date: 2023/3/30 15:24
 */
public interface DefAppService {

    /**
     * 获取流程定义服务
     * @return
     */
    IFlowDefinitionService getService();

    /**
     * 导入流程定义xml
     * @param is
     * @throws Exception
     */
    void importXml(InputStream is) throws Exception;

    /**
     * 导出流程定义xml
     * @param id
     * @return
     */
    Document exportXml(Long id);

    /**
     * 查询流程定义所以信息
     * @param id
     * @return
     */
    FlowDefinition getAllDataDefinition(Long id);

}
