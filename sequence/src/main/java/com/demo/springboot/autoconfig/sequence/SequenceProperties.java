package com.demo.springboot.autoconfig.sequence;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Author: xiuyin.cui@joymo.tech
 * @Date: 2021/8/13 16:33
 * @Description:
 */
@Data
@ConfigurationProperties(prefix = SequenceProperties.PREFIX)
public class SequenceProperties {

    public final static String PREFIX = "demo.sequence";

    private String type;

    private int step = 500;

    private DataSource dataSource;

    @Data
    public static class DataSource {
        private String url;
        private String username;
        private String password;
        private int initialSize = 5;
        private int maxActive = 10;
        private int minIdle = 2;
        private int maxWait = 60000;
        private boolean poolPreparedStatements = false;
        private int maxOpenPreparedStatements = -1;
        private String validationQuery = "SELECT 'x'";
        private boolean testOnBorrow = false;
        private boolean testOnReturn = false;
        private boolean testWhileIdle = true;
        private int timeBetweenEvictionRunsMillis = 60000;
        private int minEvictableIdleTimeMillis = 300000;

        private boolean lazyInit = true;
    }

}
