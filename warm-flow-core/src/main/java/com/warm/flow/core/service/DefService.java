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
     * 根据流程实例ID获取流程图的图片流(渲染颜色)
     *
     * @param instanceId
     * @return
     * @throws IOException
     */
    String flowChart(Long instanceId) throws IOException;

    /**
     * 根据流程定义ID获取流程图的图片流(不渲染颜色)
     * @param definitionId
     * @return
     * @throws IOException
     */
    String flowChartNoColor(Long definitionId) throws IOException;

    /**
     * 激活流程
     * @param id 流程定义id: [必传]
     */
    boolean active(Long id);

    /**
     * 挂起流程：流程定义挂起后，相关的流程实例都无法继续流转
     * @param id 流程定义id: [必传]
     */
    boolean unActive(Long id);
}
