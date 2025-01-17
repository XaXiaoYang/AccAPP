package com.example.expense_tracker.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.expense_tracker.entity.Transaction;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author xiaoyang
 * @create 2025-01-17-09:29
 * @description
 */
@Mapper
public interface TransactionMapper extends BaseMapper<Transaction> {
    // 自定义查询方法
}