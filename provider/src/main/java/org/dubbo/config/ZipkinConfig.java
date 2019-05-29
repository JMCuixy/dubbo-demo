package org.dubbo.config;

import brave.Tracing;
import brave.propagation.B3Propagation;
import brave.propagation.ExtraFieldPropagation;
import brave.sampler.Sampler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import zipkin2.reporter.AsyncReporter;
import zipkin2.reporter.Sender;
import zipkin2.reporter.okhttp3.OkHttpSender;

import java.util.concurrent.TimeUnit;

/**
 * @author : cuixiuyin
 * @date : 2019/5/28
 */
@Configuration
public class ZipkinConfig {

    @Autowired
    private ZipkinProperties properties;

    @Bean
    public Tracing tracing() {
        Sender sender = OkHttpSender.create(properties.getUrl());
        AsyncReporter reporter = AsyncReporter.builder(sender)
                .closeTimeout(properties.getConnectTimeout(), TimeUnit.MILLISECONDS)
                .messageTimeout(properties.getReadTimeout(), TimeUnit.MILLISECONDS)
                .build();
        Tracing tracing = Tracing.newBuilder()
                .localServiceName(properties.getServiceName())
                .propagationFactory(ExtraFieldPropagation.newFactory(B3Propagation.FACTORY, "shiliew"))
                .sampler(Sampler.ALWAYS_SAMPLE)
                .spanReporter(reporter)
                .build();
        return tracing;
    }


}
