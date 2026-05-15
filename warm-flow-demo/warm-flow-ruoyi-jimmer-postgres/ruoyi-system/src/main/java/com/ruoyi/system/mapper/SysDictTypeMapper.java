package com.ruoyi.system.mapper;

import com.ruoyi.common.core.domain.entity.SysDictType;
import com.ruoyi.system.jimmer.JimmerRepositorySupport;
import com.ruoyi.system.jimmer.model.SysDictTypeModel;
import org.babyfish.jimmer.sql.ast.Predicate;
import org.springframework.stereotype.Repository;

import java.util.List;

/** 字典类型 Jimmer 数据层 */
@Repository
public class SysDictTypeMapper extends JimmerRepositorySupport<SysDictType, SysDictTypeModel>
{
    public SysDictTypeMapper()
    {
        super(SysDictType.class, SysDictTypeModel.class, "dictId");
    }

    public List<SysDictType> selectDictTypeList(SysDictType dictType)
    {
        return list(dictTypePredicates(dictType), orders("dictId"));
    }

    public List<SysDictType> selectDictTypeAll()
    {
        return listAll();
    }

    public SysDictType selectDictTypeById(Long dictId)
    {
        return selectById(dictId);
    }

    public SysDictType selectDictTypeByType(String dictType)
    {
        return findOne(predicates(eq("dictType", dictType)), null);
    }

    public int deleteDictTypeById(Long dictId)
    {
        return deleteById(dictId);
    }

    public int deleteDictTypeByIds(Long[] dictIds)
    {
        return deleteByIds(dictIds);
    }

    public int insertDictType(SysDictType dictType)
    {
        return insert(dictType);
    }

    public int updateDictType(SysDictType dictType)
    {
        return updateById(dictType);
    }

    public SysDictType checkDictTypeUnique(String dictType)
    {
        return selectDictTypeByType(dictType);
    }

    private List<Predicate> dictTypePredicates(SysDictType dictType)
    {
        List<Predicate> predicates = predicates();
        if (dictType != null)
        {
            predicates.add(eq("dictId", dictType.getDictId()));
            predicates.add(like("dictName", dictType.getDictName()));
            predicates.add(like("dictType", dictType.getDictType()));
            predicates.add(eq("status", dictType.getStatus()));
            addDateRange(predicates, dictType, "createTime");
        }
        return predicates;
    }
}
