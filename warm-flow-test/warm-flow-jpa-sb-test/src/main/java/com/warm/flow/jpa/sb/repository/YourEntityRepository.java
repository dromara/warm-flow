package com.warm.flow.jpa.sb.repository;

import com.warm.flow.jpa.sb.entity.FlowDefinition;
import org.springframework.data.jpa.repository.JpaRepository;

public interface YourEntityRepository extends JpaRepository<FlowDefinition, Long> {
    // 自定义查询方法
}