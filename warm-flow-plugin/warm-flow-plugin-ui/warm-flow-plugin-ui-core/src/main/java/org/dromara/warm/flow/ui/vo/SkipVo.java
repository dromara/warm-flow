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

/**
 * 节点跳转关联对象Vo
 *
 * @author warm
 * @since 2023-03-29
 */
public class SkipVo {

    /**
     * 当前流程节点的编码
     */
    private String nowNodeCode;

    /**
     * 下一个流程节点的编码
     */
    private String nextNodeCode;

    /**
     * 跳转名称
     */
    private String skipName;

    /**
     * 跳转类型（PASS审批通过 REJECT退回）
     */
    private String skipType;

    /**
     * 跳转条件
     */
    private String skipCondition;

    /**
     * 流程跳转坐标
     */
    private String coordinate;

    public String getNowNodeCode() {
        return nowNodeCode;
    }

    public SkipVo setNowNodeCode(String nowNodeCode) {
        this.nowNodeCode = nowNodeCode;
        return this;
    }

    public String getNextNodeCode() {
        return nextNodeCode;
    }

    public SkipVo setNextNodeCode(String nextNodeCode) {
        this.nextNodeCode = nextNodeCode;
        return this;
    }

    public String getSkipName() {
        return skipName;
    }

    public SkipVo setSkipName(String skipName) {
        this.skipName = skipName;
        return this;
    }

    public String getSkipType() {
        return skipType;
    }

    public SkipVo setSkipType(String skipType) {
        this.skipType = skipType;
        return this;
    }

    public String getSkipCondition() {
        return skipCondition;
    }

    public SkipVo setSkipCondition(String skipCondition) {
        this.skipCondition = skipCondition;
        return this;
    }

    public String getCoordinate() {
        return coordinate;
    }

    public SkipVo setCoordinate(String coordinate) {
        this.coordinate = coordinate;
        return this;
    }

}
