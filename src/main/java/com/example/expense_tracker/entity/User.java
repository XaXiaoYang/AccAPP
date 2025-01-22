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
    private String currencyUnit;//货币单位

    // Getter 和 Setter
    public String getCurrencyUnit() {
        return currencyUnit;
    }

    public void setCurrencyUnit(String currencyUnit) {
        this.currencyUnit = currencyUnit;
    }
}
