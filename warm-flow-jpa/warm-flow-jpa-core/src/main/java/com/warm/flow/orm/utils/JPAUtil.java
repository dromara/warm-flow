package com.warm.flow.orm.utils;

import com.warm.flow.core.entity.RootEntity;
import com.warm.flow.core.orm.agent.WarmQuery;
import com.warm.flow.orm.entity.*;

import javax.persistence.Column;
import javax.persistence.Transient;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * @author vanlin
 * @className JPAUtil
 * @description
 * @since 2024/5/11 21:34
 */
public class JPAUtil {

    /**
     * column 到 field 映射关系,兼容 order by 处理逻辑
     */
    public static <T extends RootEntity> void initMapping(Class<T> entityClass
            , HashMap<String, String> mapping) {
        Field[] fields = entityClass.getDeclaredFields();
        for (Field field : fields) {
            Transient transientAnnotation = field.getAnnotation(Transient.class);
            if (Objects.nonNull(transientAnnotation)) {
                continue;
            }
            Column column = field.getAnnotation(Column.class);
            if(Objects.nonNull(column)) {
                mapping.put(column.name(), field.getName());
            } else {
                mapping.put(field.getName(), field.getName());
            }
        }
    }




    public static void main(String[] args) {
        System.out.println(FlowDefinition.MAPPING);
        System.out.println(FlowHisTask.MAPPING);
        System.out.println(FlowInstance.MAPPING);
        System.out.println(FlowNode.MAPPING);
        System.out.println(FlowSkip.MAPPING);
        System.out.println(FlowTask.MAPPING);
    }
}
