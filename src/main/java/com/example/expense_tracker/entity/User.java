package com.example.expense_tracker.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @author xiaoyang
 * @create 2025-01-17-09:26
 * @description
 */
@Data
@TableName("user")
public class User {
    private Long id;
    private String username;
    private String password;
    private String email;
    private Date createdAt;
    private Date updatedAt;
}
