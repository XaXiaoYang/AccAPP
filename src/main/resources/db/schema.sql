-- 1. 创建数据库：
CREATE DATABASE expense_tracker DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE expense_tracker;
-- 用户表
CREATE TABLE `user` (
                        `id` BIGINT NOT NULL AUTO_INCREMENT,
                        `username` VARCHAR(50) NOT NULL COMMENT '用户名',
                        `password` VARCHAR(100) NOT NULL COMMENT '密码',
                        `email` VARCHAR(100) COMMENT '邮箱',
                        `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                        `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                        PRIMARY KEY (`id`),
                        UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 交易记录表
CREATE TABLE `transaction` (
                               `id` BIGINT NOT NULL AUTO_INCREMENT,
                               `user_id` BIGINT NOT NULL COMMENT '用户ID',
                               `type` VARCHAR(20) NOT NULL COMMENT '类型：income-收入/expense-支出',
                               `amount` DECIMAL(10,2) NOT NULL COMMENT '金额',
                               `category_id` BIGINT COMMENT '分类ID',
                               `description` VARCHAR(255) COMMENT '描述',
                               `transaction_date` DATETIME NOT NULL COMMENT '交易时间',
                               `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                               PRIMARY KEY (`id`),
                               KEY `idx_user_id` (`user_id`),
                               KEY `idx_category_id` (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='交易记录表';

-- 分类表
CREATE TABLE IF NOT EXISTS `category` (
                                          `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '分类ID',
                                          `name` varchar(50) NOT NULL COMMENT '分类名称',
                                          `type` varchar(20) NOT NULL COMMENT '类型：income-收入/expense-支出',
                                          `user_id` bigint(20) DEFAULT NULL COMMENT '用户ID，为空表示系统预设分类',
                                          `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                          `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                          PRIMARY KEY (`id`),
                                          KEY `idx_user_id` (`user_id`),
                                          CONSTRAINT `fk_category_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='分类表';

-- 预算表
CREATE TABLE `budget` (
                          `id` BIGINT NOT NULL AUTO_INCREMENT,
                          `user_id` BIGINT NOT NULL COMMENT '用户ID',
                          `category_id` BIGINT COMMENT '分类ID，NULL表示总预算',
                          `amount` DECIMAL(10,2) NOT NULL COMMENT '预算金额',
                          `period` VARCHAR(20) NOT NULL COMMENT '周期：monthly-月度/yearly-年度',
                          `start_date` DATE NOT NULL COMMENT '开始日期',
                          `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                          PRIMARY KEY (`id`),
                          KEY `idx_user_id` (`user_id`),
                          KEY `idx_category_id` (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预算表';

-- 添加默认分类
INSERT INTO `category` (`name`, `type`) VALUES
                                            ('工资', 'income'),
                                            ('兼职', 'income'),
                                            ('投资', 'income'),
                                            ('其他收入', 'income'),
                                            ('餐饮', 'expense'),
                                            ('交通', 'expense'),
                                            ('购物', 'expense'),
                                            ('居住', 'expense'),
                                            ('娱乐', 'expense'),
                                            ('医疗', 'expense'),
                                            ('教育', 'expense'),
                                            ('其他支出', 'expense');
-- 在交易表中添加分类ID字段
ALTER TABLE `transaction`
    ADD COLUMN `category_id` bigint(20) NOT NULL COMMENT '分类ID' AFTER `type`,
    ADD CONSTRAINT `fk_transaction_category` FOREIGN KEY (`category_id`) REFERENCES `category` (`id`);
-- 插入测试用户
INSERT INTO `user` (`username`, `password`, `email`) VALUES
    ('test', '$2a$10$X/hX4qvWzxwRqjwfEYqo8eY5a8puvnGka5PHKqwzUeYwX8FZwVqWi', 'test@example.com');  -- 密码：123456

ALTER TABLE `transaction`
    ADD COLUMN `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间';

select * from user