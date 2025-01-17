package com.example.expense_tracker.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.expense_tracker.entity.Budget;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author xiaoyang
 * @create 2025-01-17-14:49
 * @description
 */
@Mapper
public interface BudgetMapper extends BaseMapper<Budget> {
}
