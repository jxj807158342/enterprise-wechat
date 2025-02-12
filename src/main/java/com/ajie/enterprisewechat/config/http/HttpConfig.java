package com.ajie.enterprisewechat.config.http;

import org.apache.http.HeaderElement;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

@Configuration
public class HttpConfig {
    private Logger logger = LoggerFactory.getLogger(HttpConfig.class);
    // 默认执行2次
    public static  final int DEFAULT_EXECUTION_COUNT = 2;
    @Bean
    public RestTemplate restTemplate() {
        // # jdk 调用出现 Remote host terminated the handshake
       //  jdk.tls.useExtendedMasterSecret=false
        // https://blog.csdn.net/sipsipsip/article/details/125778716
        // https://blog.csdn.net/EulerPlanet/article/details/118528039
        // 查看 JDK8u 161 的 release notes，添加了 TLS 会话散列和扩展主密钥扩展支持，
        // 找到引入的一个还未修复的 issue，对于带有身份验证的 TLS 的客户端，支持 UseExtendedMasterSecret
        // 会破坏 TLS-Session 的恢复，导致不使用现有的 TLS-Session，而执行新的 Handshake。
        //
        //JDK8u161 之后的版本(含 JDK8u161)，若复用会话时不能成功恢复 Session，而是创建新的会话，
        // 会造成较大性能消耗，且积压的大量的不可复用的 session 造成 GC 压力变大；
        // 如果业务场景存在不变更证书密钥，需要复用会话，且对性能有要求，
        // 可通过添加参数-Djdk.tls.useExtendedMasterSecret=false 来解决这个问题。
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        CloseableHttpClient build = HttpClientBuilder.create()
                .setRedirectStrategy(new LaxRedirectStrategy())
                // https://juejin.cn/post/6865277186438496269
                // 解决 NoHttpResponseException
                //  这个讲得好    https://firfor.cn/articles/2019/05/19/1558271846111.html
                // https://www.likecs.com/ask-4281625.html
//                .setConnectionReuseStrategy(new NoConnectionReuseStrategy())
                // 以后扩展这个 https://springframework.guru/using-resttemplate-with-apaches-httpclient/
                .setKeepAliveStrategy(new ConnectionKeepAliveStrategy(){

                    @Override
                    public long getKeepAliveDuration(HttpResponse httpResponse, HttpContext httpContext) {
                        BasicHeaderElementIterator basicHeaderElementIterator = new BasicHeaderElementIterator(httpResponse.headerIterator(HTTP.CONN_KEEP_ALIVE));
                        while (basicHeaderElementIterator.hasNext()) {
                            HeaderElement he = basicHeaderElementIterator.nextElement();
                            String param = he.getName();
                            String value = he.getValue();
                            if (value != null && param.equalsIgnoreCase("timeout")) {
                                try {
                                    return Long.parseLong(value) * 1000;
                                } catch(NumberFormatException ignore) {
                                }
                            }
                        }
                        HttpHost target = (HttpHost) httpContext.getAttribute(
                                HttpClientContext.HTTP_TARGET_HOST);
                        if ("api.weixin.qq.com".equalsIgnoreCase(target.getHostName())||"apis.map.qq.com".equalsIgnoreCase(target.getHostName())) {
                            // Keep alive for 5 seconds only
                            return 10 * 1000;
                        } else {
                            // otherwise keep alive for 30 seconds
                            return 30 * 1000;
                        }
                    }
                })
                // 官方文档 https://hc.apache.org/httpcomponents-client-4.5.x/current/tutorial/html/connmgmt.html
                .setRetryHandler(new HttpRequestRetryHandler() {
                    @Override
                    public boolean retryRequest(IOException e, int executionCount, HttpContext httpContext) {
                        if(executionCount> DEFAULT_EXECUTION_COUNT){
                            logger.warn("retryRequest,{}",executionCount);
                            return false;
                        }
                        if (e instanceof org.apache.http.NoHttpResponseException) {
                            logger.warn("Executed retry executionCount ,{},{}",executionCount,e.getMessage() );
                            return true;
                        }
                        return false;
                    }
                })
                .build();
        factory.setHttpClient(build);
        //指从连接池获取到连接的超时时间，如果是非连接池的话，该参数暂时没有发现有什么用处
        // factory.setConnectionRequestTimeout();
        //指的是建立连接后从服务器读取到可用资源所用的时间
        factory.setReadTimeout(60000);
        //指的是建立连接所用的时间，适用于网络状况正常的情况下，两端连接所用的时间
        factory.setConnectTimeout(30000);
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(factory);
        restTemplate.setInterceptors(Collections.singletonList(new headerInteceptor()));
        restTemplate.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));

        return restTemplate;
    }

    class headerInteceptor implements ClientHttpRequestInterceptor {

        @Override
        public ClientHttpResponse intercept(HttpRequest httpRequest, byte[] bytes, ClientHttpRequestExecution clientHttpRequestExecution) throws IOException {
            HttpHeaders headers = httpRequest.getHeaders();
            URI uri = httpRequest.getURI();
            headers.add("Content-Type", "application/json");
            return clientHttpRequestExecution.execute(httpRequest, bytes);
        }
    }

}
