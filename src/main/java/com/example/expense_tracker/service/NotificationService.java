package com.example.expense_tracker.service;

import com.example.expense_tracker.dto.BudgetExecutionDTO;
import com.example.expense_tracker.entity.User;

import java.util.List;

/**
 * @author xiaoyang
 * @create 2025-01-22-14:44
 * @description
 */
public interface NotificationService {
    void sendBudgetAlert(User user, BudgetExecutionDTO execution);
    void sendMonthlyReport(User user, List<BudgetExecutionDTO> executions);
    void sendOverBudgetAlert(User user, BudgetExecutionDTO execution);
}
