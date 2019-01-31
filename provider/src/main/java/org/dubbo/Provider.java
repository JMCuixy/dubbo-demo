package org.dubbo;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * @Description:
 * @author: cuixiuyin
 * @date: 2018/12/19 21:30
 */
@SpringBootApplication
public class Provider {

    public static void main(String[] args) {
        new SpringApplicationBuilder(Provider.class)
                .web(WebApplicationType.SERVLET)
                .profiles("spring")
                .run(args);
    }
}
