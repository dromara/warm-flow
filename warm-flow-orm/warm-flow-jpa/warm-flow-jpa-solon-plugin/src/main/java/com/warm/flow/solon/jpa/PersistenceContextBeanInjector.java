package com.warm.flow.solon.jpa;


import org.hibernate.cfg.AvailableSettings;
import org.noear.snack.core.utils.StringUtil;
import org.noear.solon.Solon;
import org.noear.solon.core.BeanInjector;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.Props;
import org.noear.solon.core.VarHolder;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author vanlin
 * @className PersistenceContextBeanInjector
 * @description
 * @since 2024/6/28 10:07
 */
public class PersistenceContextBeanInjector implements BeanInjector<PersistenceContext> {
    private ConcurrentHashMap<String, EntityManagerFactory> factories = new ConcurrentHashMap<>();


    @Override
    public void doInject(VarHolder varH, PersistenceContext anno) {
        varH.context().getWrapAsync(DataSource.class, (dsBw) -> {
            if (dsBw.raw() instanceof DataSource) {
                inject0(anno, varH, dsBw);
            }
        });

    }

    private void inject0(PersistenceContext anno, VarHolder varH, BeanWrap dsBw) {
        String unitName = anno.unitName();
        if (StringUtil.isEmpty(unitName)) {
            unitName = "default";
        }

        final Props dsProps =  Solon.cfg().getProp(unitName);

        final String datasource = dsProps.get("datasource");
        if (!Objects.equals(dsBw.name(), datasource)) {
            // 数据源不匹配不进入注入操作
            return;
        }

        final Props properties = dsProps.getProp("properties");

        properties.put(AvailableSettings.DATASOURCE, dsBw.raw());

        final String finalUnitName = unitName;
        final EntityManagerFactory entityManagerFactory = factories.computeIfAbsent(unitName,
                x -> Persistence.createEntityManagerFactory(finalUnitName, properties));

        varH.setValue(new EntityManagerProxy(entityManagerFactory.createEntityManager()));
    }

}
