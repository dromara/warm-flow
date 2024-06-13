package com.warm.flow.orm.mapper;

import com.warm.flow.orm.entity.FlowUser;
import org.apache.ibatis.annotations.Param;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * 流程用户Mapper接口
 *
 * @author xiarg
 * @date 2024/5/10 11:16
 */
public interface FlowUserMapper extends WarmMapper<FlowUser> {

    /**
     * 根据 taskIds 删除
     *
     * @param taskIds 代办任务id集合
     * @return int
     * @author xiarg
     * @date 2024/5/11 11:24
     */
    int deleteByTaskIds(@Param("taskIds") Collection<? extends Serializable> taskIds,
                        @Param("entity") FlowUser entity);

    /**
     * 根据 taskIds 逻辑删除
     *
     * @param taskIds 代办任务id集合
     * @return int
     * @author xiarg
     * @date 2024/5/11 11:24
     */
    int updateByTaskIdsLogic(@Param("taskIds") Collection<? extends Serializable> taskIds,
                             @Param("entity") FlowUser entity,
                             @Param("logicDeleteValue") String logicDeleteValue,
                             @Param("logicNotDeleteValue") String logicNotDeleteValue);

    List<FlowUser> listByAssociatedAndTypes(@Param("types") String[] types
            , @Param("associateds") List<Long> processedBys
            , @Param("entity") FlowUser entity);

    List<FlowUser> listByProcessedBys(@Param("types") String[] types
            , @Param("associateds") List<String> processedBys
            , @Param("entity") FlowUser entity);

}
