package com.ruoyi.system.mapper;

import com.ruoyi.common.core.domain.entity.SysDictData;
import com.ruoyi.system.jimmer.JimmerRepositorySupport;
import com.ruoyi.system.jimmer.model.SysDictDataModel;
import org.babyfish.jimmer.sql.ast.Predicate;
import org.springframework.stereotype.Repository;

import java.util.List;

/** 字典数据 Jimmer 数据层 */
@Repository
public class SysDictDataMapper extends JimmerRepositorySupport<SysDictData, SysDictDataModel>
{
    public SysDictDataMapper()
    {
        super(SysDictData.class, SysDictDataModel.class, "dictCode");
    }

    public List<SysDictData> selectDictDataList(SysDictData dictData)
    {
        return list(dictDataPredicates(dictData), orders("dictSort", "dictCode"));
    }

    public List<SysDictData> selectDictDataByType(String dictType)
    {
        return list(predicates(eq("dictType", dictType), eq("status", "0")), orders("dictSort", "dictCode"));
    }

    public String selectDictLabel(String dictType, String dictValue)
    {
        SysDictData data = findOne(predicates(eq("dictType", dictType), eq("dictValue", dictValue)), null);
        return data == null ? null : data.getDictLabel();
    }

    public SysDictData selectDictDataById(Long dictCode)
    {
        return selectById(dictCode);
    }

    public int countDictDataByType(String dictType)
    {
        return (int) count(predicates(eq("dictType", dictType)));
    }

    public int deleteDictDataById(Long dictCode)
    {
        return deleteById(dictCode);
    }

    public int deleteDictDataByIds(Long[] dictCodes)
    {
        return deleteByIds(dictCodes);
    }

    public int insertDictData(SysDictData dictData)
    {
        return insert(dictData);
    }

    public int updateDictData(SysDictData dictData)
    {
        return updateById(dictData);
    }

    public int updateDictDataType(String oldDictType, String newDictType)
    {
        return updateWhere(predicates(eq("dictType", oldDictType)), mapOf("dictType", newDictType));
    }

    private List<Predicate> dictDataPredicates(SysDictData dictData)
    {
        List<Predicate> predicates = predicates();
        if (dictData != null)
        {
            predicates.add(eq("dictCode", dictData.getDictCode()));
            predicates.add(eq("dictType", dictData.getDictType()));
            predicates.add(like("dictLabel", dictData.getDictLabel()));
            predicates.add(eq("status", dictData.getStatus()));
        }
        return predicates;
    }
}
