package com.example.expense_tracker.service.impl;

import com.example.expense_tracker.dto.BudgetExecutionDTO;
import com.example.expense_tracker.entity.User;
import com.example.expense_tracker.service.NotificationService;
import org.springframework.stereotype.Service;

import java.util.List;
/**
 * 通知服务实现
 * @author xiaoyang
 * @create 2025-01-22-14:44
 * @description
 */

@Service
public class NotificationServiceImpl implements NotificationService {

    @Override
    public void sendBudgetAlert(User user, BudgetExecutionDTO execution) {
        // 发送预算使用率预警通知
        System.out.println("发送给 " + user.getUsername() + " 的预算使用率预警: " + execution.getCategoryName() + " 已使用 " + execution.getUsageRate() + "%");
    }

    @Override
    public void sendMonthlyReport(User user, List<BudgetExecutionDTO> executions) {
        // 发送月度预算执行报告
        System.out.println("发送给 " + user.getUsername() + " 的月度预算执行报告");
    }

    @Override
    public void sendOverBudgetAlert(User user, BudgetExecutionDTO execution) {
        // 发送超预算预警通知
        System.out.println("发送给 " + user.getUsername() + " 的超预算预警: " + execution.getCategoryName() + " 已超出预算");
    }
}
