package org.dubbo.config;

import com.alibaba.csp.sentinel.adapter.servlet.CommonFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author xiuyin.cui@luckincoffee.com
 * @Date 2020-05-09 13:33
 * @Description 1.0
 */
@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean sentinelFilterRegistration() {
        FilterRegistrationBean<Filter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new CommonFilter());
        registration.addUrlPatterns("/*");
        registration.setName("sentinelFilter");
        registration.setOrder(1);
        // 区分不同 HTTP Method
        Map<String, String> initParameters = new HashMap<>(4);
        initParameters.put("HTTP_METHOD_SPECIFY ", "true");
        registration.setInitParameters(initParameters);

        return registration;
    }
}
