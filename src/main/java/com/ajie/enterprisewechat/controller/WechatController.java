package com.ajie.enterprisewechat.controller;

import com.ajie.enterprisewechat.bean.AccessToken;
import com.ajie.enterprisewechat.bean.query.QAccessToken;
import com.ajie.enterprisewechat.config.SYSTEM_CONST;
import com.ajie.enterprisewechat.config.WechatConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * @author jiangxingjie
 * @date 2025/2/11 13:34
 */
@RequestMapping("/")
@Controller
@RequiredArgsConstructor
@Slf4j
public class WechatController {
    private final RestTemplate restTemplate;
    private final WechatConfig wechatConfig;

    @GetMapping("/getOpenIdByCode")
    public String getOpenIdByCode(String code, String state) {
        StringBuffer redirect = new StringBuffer();
        log.info("企业微信调用getOpenIdByCode传入参数,{},{}", code, state);
        Map<String, String> params = new HashMap<>(16);
        AccessToken accessToken = new QAccessToken().orderBy().id.desc().findOne();

        if (accessToken == null) {
            log.error("accessToken 为null");
        }
        params.put("accessToken", accessToken.getAccessToken());
        params.put("code", code);
        ResponseEntity<HashMap> forEntity = restTemplate.getForEntity(SYSTEM_CONST.GET_USER_INFO, HashMap.class, params);
        // 判断用户是否登陆,用户登陆了传入openid 拿到token 用户未登陆
        String token = "";
        if (forEntity.getStatusCode().is2xxSuccessful()) {
            redirect.append("redirect:");
            redirect.append(wechatConfig.getDomain());
            redirect.append("?token=");
            redirect.append(token);

        } else {
            log.error("获取用户信息网络失败");
        }
        return redirect.toString();

    }

}
