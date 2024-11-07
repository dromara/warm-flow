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
package org.dromara.warm.flow.ui.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 页面左侧树列表
 *
 * @author ruoyi
 */
public class Tree implements Serializable {

    private static final long serialVersionUID = 1L;

    /** ID */
    private String id;

    /** 名称 */
    private String name;

    /** 父ID */
    private String parentId;

    /** 子 */
    private List<Tree> children = new ArrayList<>();

    public String getId() {
        return id;
    }

    public Tree setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public Tree setName(String name) {
        this.name = name;
        return this;
    }

    public String getParentId() {
        return parentId;
    }

    public Tree setParentId(String parentId) {
        this.parentId = parentId;
        return this;
    }

    public List<Tree> getChildren() {
        return children;
    }

    public Tree setChildren(List<Tree> children) {
        this.children = children;
        return this;
    }
}
