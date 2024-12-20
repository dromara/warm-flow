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
package org.dromara.warm.flow.ui.vo;

import lombok.Getter;
import lombok.Setter;
import org.dromara.warm.flow.ui.dto.Tree;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * TreeSelection树结构实体类
 *
 * @author warm
 */
@Getter
@Setter
public class TreeSelection implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 节点ID
     */
    private String id;

    /**
     * 节点名称
     */
    private String label;

    /**
     * 子节点
     */
    private List<TreeSelection> children;

    public TreeSelection(Tree tree) {
        this.id = tree.getId();
        this.label = tree.getName();
        this.children = tree.getChildren().stream().map(TreeSelection::new).collect(Collectors.toList());
    }


}
