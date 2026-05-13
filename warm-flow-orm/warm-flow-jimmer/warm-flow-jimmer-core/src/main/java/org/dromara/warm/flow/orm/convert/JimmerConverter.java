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
package org.dromara.warm.flow.orm.convert;

import org.babyfish.jimmer.DraftObjects;
import org.babyfish.jimmer.ImmutableObjects;
import org.babyfish.jimmer.meta.ImmutableType;
import org.babyfish.jimmer.runtime.Internal;
import org.dromara.warm.flow.core.entity.RootEntity;
import org.dromara.warm.flow.orm.entity.*;
import org.dromara.warm.flow.orm.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public final class JimmerConverter {

    private JimmerConverter() {
    }

    public static Object toModel(RootEntity entity) {
        if (entity == null) {
            return null;
        }
        if (entity instanceof FlowDefinition) {
            return toModel((FlowDefinition) entity);
        }
        if (entity instanceof FlowForm) {
            return toModel((FlowForm) entity);
        }
        if (entity instanceof FlowHisTask) {
            return toModel((FlowHisTask) entity);
        }
        if (entity instanceof FlowInstance) {
            return toModel((FlowInstance) entity);
        }
        if (entity instanceof FlowNode) {
            return toModel((FlowNode) entity);
        }
        if (entity instanceof FlowSkip) {
            return toModel((FlowSkip) entity);
        }
        if (entity instanceof FlowTask) {
            return toModel((FlowTask) entity);
        }
        if (entity instanceof FlowUser) {
            return toModel((FlowUser) entity);
        }
        throw new IllegalArgumentException("Unsupported warm-flow Jimmer entity: " + entity.getClass().getName());
    }

    public static FlowDefinitionModel toModel(FlowDefinition entity) {
        if (entity == null) {
            return null;
        }
        return (FlowDefinitionModel) Internal.produce(ImmutableType.get(FlowDefinitionModel.class), null, draft -> {
            if (entity.getId() == null) {
                throw new IllegalArgumentException("FlowDefinition.id is required for Jimmer persistence");
            }
            DraftObjects.set(draft, "id", entity.getId());
            setIfNotNull(draft, "createTime", entity.getCreateTime());
            setIfNotNull(draft, "updateTime", entity.getUpdateTime());
            setIfNotNull(draft, "createBy", entity.getCreateBy());
            setIfNotNull(draft, "updateBy", entity.getUpdateBy());
            setIfNotNull(draft, "tenantId", entity.getTenantId());
            setIfNotNull(draft, "delFlag", entity.getDelFlag());
            setIfNotNull(draft, "flowCode", entity.getFlowCode());
            setIfNotNull(draft, "flowName", entity.getFlowName());
            setIfNotNull(draft, "modelValue", entity.getModelValue());
            setIfNotNull(draft, "category", entity.getCategory());
            setIfNotNull(draft, "version", entity.getVersion());
            setIfNotNull(draft, "publishStatus", entity.getIsPublish());
            setIfNotNull(draft, "formCustom", entity.getFormCustom());
            setIfNotNull(draft, "formPath", entity.getFormPath());
            setIfNotNull(draft, "activityStatus", entity.getActivityStatus());
            setIfNotNull(draft, "listenerType", entity.getListenerType());
            setIfNotNull(draft, "listenerPath", entity.getListenerPath());
            setIfNotNull(draft, "ext", entity.getExt());
        });
    }

    public static FlowFormModel toModel(FlowForm entity) {
        if (entity == null) {
            return null;
        }
        return (FlowFormModel) Internal.produce(ImmutableType.get(FlowFormModel.class), null, draft -> {
            if (entity.getId() == null) {
                throw new IllegalArgumentException("FlowForm.id is required for Jimmer persistence");
            }
            DraftObjects.set(draft, "id", entity.getId());
            setIfNotNull(draft, "formCode", entity.getFormCode());
            setIfNotNull(draft, "formName", entity.getFormName());
            setIfNotNull(draft, "version", entity.getVersion());
            setIfNotNull(draft, "publishStatus", entity.getIsPublish());
            setIfNotNull(draft, "formType", entity.getFormType());
            setIfNotNull(draft, "formPath", entity.getFormPath());
            setIfNotNull(draft, "formContent", entity.getFormContent());
            setIfNotNull(draft, "ext", entity.getExt());
            setIfNotNull(draft, "createTime", entity.getCreateTime());
            setIfNotNull(draft, "updateTime", entity.getUpdateTime());
            setIfNotNull(draft, "createBy", entity.getCreateBy());
            setIfNotNull(draft, "updateBy", entity.getUpdateBy());
            setIfNotNull(draft, "tenantId", entity.getTenantId());
            setIfNotNull(draft, "delFlag", entity.getDelFlag());
        });
    }

    public static FlowHisTaskModel toModel(FlowHisTask entity) {
        if (entity == null) {
            return null;
        }
        return (FlowHisTaskModel) Internal.produce(ImmutableType.get(FlowHisTaskModel.class), null, draft -> {
            if (entity.getId() == null) {
                throw new IllegalArgumentException("FlowHisTask.id is required for Jimmer persistence");
            }
            DraftObjects.set(draft, "id", entity.getId());
            setIfNotNull(draft, "createTime", entity.getCreateTime());
            setIfNotNull(draft, "updateTime", entity.getUpdateTime());
            setIfNotNull(draft, "tenantId", entity.getTenantId());
            setIfNotNull(draft, "delFlag", entity.getDelFlag());
            setIfNotNull(draft, "definitionId", entity.getDefinitionId());
            setIfNotNull(draft, "instanceId", entity.getInstanceId());
            setIfNotNull(draft, "taskId", entity.getTaskId());
            setIfNotNull(draft, "cooperateType", entity.getCooperateType());
            setIfNotNull(draft, "nodeCode", entity.getNodeCode());
            setIfNotNull(draft, "nodeName", entity.getNodeName());
            setIfNotNull(draft, "nodeType", entity.getNodeType());
            setIfNotNull(draft, "targetNodeCode", entity.getTargetNodeCode());
            setIfNotNull(draft, "targetNodeName", entity.getTargetNodeName());
            setIfNotNull(draft, "approver", entity.getApprover());
            setIfNotNull(draft, "collaborator", entity.getCollaborator());
            setIfNotNull(draft, "skipType", entity.getSkipType());
            setIfNotNull(draft, "flowStatus", entity.getFlowStatus());
            setIfNotNull(draft, "message", entity.getMessage());
            setIfNotNull(draft, "variable", entity.getVariable());
            setIfNotNull(draft, "ext", entity.getExt());
            setIfNotNull(draft, "formCustom", entity.getFormCustom());
            setIfNotNull(draft, "formPath", entity.getFormPath());
        });
    }

    public static FlowInstanceModel toModel(FlowInstance entity) {
        if (entity == null) {
            return null;
        }
        return (FlowInstanceModel) Internal.produce(ImmutableType.get(FlowInstanceModel.class), null, draft -> {
            if (entity.getId() == null) {
                throw new IllegalArgumentException("FlowInstance.id is required for Jimmer persistence");
            }
            DraftObjects.set(draft, "id", entity.getId());
            setIfNotNull(draft, "createTime", entity.getCreateTime());
            setIfNotNull(draft, "updateTime", entity.getUpdateTime());
            setIfNotNull(draft, "createBy", entity.getCreateBy());
            setIfNotNull(draft, "updateBy", entity.getUpdateBy());
            setIfNotNull(draft, "tenantId", entity.getTenantId());
            setIfNotNull(draft, "delFlag", entity.getDelFlag());
            setIfNotNull(draft, "definitionId", entity.getDefinitionId());
            setIfNotNull(draft, "businessId", entity.getBusinessId());
            setIfNotNull(draft, "nodeType", entity.getNodeType());
            setIfNotNull(draft, "nodeCode", entity.getNodeCode());
            setIfNotNull(draft, "nodeName", entity.getNodeName());
            setIfNotNull(draft, "variable", entity.getVariable());
            setIfNotNull(draft, "flowStatus", entity.getFlowStatus());
            setIfNotNull(draft, "activityStatus", entity.getActivityStatus());
            setIfNotNull(draft, "formCustom", entity.getFormCustom());
            setIfNotNull(draft, "formPath", entity.getFormPath());
            setIfNotNull(draft, "defJson", entity.getDefJson());
            setIfNotNull(draft, "ext", entity.getExt());
        });
    }

    public static FlowNodeModel toModel(FlowNode entity) {
        if (entity == null) {
            return null;
        }
        return (FlowNodeModel) Internal.produce(ImmutableType.get(FlowNodeModel.class), null, draft -> {
            if (entity.getId() == null) {
                throw new IllegalArgumentException("FlowNode.id is required for Jimmer persistence");
            }
            DraftObjects.set(draft, "id", entity.getId());
            setIfNotNull(draft, "createTime", entity.getCreateTime());
            setIfNotNull(draft, "updateTime", entity.getUpdateTime());
            setIfNotNull(draft, "createBy", entity.getCreateBy());
            setIfNotNull(draft, "updateBy", entity.getUpdateBy());
            setIfNotNull(draft, "tenantId", entity.getTenantId());
            setIfNotNull(draft, "delFlag", entity.getDelFlag());
            setIfNotNull(draft, "nodeType", entity.getNodeType());
            setIfNotNull(draft, "definitionId", entity.getDefinitionId());
            setIfNotNull(draft, "nodeCode", entity.getNodeCode());
            setIfNotNull(draft, "nodeName", entity.getNodeName());
            setIfNotNull(draft, "nodeRatio", entity.getNodeRatio());
            setIfNotNull(draft, "permissionFlag", entity.getPermissionFlag());
            setIfNotNull(draft, "coordinate", entity.getCoordinate());
            setIfNotNull(draft, "anyNodeSkip", entity.getAnyNodeSkip());
            setIfNotNull(draft, "listenerType", entity.getListenerType());
            setIfNotNull(draft, "listenerPath", entity.getListenerPath());
            setIfNotNull(draft, "formCustom", entity.getFormCustom());
            setIfNotNull(draft, "formPath", entity.getFormPath());
            setIfNotNull(draft, "ext", entity.getExt());
            setIfNotNull(draft, "version", entity.getVersion());
        });
    }

    public static FlowSkipModel toModel(FlowSkip entity) {
        if (entity == null) {
            return null;
        }
        return (FlowSkipModel) Internal.produce(ImmutableType.get(FlowSkipModel.class), null, draft -> {
            if (entity.getId() == null) {
                throw new IllegalArgumentException("FlowSkip.id is required for Jimmer persistence");
            }
            DraftObjects.set(draft, "id", entity.getId());
            setIfNotNull(draft, "createTime", entity.getCreateTime());
            setIfNotNull(draft, "updateTime", entity.getUpdateTime());
            setIfNotNull(draft, "createBy", entity.getCreateBy());
            setIfNotNull(draft, "updateBy", entity.getUpdateBy());
            setIfNotNull(draft, "tenantId", entity.getTenantId());
            setIfNotNull(draft, "delFlag", entity.getDelFlag());
            setIfNotNull(draft, "definitionId", entity.getDefinitionId());
            setIfNotNull(draft, "nodeId", entity.getNodeId());
            setIfNotNull(draft, "nowNodeCode", entity.getNowNodeCode());
            setIfNotNull(draft, "nowNodeType", entity.getNowNodeType());
            setIfNotNull(draft, "nextNodeCode", entity.getNextNodeCode());
            setIfNotNull(draft, "nextNodeType", entity.getNextNodeType());
            setIfNotNull(draft, "skipName", entity.getSkipName());
            setIfNotNull(draft, "skipType", entity.getSkipType());
            setIfNotNull(draft, "skipCondition", entity.getSkipCondition());
            setIfNotNull(draft, "coordinate", entity.getCoordinate());
        });
    }

    public static FlowTaskModel toModel(FlowTask entity) {
        if (entity == null) {
            return null;
        }
        return (FlowTaskModel) Internal.produce(ImmutableType.get(FlowTaskModel.class), null, draft -> {
            if (entity.getId() == null) {
                throw new IllegalArgumentException("FlowTask.id is required for Jimmer persistence");
            }
            DraftObjects.set(draft, "id", entity.getId());
            setIfNotNull(draft, "createTime", entity.getCreateTime());
            setIfNotNull(draft, "updateTime", entity.getUpdateTime());
            setIfNotNull(draft, "createBy", entity.getCreateBy());
            setIfNotNull(draft, "updateBy", entity.getUpdateBy());
            setIfNotNull(draft, "tenantId", entity.getTenantId());
            setIfNotNull(draft, "delFlag", entity.getDelFlag());
            setIfNotNull(draft, "definitionId", entity.getDefinitionId());
            setIfNotNull(draft, "instanceId", entity.getInstanceId());
            setIfNotNull(draft, "nodeCode", entity.getNodeCode());
            setIfNotNull(draft, "nodeName", entity.getNodeName());
            setIfNotNull(draft, "nodeType", entity.getNodeType());
            setIfNotNull(draft, "flowStatus", entity.getFlowStatus());
            setIfNotNull(draft, "formCustom", entity.getFormCustom());
            setIfNotNull(draft, "formPath", entity.getFormPath());
        });
    }

    public static FlowUserModel toModel(FlowUser entity) {
        if (entity == null) {
            return null;
        }
        return (FlowUserModel) Internal.produce(ImmutableType.get(FlowUserModel.class), null, draft -> {
            if (entity.getId() == null) {
                throw new IllegalArgumentException("FlowUser.id is required for Jimmer persistence");
            }
            DraftObjects.set(draft, "id", entity.getId());
            setIfNotNull(draft, "createTime", entity.getCreateTime());
            setIfNotNull(draft, "updateTime", entity.getUpdateTime());
            setIfNotNull(draft, "createBy", entity.getCreateBy());
            setIfNotNull(draft, "updateBy", entity.getUpdateBy());
            setIfNotNull(draft, "tenantId", entity.getTenantId());
            setIfNotNull(draft, "delFlag", entity.getDelFlag());
            setIfNotNull(draft, "type", entity.getType());
            setIfNotNull(draft, "processedBy", entity.getProcessedBy());
            setIfNotNull(draft, "associated", entity.getAssociated());
        });
    }


    @SuppressWarnings("unchecked")
    public static <T extends RootEntity> T fromModel(Object model, Supplier<T> supplier) {
        if (model == null) {
            return null;
        }
        if (model instanceof FlowDefinitionModel) {
            return (T) fromModel((FlowDefinitionModel) model);
        }
        if (model instanceof FlowFormModel) {
            return (T) fromModel((FlowFormModel) model);
        }
        if (model instanceof FlowHisTaskModel) {
            return (T) fromModel((FlowHisTaskModel) model);
        }
        if (model instanceof FlowInstanceModel) {
            return (T) fromModel((FlowInstanceModel) model);
        }
        if (model instanceof FlowNodeModel) {
            return (T) fromModel((FlowNodeModel) model);
        }
        if (model instanceof FlowSkipModel) {
            return (T) fromModel((FlowSkipModel) model);
        }
        if (model instanceof FlowTaskModel) {
            return (T) fromModel((FlowTaskModel) model);
        }
        if (model instanceof FlowUserModel) {
            return (T) fromModel((FlowUserModel) model);
        }
        throw new IllegalArgumentException("Unsupported Jimmer model: " + model.getClass().getName());
    }


    public static <T extends RootEntity> List<T> fromModels(List<?> models, Supplier<T> supplier) {
        List<T> entities = new ArrayList<>();
        if (models == null) {
            return entities;
        }
        for (Object model : models) {
            entities.add(fromModel(model, supplier));
        }
        return entities;
    }

    public static FlowDefinition fromModel(FlowDefinitionModel model) {
        if (model == null) {
            return null;
        }
        FlowDefinition entity = new FlowDefinition();
        entity.setId(model.id());
        if (ImmutableObjects.isLoaded(model, "createTime")) {
            entity.setCreateTime(model.createTime());
        }
        if (ImmutableObjects.isLoaded(model, "updateTime")) {
            entity.setUpdateTime(model.updateTime());
        }
        if (ImmutableObjects.isLoaded(model, "createBy")) {
            entity.setCreateBy(model.createBy());
        }
        if (ImmutableObjects.isLoaded(model, "updateBy")) {
            entity.setUpdateBy(model.updateBy());
        }
        if (ImmutableObjects.isLoaded(model, "tenantId")) {
            entity.setTenantId(model.tenantId());
        }
        if (ImmutableObjects.isLoaded(model, "delFlag")) {
            entity.setDelFlag(model.delFlag());
        }
        if (ImmutableObjects.isLoaded(model, "flowCode")) {
            entity.setFlowCode(model.flowCode());
        }
        if (ImmutableObjects.isLoaded(model, "flowName")) {
            entity.setFlowName(model.flowName());
        }
        if (ImmutableObjects.isLoaded(model, "modelValue")) {
            entity.setModelValue(model.modelValue());
        }
        if (ImmutableObjects.isLoaded(model, "category")) {
            entity.setCategory(model.category());
        }
        if (ImmutableObjects.isLoaded(model, "version")) {
            entity.setVersion(model.version());
        }
        if (ImmutableObjects.isLoaded(model, "publishStatus")) {
            entity.setIsPublish(model.publishStatus());
        }
        if (ImmutableObjects.isLoaded(model, "formCustom")) {
            entity.setFormCustom(model.formCustom());
        }
        if (ImmutableObjects.isLoaded(model, "formPath")) {
            entity.setFormPath(model.formPath());
        }
        if (ImmutableObjects.isLoaded(model, "activityStatus")) {
            entity.setActivityStatus(model.activityStatus());
        }
        if (ImmutableObjects.isLoaded(model, "listenerType")) {
            entity.setListenerType(model.listenerType());
        }
        if (ImmutableObjects.isLoaded(model, "listenerPath")) {
            entity.setListenerPath(model.listenerPath());
        }
        if (ImmutableObjects.isLoaded(model, "ext")) {
            entity.setExt(model.ext());
        }
        return entity;
    }

    public static FlowForm fromModel(FlowFormModel model) {
        if (model == null) {
            return null;
        }
        FlowForm entity = new FlowForm();
        entity.setId(model.id());
        if (ImmutableObjects.isLoaded(model, "formCode")) {
            entity.setFormCode(model.formCode());
        }
        if (ImmutableObjects.isLoaded(model, "formName")) {
            entity.setFormName(model.formName());
        }
        if (ImmutableObjects.isLoaded(model, "version")) {
            entity.setVersion(model.version());
        }
        if (ImmutableObjects.isLoaded(model, "publishStatus")) {
            entity.setIsPublish(model.publishStatus());
        }
        if (ImmutableObjects.isLoaded(model, "formType")) {
            entity.setFormType(model.formType());
        }
        if (ImmutableObjects.isLoaded(model, "formPath")) {
            entity.setFormPath(model.formPath());
        }
        if (ImmutableObjects.isLoaded(model, "formContent")) {
            entity.setFormContent(model.formContent());
        }
        if (ImmutableObjects.isLoaded(model, "ext")) {
            entity.setExt(model.ext());
        }
        if (ImmutableObjects.isLoaded(model, "createTime")) {
            entity.setCreateTime(model.createTime());
        }
        if (ImmutableObjects.isLoaded(model, "updateTime")) {
            entity.setUpdateTime(model.updateTime());
        }
        if (ImmutableObjects.isLoaded(model, "createBy")) {
            entity.setCreateBy(model.createBy());
        }
        if (ImmutableObjects.isLoaded(model, "updateBy")) {
            entity.setUpdateBy(model.updateBy());
        }
        if (ImmutableObjects.isLoaded(model, "tenantId")) {
            entity.setTenantId(model.tenantId());
        }
        if (ImmutableObjects.isLoaded(model, "delFlag")) {
            entity.setDelFlag(model.delFlag());
        }
        return entity;
    }

    public static FlowHisTask fromModel(FlowHisTaskModel model) {
        if (model == null) {
            return null;
        }
        FlowHisTask entity = new FlowHisTask();
        entity.setId(model.id());
        if (ImmutableObjects.isLoaded(model, "createTime")) {
            entity.setCreateTime(model.createTime());
        }
        if (ImmutableObjects.isLoaded(model, "updateTime")) {
            entity.setUpdateTime(model.updateTime());
        }
        if (ImmutableObjects.isLoaded(model, "tenantId")) {
            entity.setTenantId(model.tenantId());
        }
        if (ImmutableObjects.isLoaded(model, "delFlag")) {
            entity.setDelFlag(model.delFlag());
        }
        if (ImmutableObjects.isLoaded(model, "definitionId")) {
            entity.setDefinitionId(model.definitionId());
        }
        if (ImmutableObjects.isLoaded(model, "instanceId")) {
            entity.setInstanceId(model.instanceId());
        }
        if (ImmutableObjects.isLoaded(model, "taskId")) {
            entity.setTaskId(model.taskId());
        }
        if (ImmutableObjects.isLoaded(model, "cooperateType")) {
            entity.setCooperateType(model.cooperateType());
        }
        if (ImmutableObjects.isLoaded(model, "nodeCode")) {
            entity.setNodeCode(model.nodeCode());
        }
        if (ImmutableObjects.isLoaded(model, "nodeName")) {
            entity.setNodeName(model.nodeName());
        }
        if (ImmutableObjects.isLoaded(model, "nodeType")) {
            entity.setNodeType(model.nodeType());
        }
        if (ImmutableObjects.isLoaded(model, "targetNodeCode")) {
            entity.setTargetNodeCode(model.targetNodeCode());
        }
        if (ImmutableObjects.isLoaded(model, "targetNodeName")) {
            entity.setTargetNodeName(model.targetNodeName());
        }
        if (ImmutableObjects.isLoaded(model, "approver")) {
            entity.setApprover(model.approver());
        }
        if (ImmutableObjects.isLoaded(model, "collaborator")) {
            entity.setCollaborator(model.collaborator());
        }
        if (ImmutableObjects.isLoaded(model, "skipType")) {
            entity.setSkipType(model.skipType());
        }
        if (ImmutableObjects.isLoaded(model, "flowStatus")) {
            entity.setFlowStatus(model.flowStatus());
        }
        if (ImmutableObjects.isLoaded(model, "message")) {
            entity.setMessage(model.message());
        }
        if (ImmutableObjects.isLoaded(model, "variable")) {
            entity.setVariable(model.variable());
        }
        if (ImmutableObjects.isLoaded(model, "ext")) {
            entity.setExt(model.ext());
        }
        if (ImmutableObjects.isLoaded(model, "formCustom")) {
            entity.setFormCustom(model.formCustom());
        }
        if (ImmutableObjects.isLoaded(model, "formPath")) {
            entity.setFormPath(model.formPath());
        }
        return entity;
    }

    public static FlowInstance fromModel(FlowInstanceModel model) {
        if (model == null) {
            return null;
        }
        FlowInstance entity = new FlowInstance();
        entity.setId(model.id());
        if (ImmutableObjects.isLoaded(model, "createTime")) {
            entity.setCreateTime(model.createTime());
        }
        if (ImmutableObjects.isLoaded(model, "updateTime")) {
            entity.setUpdateTime(model.updateTime());
        }
        if (ImmutableObjects.isLoaded(model, "createBy")) {
            entity.setCreateBy(model.createBy());
        }
        if (ImmutableObjects.isLoaded(model, "updateBy")) {
            entity.setUpdateBy(model.updateBy());
        }
        if (ImmutableObjects.isLoaded(model, "tenantId")) {
            entity.setTenantId(model.tenantId());
        }
        if (ImmutableObjects.isLoaded(model, "delFlag")) {
            entity.setDelFlag(model.delFlag());
        }
        if (ImmutableObjects.isLoaded(model, "definitionId")) {
            entity.setDefinitionId(model.definitionId());
        }
        if (ImmutableObjects.isLoaded(model, "businessId")) {
            entity.setBusinessId(model.businessId());
        }
        if (ImmutableObjects.isLoaded(model, "nodeType")) {
            entity.setNodeType(model.nodeType());
        }
        if (ImmutableObjects.isLoaded(model, "nodeCode")) {
            entity.setNodeCode(model.nodeCode());
        }
        if (ImmutableObjects.isLoaded(model, "nodeName")) {
            entity.setNodeName(model.nodeName());
        }
        if (ImmutableObjects.isLoaded(model, "variable")) {
            entity.setVariable(model.variable());
        }
        if (ImmutableObjects.isLoaded(model, "flowStatus")) {
            entity.setFlowStatus(model.flowStatus());
        }
        if (ImmutableObjects.isLoaded(model, "activityStatus")) {
            entity.setActivityStatus(model.activityStatus());
        }
        if (ImmutableObjects.isLoaded(model, "formCustom")) {
            entity.setFormCustom(model.formCustom());
        }
        if (ImmutableObjects.isLoaded(model, "formPath")) {
            entity.setFormPath(model.formPath());
        }
        if (ImmutableObjects.isLoaded(model, "defJson")) {
            entity.setDefJson(model.defJson());
        }
        if (ImmutableObjects.isLoaded(model, "ext")) {
            entity.setExt(model.ext());
        }
        return entity;
    }

    public static FlowNode fromModel(FlowNodeModel model) {
        if (model == null) {
            return null;
        }
        FlowNode entity = new FlowNode();
        entity.setId(model.id());
        if (ImmutableObjects.isLoaded(model, "createTime")) {
            entity.setCreateTime(model.createTime());
        }
        if (ImmutableObjects.isLoaded(model, "updateTime")) {
            entity.setUpdateTime(model.updateTime());
        }
        if (ImmutableObjects.isLoaded(model, "createBy")) {
            entity.setCreateBy(model.createBy());
        }
        if (ImmutableObjects.isLoaded(model, "updateBy")) {
            entity.setUpdateBy(model.updateBy());
        }
        if (ImmutableObjects.isLoaded(model, "tenantId")) {
            entity.setTenantId(model.tenantId());
        }
        if (ImmutableObjects.isLoaded(model, "delFlag")) {
            entity.setDelFlag(model.delFlag());
        }
        if (ImmutableObjects.isLoaded(model, "nodeType")) {
            entity.setNodeType(model.nodeType());
        }
        if (ImmutableObjects.isLoaded(model, "definitionId")) {
            entity.setDefinitionId(model.definitionId());
        }
        if (ImmutableObjects.isLoaded(model, "nodeCode")) {
            entity.setNodeCode(model.nodeCode());
        }
        if (ImmutableObjects.isLoaded(model, "nodeName")) {
            entity.setNodeName(model.nodeName());
        }
        if (ImmutableObjects.isLoaded(model, "nodeRatio")) {
            entity.setNodeRatio(model.nodeRatio());
        }
        if (ImmutableObjects.isLoaded(model, "permissionFlag")) {
            entity.setPermissionFlag(model.permissionFlag());
        }
        if (ImmutableObjects.isLoaded(model, "coordinate")) {
            entity.setCoordinate(model.coordinate());
        }
        if (ImmutableObjects.isLoaded(model, "anyNodeSkip")) {
            entity.setAnyNodeSkip(model.anyNodeSkip());
        }
        if (ImmutableObjects.isLoaded(model, "listenerType")) {
            entity.setListenerType(model.listenerType());
        }
        if (ImmutableObjects.isLoaded(model, "listenerPath")) {
            entity.setListenerPath(model.listenerPath());
        }
        if (ImmutableObjects.isLoaded(model, "formCustom")) {
            entity.setFormCustom(model.formCustom());
        }
        if (ImmutableObjects.isLoaded(model, "formPath")) {
            entity.setFormPath(model.formPath());
        }
        if (ImmutableObjects.isLoaded(model, "ext")) {
            entity.setExt(model.ext());
        }
        if (ImmutableObjects.isLoaded(model, "version")) {
            entity.setVersion(model.version());
        }
        return entity;
    }

    public static FlowSkip fromModel(FlowSkipModel model) {
        if (model == null) {
            return null;
        }
        FlowSkip entity = new FlowSkip();
        entity.setId(model.id());
        if (ImmutableObjects.isLoaded(model, "createTime")) {
            entity.setCreateTime(model.createTime());
        }
        if (ImmutableObjects.isLoaded(model, "updateTime")) {
            entity.setUpdateTime(model.updateTime());
        }
        if (ImmutableObjects.isLoaded(model, "createBy")) {
            entity.setCreateBy(model.createBy());
        }
        if (ImmutableObjects.isLoaded(model, "updateBy")) {
            entity.setUpdateBy(model.updateBy());
        }
        if (ImmutableObjects.isLoaded(model, "tenantId")) {
            entity.setTenantId(model.tenantId());
        }
        if (ImmutableObjects.isLoaded(model, "delFlag")) {
            entity.setDelFlag(model.delFlag());
        }
        if (ImmutableObjects.isLoaded(model, "definitionId")) {
            entity.setDefinitionId(model.definitionId());
        }
        if (ImmutableObjects.isLoaded(model, "nodeId")) {
            entity.setNodeId(model.nodeId());
        }
        if (ImmutableObjects.isLoaded(model, "nowNodeCode")) {
            entity.setNowNodeCode(model.nowNodeCode());
        }
        if (ImmutableObjects.isLoaded(model, "nowNodeType")) {
            entity.setNowNodeType(model.nowNodeType());
        }
        if (ImmutableObjects.isLoaded(model, "nextNodeCode")) {
            entity.setNextNodeCode(model.nextNodeCode());
        }
        if (ImmutableObjects.isLoaded(model, "nextNodeType")) {
            entity.setNextNodeType(model.nextNodeType());
        }
        if (ImmutableObjects.isLoaded(model, "skipName")) {
            entity.setSkipName(model.skipName());
        }
        if (ImmutableObjects.isLoaded(model, "skipType")) {
            entity.setSkipType(model.skipType());
        }
        if (ImmutableObjects.isLoaded(model, "skipCondition")) {
            entity.setSkipCondition(model.skipCondition());
        }
        if (ImmutableObjects.isLoaded(model, "coordinate")) {
            entity.setCoordinate(model.coordinate());
        }
        return entity;
    }

    public static FlowTask fromModel(FlowTaskModel model) {
        if (model == null) {
            return null;
        }
        FlowTask entity = new FlowTask();
        entity.setId(model.id());
        if (ImmutableObjects.isLoaded(model, "createTime")) {
            entity.setCreateTime(model.createTime());
        }
        if (ImmutableObjects.isLoaded(model, "updateTime")) {
            entity.setUpdateTime(model.updateTime());
        }
        if (ImmutableObjects.isLoaded(model, "createBy")) {
            entity.setCreateBy(model.createBy());
        }
        if (ImmutableObjects.isLoaded(model, "updateBy")) {
            entity.setUpdateBy(model.updateBy());
        }
        if (ImmutableObjects.isLoaded(model, "tenantId")) {
            entity.setTenantId(model.tenantId());
        }
        if (ImmutableObjects.isLoaded(model, "delFlag")) {
            entity.setDelFlag(model.delFlag());
        }
        if (ImmutableObjects.isLoaded(model, "definitionId")) {
            entity.setDefinitionId(model.definitionId());
        }
        if (ImmutableObjects.isLoaded(model, "instanceId")) {
            entity.setInstanceId(model.instanceId());
        }
        if (ImmutableObjects.isLoaded(model, "nodeCode")) {
            entity.setNodeCode(model.nodeCode());
        }
        if (ImmutableObjects.isLoaded(model, "nodeName")) {
            entity.setNodeName(model.nodeName());
        }
        if (ImmutableObjects.isLoaded(model, "nodeType")) {
            entity.setNodeType(model.nodeType());
        }
        if (ImmutableObjects.isLoaded(model, "flowStatus")) {
            entity.setFlowStatus(model.flowStatus());
        }
        if (ImmutableObjects.isLoaded(model, "formCustom")) {
            entity.setFormCustom(model.formCustom());
        }
        if (ImmutableObjects.isLoaded(model, "formPath")) {
            entity.setFormPath(model.formPath());
        }
        return entity;
    }

    public static FlowUser fromModel(FlowUserModel model) {
        if (model == null) {
            return null;
        }
        FlowUser entity = new FlowUser();
        entity.setId(model.id());
        if (ImmutableObjects.isLoaded(model, "createTime")) {
            entity.setCreateTime(model.createTime());
        }
        if (ImmutableObjects.isLoaded(model, "updateTime")) {
            entity.setUpdateTime(model.updateTime());
        }
        if (ImmutableObjects.isLoaded(model, "createBy")) {
            entity.setCreateBy(model.createBy());
        }
        if (ImmutableObjects.isLoaded(model, "updateBy")) {
            entity.setUpdateBy(model.updateBy());
        }
        if (ImmutableObjects.isLoaded(model, "tenantId")) {
            entity.setTenantId(model.tenantId());
        }
        if (ImmutableObjects.isLoaded(model, "delFlag")) {
            entity.setDelFlag(model.delFlag());
        }
        if (ImmutableObjects.isLoaded(model, "type")) {
            entity.setType(model.type());
        }
        if (ImmutableObjects.isLoaded(model, "processedBy")) {
            entity.setProcessedBy(model.processedBy());
        }
        if (ImmutableObjects.isLoaded(model, "associated")) {
            entity.setAssociated(model.associated());
        }
        return entity;
    }


    public static void copyLoadedModelValues(Object model, RootEntity entity) {
        if (model == null || entity == null) {
            return;
        }
        RootEntity loaded = fromModel(model, () -> entity);
        copyLoadedValues(loaded, entity);
    }

    private static void copyLoadedValues(RootEntity source, RootEntity target) {
        if (source == null || target == null) {
            return;
        }
        target.setId(source.getId())
            .setCreateTime(source.getCreateTime())
            .setUpdateTime(source.getUpdateTime())
            .setCreateBy(source.getCreateBy())
            .setUpdateBy(source.getUpdateBy())
            .setTenantId(source.getTenantId())
            .setDelFlag(source.getDelFlag());
        if (source instanceof FlowDefinition && target instanceof FlowDefinition) {
            FlowDefinition s = (FlowDefinition) source;
            FlowDefinition t = (FlowDefinition) target;
            t.setFlowCode(s.getFlowCode());
            t.setFlowName(s.getFlowName());
            t.setModelValue(s.getModelValue());
            t.setCategory(s.getCategory());
            t.setVersion(s.getVersion());
            t.setIsPublish(s.getIsPublish());
            t.setFormCustom(s.getFormCustom());
            t.setFormPath(s.getFormPath());
            t.setActivityStatus(s.getActivityStatus());
            t.setListenerType(s.getListenerType());
            t.setListenerPath(s.getListenerPath());
            t.setExt(s.getExt());
            return;
        }
        if (source instanceof FlowForm && target instanceof FlowForm) {
            FlowForm s = (FlowForm) source;
            FlowForm t = (FlowForm) target;
            t.setFormCode(s.getFormCode());
            t.setFormName(s.getFormName());
            t.setVersion(s.getVersion());
            t.setIsPublish(s.getIsPublish());
            t.setFormType(s.getFormType());
            t.setFormPath(s.getFormPath());
            t.setFormContent(s.getFormContent());
            t.setExt(s.getExt());
            return;
        }
        if (source instanceof FlowHisTask && target instanceof FlowHisTask) {
            FlowHisTask s = (FlowHisTask) source;
            FlowHisTask t = (FlowHisTask) target;
            t.setDefinitionId(s.getDefinitionId());
            t.setInstanceId(s.getInstanceId());
            t.setTaskId(s.getTaskId());
            t.setCooperateType(s.getCooperateType());
            t.setNodeCode(s.getNodeCode());
            t.setNodeName(s.getNodeName());
            t.setNodeType(s.getNodeType());
            t.setTargetNodeCode(s.getTargetNodeCode());
            t.setTargetNodeName(s.getTargetNodeName());
            t.setApprover(s.getApprover());
            t.setCollaborator(s.getCollaborator());
            t.setSkipType(s.getSkipType());
            t.setFlowStatus(s.getFlowStatus());
            t.setMessage(s.getMessage());
            t.setVariable(s.getVariable());
            t.setExt(s.getExt());
            t.setFormCustom(s.getFormCustom());
            t.setFormPath(s.getFormPath());
            return;
        }
        if (source instanceof FlowInstance && target instanceof FlowInstance) {
            FlowInstance s = (FlowInstance) source;
            FlowInstance t = (FlowInstance) target;
            t.setDefinitionId(s.getDefinitionId());
            t.setBusinessId(s.getBusinessId());
            t.setNodeType(s.getNodeType());
            t.setNodeCode(s.getNodeCode());
            t.setNodeName(s.getNodeName());
            t.setVariable(s.getVariable());
            t.setFlowStatus(s.getFlowStatus());
            t.setActivityStatus(s.getActivityStatus());
            t.setFormCustom(s.getFormCustom());
            t.setFormPath(s.getFormPath());
            t.setDefJson(s.getDefJson());
            t.setExt(s.getExt());
            return;
        }
        if (source instanceof FlowNode && target instanceof FlowNode) {
            FlowNode s = (FlowNode) source;
            FlowNode t = (FlowNode) target;
            t.setNodeType(s.getNodeType());
            t.setDefinitionId(s.getDefinitionId());
            t.setNodeCode(s.getNodeCode());
            t.setNodeName(s.getNodeName());
            t.setNodeRatio(s.getNodeRatio());
            t.setPermissionFlag(s.getPermissionFlag());
            t.setCoordinate(s.getCoordinate());
            t.setAnyNodeSkip(s.getAnyNodeSkip());
            t.setListenerType(s.getListenerType());
            t.setListenerPath(s.getListenerPath());
            t.setFormCustom(s.getFormCustom());
            t.setFormPath(s.getFormPath());
            t.setExt(s.getExt());
            t.setVersion(s.getVersion());
            return;
        }
        if (source instanceof FlowSkip && target instanceof FlowSkip) {
            FlowSkip s = (FlowSkip) source;
            FlowSkip t = (FlowSkip) target;
            t.setDefinitionId(s.getDefinitionId());
            t.setNodeId(s.getNodeId());
            t.setNowNodeCode(s.getNowNodeCode());
            t.setNowNodeType(s.getNowNodeType());
            t.setNextNodeCode(s.getNextNodeCode());
            t.setNextNodeType(s.getNextNodeType());
            t.setSkipName(s.getSkipName());
            t.setSkipType(s.getSkipType());
            t.setSkipCondition(s.getSkipCondition());
            t.setCoordinate(s.getCoordinate());
            return;
        }
        if (source instanceof FlowTask && target instanceof FlowTask) {
            FlowTask s = (FlowTask) source;
            FlowTask t = (FlowTask) target;
            t.setDefinitionId(s.getDefinitionId());
            t.setInstanceId(s.getInstanceId());
            t.setNodeCode(s.getNodeCode());
            t.setNodeName(s.getNodeName());
            t.setNodeType(s.getNodeType());
            t.setFlowStatus(s.getFlowStatus());
            t.setFormCustom(s.getFormCustom());
            t.setFormPath(s.getFormPath());
            return;
        }
        if (source instanceof FlowUser && target instanceof FlowUser) {
            FlowUser s = (FlowUser) source;
            FlowUser t = (FlowUser) target;
            t.setType(s.getType());
            t.setProcessedBy(s.getProcessedBy());
            t.setAssociated(s.getAssociated());
            return;
        }
    }

    private static void setIfNotNull(Object draft, String prop, Object value) {
        if (value != null) {
            DraftObjects.set(draft, prop, value);
        }
    }
}
