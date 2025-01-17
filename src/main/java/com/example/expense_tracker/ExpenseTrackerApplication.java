package com.example.expense_tracker;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author xiaoyang
 * @create 2025-01-16-19:33
 * @description
 */
@SpringBootApplication
@MapperScan("com.example.expense_tracker.mapper")
// 启用事务管理
@EnableTransactionManagement
public class ExpenseTrackerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExpenseTrackerApplication.class, args);
        System.out.println("启动完成");
    }
}
