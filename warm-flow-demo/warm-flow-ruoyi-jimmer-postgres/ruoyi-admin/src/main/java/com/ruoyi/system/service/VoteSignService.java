package com.ruoyi.system.service;

import org.dromara.warm.flow.core.entity.HisTask;
import org.dromara.warm.flow.core.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 票签spel表达式计算
 *
 * @author warm
 * @since 2025/11/7
 */
@Component("voteSignService")
public class VoteSignService {

    private static final Logger log = LoggerFactory.getLogger(VoteSignService.class);

    /**
     * 票签通过率计算
     *
     * @param skipType 跳转类型
     * @param passNum  审批通过人数
     * @param rejectNum 审批驳回人数
     * @param todoNum  待处理人数
     * @param allNum  总人数
     * @param passList List<HisTask> 通过历史任务列表，HisTask中approver字段是审批人的唯一标识
     * @param rejectList List<HisTask> 拒绝历史任务列表，HisTask中approver字段是审批人的唯一标识
     * @param todoList 待处理用户列表
     * @return  boolean
     */
    public boolean eval(String skipType, Integer passNum, Integer rejectNum, Integer todoNum, Integer allNum
            , List<HisTask> passList, List<HisTask> rejectList, List<User> todoList ) {

        log.info("跳过类型: {}", skipType);
        log.info("通过数量: {}", passNum);
        log.info("拒绝数量: {}", rejectNum);
        log.info("待处理数量: {}", todoNum);
        log.info("总人数: {}", allNum);
        log.info("通过历史任务列表: {}", passList);
        log.info("拒绝历史任务列表: {}", rejectList);
        log.info("待处理用户列表: {}", todoList);
        log.info("开始票签通过率计算......");

        return true;
    }

}
