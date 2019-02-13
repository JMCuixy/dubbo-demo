package org.dubbo;


import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ImportResource;

/**
 * @Description:
 * @author: cuixiuyin
 * @date: 2018/12/19 21:32
 */
@SpringBootApplication
@ImportResource(value = {"classpath:dubbo-consumer.xml"})
public class Consumer {

    public static void main(String[] args) {
        new SpringApplicationBuilder(Consumer.class)
                .web(WebApplicationType.SERVLET)
                .run(args);
    }
}
