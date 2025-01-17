package com.example.expense_tracker.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.util.Date;

/**
 * @author xiaoyang
 * @create 2025-01-17-13:53
 * @description
 */

@Data
@TableName("category")
public class Category {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String type;  // income-收入/expense-支出

    private Long userId;  // null表示系统预设分类

    private Date createdAt;

    private Date updatedAt;
}