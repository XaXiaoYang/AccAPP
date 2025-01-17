package com.example.expense_tracker.service;

import org.springframework.core.io.Resource;
/**
 * @author xiaoyang
 * @create 2025-01-17-15:05
 * @description
 */
public interface ExportService {
    // 导出交易记录
    Resource exportTransactions(Long userId, Integer year, Integer month);

    // 导出预算执行情况
    Resource exportBudgetExecution(Long userId, Integer year, Integer month);

    // 导出年度报表
    Resource exportAnnualReport(Long userId, Integer year);
}
