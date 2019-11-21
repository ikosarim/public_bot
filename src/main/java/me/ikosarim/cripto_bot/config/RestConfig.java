package me.ikosarim.cripto_bot.config;

import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import static java.lang.Integer.*;

@Configuration
public class RestConfig {

    private Environment env;

    @Autowired
    public void setEnv(Environment env) {
        this.env = env;
    }

    @Bean
    public PoolingHttpClientConnectionManager poolingHttpClientConnectionManager() {
        PoolingHttpClientConnectionManager result = new PoolingHttpClientConnectionManager();
        result.setMaxTotal(20);
        return result;
    }

    @Bean
    public RequestConfig requestConfig() {
        return RequestConfig.custom()
                .setConnectionRequestTimeout(5000)
                .setConnectTimeout(5000)
                .setSocketTimeout(5000)
                .build();
    }

    @Bean
    public CloseableHttpClient publicHttpClient(PoolingHttpClientConnectionManager poolingHttpClientConnectionManager,
                                                RequestConfig requestConfig) {
        return createHttpClient(poolingHttpClientConnectionManager, requestConfig, "http.proxy.public.host",
                "http.proxy.public.port");
    }

    @Bean
    public CloseableHttpClient privateHttpClient(PoolingHttpClientConnectionManager poolingHttpClientConnectionManager,
                                                 RequestConfig requestConfig) {
        return createHttpClient(poolingHttpClientConnectionManager, requestConfig, "http.proxy.private.host",
                "http.proxy.private.port");
    }

    private CloseableHttpClient createHttpClient(PoolingHttpClientConnectionManager poolingHttpClientConnectionManager,
                                                 RequestConfig requestConfig, String propertyHost, String propertyPort) {
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create()
                .setConnectionManager(poolingHttpClientConnectionManager)
                .setDefaultRequestConfig(requestConfig);
        String host = env.getProperty(propertyHost);
        String port = env.getProperty(propertyPort);
        if (!"".equals(host) && !"".equals(port)) {
            httpClientBuilder.setProxy(new HttpHost(host, parseInt(port)));
        }
        return httpClientBuilder.build();
    }

    @Bean(name = "publicRestTemplate")
    public RestTemplate publicRestTemplate(HttpClient publicHttpClient) {
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setHttpClient(publicHttpClient);
        return new RestTemplate(requestFactory);
    }

    @Bean(name = "privateRestTemplate")
    public RestTemplate privateRestTemplate(HttpClient privateHttpClient) {
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setHttpClient(privateHttpClient);
        return new RestTemplate(requestFactory);
    }
}
