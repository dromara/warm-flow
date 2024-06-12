package com.warm.flow.core.constant;

/**
 * warm-flow配置文件常量
 *
 * @author warm
 */
public class FlowConfigCons {

    /**
     * 是否支持任意跳转
     */
    public static final String BANNER = "warm-flow.banner";

    /**
     * 是否开启逻辑删除
     */
    public static final String LOGICDELETE = "warm-flow.logic_delete";

    /**
     * 逻辑删除字段值
     */
    public static final String LOGICDELETEVALUE = "warm-flow.logic_delete_value";

    /**
     * 逻辑未删除字段
     */
    public static final String LOGICNOTDELETEVALUE = "warm-flow.logic_not_delete_value";

    /**
     * 数据填充处理类路径
     */
    public static final String DATAFILLHANDLEPATH = "warm-flow.data-fill-handler-path";

    /**
     * 租户处理类路径
     */
    public static final String TENANTHANDLERPATH = "warm-flow.tenant_handler_path";
    /**
     * 数据源类型, mybatis模块对orm进一步的封装, 由于各数据库分页语句存在差异,
     * 当配置此参数时, 以此参数结果为基准, 未配置时, 取DataSource中数据源类型,
     * 兜底为mysql数据库
     */
    public static final String DATA_SOURCE_TYPE = "warm-flow.data_source_type";
}
