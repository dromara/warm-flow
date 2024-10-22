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
package com.warm.flow.ui.vo;

import java.util.List;

/**
 * 办理人选择组对象
 *
 * @author warm
 */
public class SelectGroup {

    private String label;

    private String value;

    private List<SelectGroup> options;

    public String getLabel() {
        return label;
    }

    public SelectGroup setLabel(String label) {
        this.label = label;
        return this;
    }

    public String getValue() {
        return value;
    }

    public SelectGroup setValue(String value) {
        this.value = value;
        return this;
    }

    public List<SelectGroup> getOptions() {
        return options;
    }

    public SelectGroup setOptions(List<SelectGroup> options) {
        this.options = options;
        return this;
    }
}

