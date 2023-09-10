package com.warm.flow.core.constant;

/**
 * @author minliuhua
 * @description: 工作流中用到的一些常量
 * @date: 2023/3/30 14:05
 */
public class FlowConstant {

    //******************************以下为异常常量****************************************


    public static final String SAME_CONDITION_VALUE = "同一个结点不能有相同的跳转跳转类型!";

    public static final String SAME_DEST_NODE = "同一结点不能跳转同一个目标结点!";

    public static final String SAME_FLOW_CODE = "相同流程的编码的不能重复,!";

    public static final String MUL_START_NODE = "开始结点超过1个!";

    public static final String ALREADY_EXIST = "流程已经存在,请通过创建新版本的流程对该流程进行更新!";

    public static final String LOST_START_NODE = "流程缺少开始结点!";

    public static final String LOST_NODE_CODE = "结点编码缺失";

    public static final String SAME_NODE_CODE = "同一流程中结点编码重复!";

    public static final String MUL_DEST_NODE = "当前结点有多个可跳转的结点,无法获知应该跳转到哪个结点!";

    public static final String MUL_FROM_STATUS = "存在流程状态不同的流程,无法批量处理!";

    public static final String NULL_DEST_NODE = "无法跳转到结点,请检查跳转类型和当前用户权限是否匹配!";

    public static final String NULL_CONDITIONVALUE_NODE = "无法跳转到结点,请检查跳转类型是否匹配!";

    public static final String NULL_CONDITIONVALUE = "跳转条件不能为空!";

    public static final String FRIST_FORBID_BACK = "第一个结点禁止驳回";

    public static final String NULL_ROLE_NODE = "无法跳转到该结点,请检查当前用户是否有权限!";

    public static final String MEANINGLESS_CONDITION = "开始结点不需要设置跳转类型和权限标识!";

    public static final String MUL_BUSINESS_ID = "当前业务id已经创建过流程!";

    public static final String LOST_DEST_NODE = "目标结点为空!";

    public static final String NULL_NODE_CODE = "目标结点不存在!";

    public static final String NULL_BUSINESS_ID = "业务id为空!";

    public static final String NULL_FLOW_CODE = "流程编码缺失!";

    public static final String NULL_FLOW_VERSION = "流程版本不能为空!";

    public static final String NOT_FOUNT_INSTANCE_ID = "流程实例id为空!";

    public static final String LOST_FOUNT_INSTANCE = "流程实例缺失!";

    public static final String NOT_FOUNT_INSTANCE = "流程实例获取失败!";

    public static final String NULL_INSTANCE_ID = "流程实例id不能为空!";

    public static final String LOST_FOUNT_TASK = "待办任务缺失!";

    public static final String NOT_FOUNT_TASK = "待办任务获取失败!";

    public static final String NOT_ROLE_PERMISSIONS = "当前账号无角色权限!";

    public static final String NOT_DEFINITION_ID = "流程定义id不能为空!";

    public static final String NOT_NODE_DATA = "流程结点数据缺失!";

    public static final String NOT_PUBLISH_NODE = "不存在已发布的流程定义!";

    public static final String NOT_FOUNT_HIS = "历史任务记录缺失!";

    public static final String MSG_OVER_LENGTH = "意见长度过长!";

}
