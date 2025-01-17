package com.example.expense_tracker.service;

import com.example.expense_tracker.dto.CategoryStatisticsDTO;
import com.example.expense_tracker.dto.StatisticsDTO;
import com.example.expense_tracker.dto.TransactionDTO;
import com.example.expense_tracker.entity.Transaction;

import java.util.Date;
import java.util.List;

/**
 * @author xiaoyang
 * @create 2025-01-17-09:28
 * @description
 */
public interface TransactionService {
//    添加一笔交易记录
    Transaction addTransaction(Long userId, TransactionDTO dto);
//    获取指定用户的交易记录列表。
    List<Transaction> getUserTransactions(Long userId);
//    获取指定用户的统计数据。
    StatisticsDTO getStatistics(Long userId);
//    根据日期范围获取指定用户的统计数据。
    StatisticsDTO getStatisticsByDateRange(Long userId, String startDate, String endDate);
//    根据日期范围获取指定用户的分类统计数据。
    CategoryStatisticsDTO getCategoryStatistics(Long userId, String startDate, String endDate);
    /**
     * 获取指定日期范围内的交易记录
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 交易记录列表
     */
    List<Transaction> getUserTransactionsByDateRange(Long userId, Date startDate, Date endDate);
}