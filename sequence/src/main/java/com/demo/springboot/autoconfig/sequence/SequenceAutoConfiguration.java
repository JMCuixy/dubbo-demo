package com.demo.springboot.autoconfig.sequence;

import com.alibaba.druid.pool.DruidDataSource;
import com.demo.sequence.GlobalSequence;
import com.demo.sequence.dao.impl.DefaultGlobalSequenceDao;
import com.demo.sequence.datasource.DataSourceManagerFactory;
import com.demo.sequence.impl.DefaultGlobalSequence;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

/**
 * @Author: xiuyin.cui@joymo.tech
 * @Date: 2021/8/13 16:33
 * @Description:
 */
@Slf4j
@Configuration
@EnableConfigurationProperties
public class SequenceAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public SequenceProperties sequenceProperties() {
        return new SequenceProperties();
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = SequenceProperties.PREFIX, name = "type", havingValue = "global", matchIfMissing = true)
    public GlobalSequence globalSequence(SequenceProperties properties) throws Exception {
        DataSourceManagerFactory factory = buildDataSourceManagerFactory(properties);
        DefaultGlobalSequence sequence = new DefaultGlobalSequence();
        sequence.setCapacity(properties.getStep());
        sequence.setDataSourceManagerFactory(factory);
        sequence.setSequenceDao(new DefaultGlobalSequenceDao());
        sequence.init();
        return sequence;
    }


    private DataSourceManagerFactory buildDataSourceManagerFactory(SequenceProperties properties) throws Exception {
        DruidDataSource ds = new DruidDataSource();
        ds.setUrl(properties.getDataSource().getUrl());
        ds.setUsername(properties.getDataSource().getUsername());
        ds.setPassword(properties.getDataSource().getPassword());
        ds.setInitialSize(properties.getDataSource().getInitialSize());
        ds.setMaxActive(properties.getDataSource().getMaxActive());
        ds.setMinIdle(properties.getDataSource().getMinIdle());
        ds.setMaxWait(properties.getDataSource().getMaxWait());
        ds.setPoolPreparedStatements(properties.getDataSource().isPoolPreparedStatements());
        ds.setMaxOpenPreparedStatements(properties.getDataSource().getMaxOpenPreparedStatements());
        ds.setValidationQuery(properties.getDataSource().getValidationQuery());
        ds.setTestOnBorrow(properties.getDataSource().isTestOnBorrow());
        ds.setTestOnReturn(properties.getDataSource().isTestOnReturn());
        ds.setTestWhileIdle(properties.getDataSource().isTestWhileIdle());
        ds.setTimeBetweenEvictionRunsMillis(properties.getDataSource().getTimeBetweenEvictionRunsMillis());
        ds.setMinEvictableIdleTimeMillis(properties.getDataSource().getMinEvictableIdleTimeMillis());

        if (!properties.getDataSource().isLazyInit()) {
            ds.init();
        }

        DataSourceManagerFactory factory = new DataSourceManagerFactory();
        factory.setDataSourceList(Collections.singletonList(ds));
        return factory;
    }
}
