package org.dromara.warm.flow.core.model;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户类，承载用户信息，可根据需要扩展属性
 *
 * @author chengmaoning
 * @since 2025/8/7 10:23
 */
@Data
public class WfUser implements Serializable {

    private String userId;

    private String userName;

    private String nickName;

    private Long deptId;

    private String deptName;

    private Long tenantId;

    private String tenantName;

    private String leader;

    /**
     * 性别
     */
    private String sex;

    private String email;

    private String phonenumber;

    /**
     * 状态
     */
    private String status;

}
