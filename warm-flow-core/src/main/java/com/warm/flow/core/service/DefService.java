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
     * 导入流程定义xml的is，返回流程定义对象
     *
     * @param is
     * @throws Exception
     */
    Definition importXml(InputStream is) throws Exception;

    /**
     * 传入流程定义id、流程定义xml字符串，保存流程定义数据
     *
     * @param def
     * @throws Exception
     */
    void saveXml(Definition def) throws Exception;

    /**
     * 导出流程定义xml的Document对象
     *
     * @param id 流程定义id
     * @return
     */
    Document exportXml(Long id);

    /**
     * 获取流程定义xml的字符串
     *
     * @param id
     * @return
     */
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
     * 删除流程定义相关数据
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
