package com.example.expense_tracker.service;

import com.example.expense_tracker.dto.BudgetDTO;
import com.example.expense_tracker.dto.BudgetExecutionDTO;
import com.example.expense_tracker.entity.Budget;

import java.util.List;

/**
 * @author xiaoyang
 * @create 2025-01-17-14:50
 * @description
 */

public interface BudgetService {
    // 设置预算
    Budget setBudget(Long userId, BudgetDTO budgetDTO);

    // 获取指定月份的所有预算
    List<Budget> getMonthlyBudgets(Long userId, Integer year, Integer month);

    // 获取指定月份的预算执行情况
    List<BudgetExecutionDTO> getBudgetExecution(Long userId, Integer year, Integer month);

    // 删除预算
    void deleteBudget(Long userId, Long budgetId);
}