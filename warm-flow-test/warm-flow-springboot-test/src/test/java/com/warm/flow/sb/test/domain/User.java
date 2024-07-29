package com.warm.flow.sb.test.domain;

import org.springframework.stereotype.Component;

/**
 * 用户类
 */
@Component("user")
public class User {

    public boolean eval(String aa) {
        return "yes".equals(aa);
    }
}