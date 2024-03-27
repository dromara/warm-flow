package com.warm.flow.orm.mapper;

import com.warm.flow.core.entity.Skip;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 节点跳转关联Mapper接口
 *
 * @author warm
 * @date 2023-03-29
 */
public interface FlowSkipMapper extends WarmMapper<Skip> {

    /**
     * 获取当前节点跳转
     *
     * @param definitionId
     * @param nowNodeCode
     * @return
     */
    List<Skip> queryByDefAndCode(@Param("definitionId") Long definitionId, @Param("nowNodeCode") String nowNodeCode);

}
