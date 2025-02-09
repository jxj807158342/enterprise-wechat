package com.ajie.enterprisewechat.bean;

import io.ebean.annotation.DbComment;

import javax.persistence.Entity;

/**
 * @author jiangxingjie
 * @date 2025/2/8 16:48
 */
@Entity
@DbComment("测试")
public class Demo extends  BaseDomain{
    @DbComment("姓名")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
