package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.SysConfig;
import com.ruoyi.system.jimmer.JimmerRepositorySupport;
import com.ruoyi.system.jimmer.model.SysConfigModel;
import org.babyfish.jimmer.sql.ast.Predicate;
import org.springframework.stereotype.Repository;

import java.util.List;

/** 参数配置 Jimmer 数据层 */
@Repository
public class SysConfigMapper extends JimmerRepositorySupport<SysConfig, SysConfigModel>
{
    public SysConfigMapper()
    {
        super(SysConfig.class, SysConfigModel.class, "configId");
    }

    public SysConfig selectConfig(SysConfig config)
    {
        return findOne(configPredicates(config), null);
    }

    public SysConfig selectConfigById(Long configId)
    {
        return selectById(configId);
    }

    public List<SysConfig> selectConfigList(SysConfig config)
    {
        return list(configPredicates(config), orders("configId"));
    }

    public SysConfig checkConfigKeyUnique(String configKey)
    {
        return findOne(predicates(eq("configKey", configKey)), null);
    }

    public int insertConfig(SysConfig config)
    {
        return insert(config);
    }

    public int updateConfig(SysConfig config)
    {
        return updateById(config);
    }

    public int deleteConfigById(Long configId)
    {
        return deleteById(configId);
    }

    public int deleteConfigByIds(Long[] configIds)
    {
        return deleteByIds(configIds);
    }

    private List<Predicate> configPredicates(SysConfig config)
    {
        List<Predicate> predicates = predicates();
        if (config != null)
        {
            predicates.add(eq("configId", config.getConfigId()));
            predicates.add(like("configName", config.getConfigName()));
            predicates.add(eq("configType", config.getConfigType()));
            predicates.add(like("configKey", config.getConfigKey()));
            addDateRange(predicates, config, "createTime");
        }
        return predicates;
    }
}
