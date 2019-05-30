package org.dubbo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author : cuixiuyin
 * @date : 2019/5/28
 */
@Configuration
@ConfigurationProperties("zipkin")
public class ZipkinProperties {


    @Value("${spring.application.name}")
    private String serviceName;
    private String url;
    private Long connectTimeout;
    private Long readTimeout;
    private Float rate;


    /*getter and setter*/

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

    public void setRate(Float rate) {
        this.rate = rate;
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

    public Float getRate() {
        return rate;
    }
}
