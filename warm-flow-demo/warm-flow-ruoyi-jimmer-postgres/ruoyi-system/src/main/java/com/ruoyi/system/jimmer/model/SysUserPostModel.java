package com.ruoyi.system.jimmer.model;

import org.babyfish.jimmer.sql.Column;
import org.babyfish.jimmer.sql.Entity;
import org.babyfish.jimmer.sql.GeneratedValue;
import org.babyfish.jimmer.sql.GenerationType;
import org.babyfish.jimmer.sql.Id;
import org.babyfish.jimmer.sql.Key;
import org.babyfish.jimmer.sql.Table;

@Entity
@Table(name = "sys_user_post")
public interface SysUserPostModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_post_id")
    long userPostId();

    @Key
    @Column(name = "user_id")
    long userId();

    @Key
    @Column(name = "post_id")
    long postId();

}
