package com.ajie.enterprisewechat.config;

public class SYSTEM_CONST {


    /**
     *  get token
     */
    public static final String GET_TOKEN = "https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid={corpid}&corpsecret={corpsecret}";

    /**
     * 获取用户信息
     */
    public static  final String GET_USER_INFO  = "https://qyapi.weixin.qq.com/cgi-bin/auth/getuserinfo?access_token={accessToken}&code={code}";

}