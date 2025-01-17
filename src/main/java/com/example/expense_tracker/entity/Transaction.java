package com.example.expense_tracker.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author xiaoyang
 * @create 2025-01-17-09:26
 * @description
 */
@Data
@TableName("transaction")
public class Transaction {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String type;  // income-收入/expense-支出

    private Long categoryId;  // 分类ID

    private BigDecimal amount;

    private String description;

    private Date transactionDate;

    private Date createdAt;

    private Date updatedAt;
}
