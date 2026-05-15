package com.ruoyi.system.service.impl;

import org.dromara.warm.flow.core.dto.Tree;
import org.dromara.warm.flow.ui.service.FormPathService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义表单路径服务
 *
 * @author warm
 * @since 2025/10/22
 */
@Service
public class FormPathServiceImpl implements FormPathService {

    @Override
    public List<Tree> queryFormPath() {
        List<Tree> trees = new ArrayList<>();
        trees.add(new Tree("1", "表单1", null, null));
        trees.add(new Tree("1-1", "表单1-1", "1", null));
        trees.add(new Tree("2", "表单2", null, null));
        trees.add(new Tree("2-1", "表单2-1", "2", null));
        trees.add(new Tree("3", "表单3", null, null));

        return trees;
    }
}
