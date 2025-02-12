package com.ajie.enterprisewechat.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author jiangxingjie
 * @date 2025/2/11 11:36
 */
@Component
@ConfigurationProperties(prefix = "wechat")
@Data
public class WechatConfig {
    /**
     * 企业微信ID
     */
    private String corpid;
    /**
     *  应用的凭证密钥
     */
    private String corpsecret;
    /**
     * 域名
     */
    private String domain;

}
