package com.example.expense_tracker.common.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.net.InetAddress;
import java.net.UnknownHostException;
/**
 * @author xiaoyang
 * @create 2025-01-21-15:01
 * @description
 */

//@Configuration
public class DynamicDataSourceConfig {

    @Value("${spring.datasource.driver-class-name}")
    private String driverClassName;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Bean
    public DataSource dataSource() throws UnknownHostException {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(driverClassName);
        dataSource.setUsername(username);
        dataSource.setPassword(password);

        // 获取当前服务器的IP地址
        String ipAddress = InetAddress.getLocalHost().getHostAddress();
        String url = String.format("jdbc:mysql://%s:3306/expense_tracker?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai", ipAddress);
        dataSource.setUrl(url);

        return dataSource;
    }
}
