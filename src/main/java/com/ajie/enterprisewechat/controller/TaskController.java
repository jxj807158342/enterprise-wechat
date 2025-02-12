package com.ajie.enterprisewechat.controller;

import com.ajie.enterprisewechat.bean.AccessToken;
import com.ajie.enterprisewechat.config.SYSTEM_CONST;
import com.ajie.enterprisewechat.config.WechatConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author jiangxingjie
 * @date 2025/2/10 16:59
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TaskController {
    private final RestTemplate restTemplate;
    private final WechatConfig wechatConfig;
    private ObjectMapper objectMapper = new ObjectMapper();

    @PostConstruct
    public void init() throws JsonProcessingException {
        getToken();
    }

    @Scheduled(fixedRate = 3600000)
    public void getToken() throws JsonProcessingException {
        Map<String, String> params = new HashMap<>(16);
        params.put("corpid", wechatConfig.getCorpid());
        params.put("corpsecret", wechatConfig.getCorpsecret());
        ResponseEntity<String> forEntity = restTemplate.getForEntity(SYSTEM_CONST.GET_TOKEN, String.class, params);
        if (forEntity.getStatusCode().is2xxSuccessful()) {
            String body = forEntity.getBody();
            if (body == null || Objects.equals(body, "")) {
                log.error("获取token失败,body 为null");
            } else {
                HashMap hashMap = objectMapper.readValue(body, HashMap.class);
                if (Objects.equals(hashMap.get("errcode"), 0)) {
                    String accessTokenValue = (String) hashMap.get("access_token");
                    AccessToken accessToken = new AccessToken();
                    accessToken.setAccessToken(accessTokenValue);
                    accessToken.save();
                } else {
                    log.error("获取token失败,{}", body);
                }
            }
        } else {
            log.info("获取token 网络失败");
        }

    }
}
