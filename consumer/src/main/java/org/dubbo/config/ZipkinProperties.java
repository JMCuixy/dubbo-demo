package org.dubbo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author : cuixiuyin
 * @date : 2019/5/28
 */
@Configuration
@ConfigurationProperties("zipkin")
public class ZipkinProperties {


    private String url;
    private Long connectTimeout;
    private Long readTimeout;
    private String serviceName;


    public void setUrl(String url) {
        this.url = url;
    }

    public void setConnectTimeout(Long connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public void setReadTimeout(Long readTimeout) {
        this.readTimeout = readTimeout;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getUrl() {
        return url;
    }

    public Long getConnectTimeout() {
        return connectTimeout;
    }

    public Long getReadTimeout() {
        return readTimeout;
    }

    public String getServiceName() {
        return serviceName;
    }
}
