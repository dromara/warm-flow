package com.warm.flow.core.service;

import com.warm.flow.core.entity.Definition;
import com.warm.flow.core.orm.service.IWarmService;
import org.dom4j.Document;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * 流程定义Service接口
 *
 * @author warm
 * @date 2023-03-29
 */
public interface DefService extends IWarmService<Definition> {

    /**
     * 导入xml
     *
     * @param is
     * @throws Exception
     */
    Definition importXml(InputStream is) throws Exception;

    /**
     * 根据xml字符串保持流程定义
     *
     * @param def
     * @throws Exception
     */
    void saveXml(Definition def) throws Exception;

    Document exportXml(Long id);

    String xmlString(Long id);

    List<Definition> queryByCodeList(List<String> flowCodeList);

    void closeFlowByCodeList(List<String> flowCodeList);

    /**
     * 校验后新增
     *
     * @param definition
     * @return
     */
    boolean checkAndSave(Definition definition);

    /**
     * 删除流程定义
     *
     * @param ids
     * @return
     */
    boolean removeDef(List<Long> ids);

    /**
     * 发布流程定义
     *
     * @param id
     * @return
     */
    boolean publish(Long id);

    /**
     * 取消发布流程定义
     *
     * @param id
     * @return
     */
    boolean unPublish(Long id);

    /**
     * 复制流程定义
     *
     * @param id
     * @return
     */
    boolean copyDef(Long id);

    /**
     * 获取流程图的图片流
     *
     * @param instanceId
     * @return
     * @throws IOException
     */
    String flowChart(Long instanceId) throws IOException;
}
