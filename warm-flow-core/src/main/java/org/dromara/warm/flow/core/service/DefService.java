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
package org.dromara.warm.flow.core.service;

import org.dom4j.Document;
import org.dromara.warm.flow.core.dto.DefJson;
import org.dromara.warm.flow.core.dto.FlowCombine;
import org.dromara.warm.flow.core.entity.Definition;
import org.dromara.warm.flow.core.entity.Node;
import org.dromara.warm.flow.core.entity.Skip;
import org.dromara.warm.flow.core.orm.service.IWarmService;

import java.io.InputStream;
import java.util.List;

/**
 * 流程定义Service接口
 *
 * @author warm
 * @since 2023-03-29
 */
public interface DefService extends IWarmService<Definition> {

    /**
     * 导入流程定义、流程节点和流程跳转数据
     * @param is 流程定义的输入流
     */
    Definition importIs(InputStream is);

    /**
     * 导入流程定义、流程节点和流程跳转数据
     * @param defStr 流程定义的json字符串 {@link DefJson 的json格式}
     */
    Definition importJson(String defStr);

    /**
     * 导入流程定义、流程节点和流程跳转数据
     * @param defJson 流程定义json对象，流程定义、流程节点和流程跳转按照主子集传递
     */
    Definition importDef(DefJson defJson);

    /**
     * 新增工作流定义，并初始化流程节点和流程跳转数据
     *
     * @param definition 流程定义
     * @param nodeList 流程节点
     * @param skipList 流程跳转
     */
    Definition insertFlow(Definition definition, List<Node> nodeList, List<Skip> skipList);

    /**
     * 导入流程定义、流程节点和流程跳转数据
     *
     * @param is 流程定义xml的输入流
     * @deprecated 请使用 {@link #importDef(DefJson)}
     */
    @Deprecated
    Definition importXml(InputStream is) throws Exception;

    /**
     * 读取流程配置文件
     * @param is  流程配置文件输入流
     * @return 流程数据集合
     * @deprecated 请使用 {@link #importDef(DefJson)}
     */
    @Deprecated
    FlowCombine readXml(InputStream is) throws Exception;


    /**
     * 新增流程定义，并初始化流程节点和流程跳转数据
     * @param definition 流程定义对象
     * @return boolean
     */
    boolean saveAndInitNode(Definition definition);

    /**
     * 只新增流程定义表数据
     * @param definition 流程定义对象
     * @return boolean
     */
    boolean checkAndSave(Definition definition);

    /**
     * 保存流程节点和跳转
     *
     * @param defJson 流程定义json对象
     * @author xiarg
     * @since 2024/10/29 16:30
     */
    void saveDef(DefJson defJson) throws Exception;

    /**
     * 保存流程节点和流程跳转数据
     *
     * @param def 流程定义对象
     * @deprecated 请使用 {@link #saveDef(DefJson)}
     */
    @Deprecated
    void saveXml(Definition def) throws Exception;

    /**
     * 保存流程节点和流程跳转数据
     * @param id 流程定义id
     * @param xmlString 流程定义xml字符串
     * @deprecated 请使用 {@link #saveDef(DefJson)}
     */
    @Deprecated
    void saveXml(Long id, String xmlString) throws Exception;

    /**
     * 导出流程定义(流程定义、流程节点和流程跳转数据)xml的Document对象
     *
     * @param id 流程定义id
     * @return Document
     * @deprecated 请使用 {@link #exportJson(Long)}
     */
    @Deprecated
    Document exportXml(Long id);

    /**
     * 导出流程定义(流程定义、流程节点和流程跳转数据)的json字符串
     * @param id 流程定义id
     * @return json字符串
     */
    String exportJson(Long id);

    /**
     * 获取流程定义xml(流程定义、流程节点和流程跳转数据)的字符串
     *
     * @param id 流程定义id
     * @return xmlString
     * @deprecated 请使用 {@link #queryDesign(Long)}
     */
    @Deprecated
    String xmlString(Long id);

    /**
     * 获取流程定义全部数据(包含节点和跳转)
     * @param id 流程定义id
     * @return Definition
     */
    Definition getAllDataDefinition(Long id);

    /**
     * 查询流程设计所需的数据，比如流程图渲染
     * @param id 流程定义id
     * @return 流程定义json对象
     */
    DefJson queryDesign(Long id);

    List<Definition> queryByCodeList(List<String> flowCodeList);

    /**
     * 更新流程定义发布状态
     * @param defIds 流程定义id列表
     * @param publishStatus 流程定义发布状态
     */
    void updatePublishStatus(List<Long> defIds, Integer publishStatus);

    /**
     * 删除流程定义相关数据
     *
     * @param ids 流程定义id列表
     * @return boolean
     */
    boolean removeDef(List<Long> ids);

    /**
     * 发布流程定义
     *
     * @param id 流程定义id
     * @return boolean
     */
    boolean publish(Long id);

    /**
     * 取消发布流程定义
     *
     * @param id 流程定义id
     * @return boolean
     */
    boolean unPublish(Long id);

    /**
     * 复制流程定义
     *
     * @param id 流程定义id
     * @return boolean
     */
    boolean copyDef(Long id);

    /**
     * 激活流程
     * @param id 流程定义id
     */
    boolean active(Long id);

    /**
     * 挂起流程：流程定义挂起后，相关的流程实例都无法继续流转
     * @param id 流程定义id
     */
    boolean unActive(Long id);

    /**
     * 将流程实例数据转换为流程定义数据
     * @param instanceId 指定某个流程实例，如果为空，则将所有流程实例数据转换为流程定义数据
     */
    void hisToDefJson(Long instanceId);
}
