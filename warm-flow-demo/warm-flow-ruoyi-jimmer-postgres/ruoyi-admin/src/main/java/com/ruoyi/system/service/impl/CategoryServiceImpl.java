package com.ruoyi.system.service.impl;

import org.dromara.warm.flow.core.dto.Tree;
import org.dromara.warm.flow.ui.service.CategoryService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 分类服务
 *
 * @author warm
 * @since 2025/6/24
 */
@Service
public class CategoryServiceImpl implements CategoryService {

    @Override
    public List<Tree> queryCategory() {
        List<Tree> trees = new ArrayList<>();
        trees.add(new Tree("1", "分类1", null, null));
        trees.add(new Tree("1-1", "分类1-1", "1", null));
        trees.add(new Tree("2", "分类2", null, null));
        trees.add(new Tree("2-1", "分类2-1", "2", null));
        trees.add(new Tree("3", "分类3", null, null));

        return trees;
    }
}
