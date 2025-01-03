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

import org.dromara.warm.flow.core.chart.FlowChart;
import org.dromara.warm.flow.core.dto.FlowCombine;
import org.dromara.warm.flow.core.entity.Definition;
import org.dromara.warm.flow.core.entity.Node;
import org.dromara.warm.flow.core.entity.Skip;
import org.dromara.warm.flow.core.orm.service.IWarmService;
import org.dom4j.Document;

import java.io.IOException;
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
     *
     * @param is 流程定义xml的输入流
     */
    Definition importXml(InputStream is) throws Exception;

    /**
     * 读取流程配置文件
     * @param is  流程配置文件输入流
     * @return 流程数据集合
     */
    FlowCombine readXml(InputStream is) throws Exception;

    /**
     * 导入流程定义
     * @param combine 流程数据集合
     * @return 流程定义
     */
    Definition importFlow(FlowCombine combine);

    /**
     * 每次只做新增操作,保证新增的flowCode+version是唯一的
     *
     * @param definition 流程定义
     * @param allNodes 所有节点
     * @param allSkips 所有跳转
     */
    void insertFlow(Definition definition, List<Node> allNodes, List<Skip> allSkips);

    /**
     * 保存流程节点和流程跳转数据
     *
     * @param def 流程定义对象
     */
    void saveXml(Definition def) throws Exception;

    /**
     * 保存流程节点和流程跳转数据
     * @param id 流程定义id
     * @param xmlString 流程定义xml字符串
     */
    void saveXml(Long id, String xmlString) throws Exception;

    /**
     * 导出流程定义(流程定义、流程节点和流程跳转数据)xml的Document对象
     *
     * @param id 流程定义id
     * @return Document
     */
    Document exportXml(Long id);

    /**
     * 获取流程定义xml(流程定义、流程节点和流程跳转数据)的字符串
     *
     * @param id 流程定义id
     * @return xmlString
     */
    String xmlString(Long id);

    List<Definition> queryByCodeList(List<String> flowCodeList);

    /**
     * 更新流程定义发布状态
     * @param defIds 流程定义id列表
     * @param publishStatus 流程定义发布状态
     */
    void updatePublishStatus(List<Long> defIds, Integer publishStatus);

    /**
     * 新增流程定义表数据，新增后需要通过saveXml接口保存流程节点和流程跳转数据
     * 校验后新增
     * @param definition 流程定义对象
     * @return boolean
     */
    boolean checkAndSave(Definition definition);

    /**
     * 新增流程定义，并初始化流程节点和流程跳转数据
     * 校验后新增
     * @param definition 流程定义对象
     * @return boolean
     */
    boolean saveAndInitNode(Definition definition);

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
     * 根据流程实例ID,获取流程图的图片流(渲染颜色)
     *
     * @param instanceId 流程实例id
     * @return base64编码的图片流字符串
     */
    String flowChart(Long instanceId) throws IOException;

    /**
     * 根据流程实例ID,获取流程图元数据
     *
     * @param instanceId 流程实例id
     * @return List<FlowChart>
     */
    List<FlowChart> flowChartData(Long instanceId) throws IOException;

    /**
     * 根据流程定义ID,获取流程图的图片流(不渲染颜色)
     * @param definitionId 流程定义id
     * @return base64编码的图片流字符串
     */
    String flowChartNoColor(Long definitionId) throws IOException;

    /**
     * 根据流程定义ID,获取流程图元数据
     * @param definitionId 流程定义id
     * @return List<FlowChart>
     */
    List<FlowChart> flowChartNoColorData(Long definitionId) throws IOException;

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
     * 传入流程定义id、流程定义json字符串，保存流程定义数据
     *
     * @param defId 流程定义id
     * @param jsonString 流程定义json字符串
     * @author xiarg
     * @since 2024/10/29 16:30
     */
    void saveJson(Long defId, String jsonString) throws Exception;

    /**
     * 获取流程定义json的字符串
     *
     * @param defId 流程定义id
     * @return 流程定义的json字符串
     * @author xiarg
     * @since 2024/10/29 16:31
     */
    String jsonString(Long defId);

    /**
     * 入流程定义json的is，返回流程定义对象
     *
     * @param inputStream 流程定义json的inputStream
     * @return 流程定义
     * @author xiarg
     * @since 2024/10/30 16:05
     */
    Definition importJson(InputStream inputStream) throws Exception;

    /**
     * 导出流程定义json的输出流
     *
     * @param defId 流程定义id
     * @return json数据流
     * @author xiarg
     * @since 2024/10/30 16:05
     */
    InputStream exportJson(Long defId);
}
