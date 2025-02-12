package com.ajie.enterprisewechat.bean;

import io.ebean.annotation.DbComment;
import lombok.Data;
import lombok.ToString;

import javax.persistence.Entity;

/**
 * @author jiangxingjie
 * @date 2025/2/10 17:04
 */
@Entity
@DbComment("获取token")
@Data
@ToString
public class AccessToken extends  BaseDomain{
    @DbComment("token 值")
    public String accessToken;
}
