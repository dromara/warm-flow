package com.warm.flow.orm.utils;

import com.warm.flow.core.entity.RootEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Transient;

import java.lang.reflect.Field;
import java.util.HashMap;
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

}
