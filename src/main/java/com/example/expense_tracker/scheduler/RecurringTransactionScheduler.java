package com.example.expense_tracker.scheduler;

import com.example.expense_tracker.entity.Transaction;
import com.example.expense_tracker.mapper.TransactionMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
/**
 * 定期账单功能（如每月固定支出）
 * @author xiaoyang
 * @create 2025-01-22-15:20
 * @description
 */

@Component
@Api(tags = "定期账单功能定时任务")
@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/api/scheduler")
public class RecurringTransactionScheduler {

    private final TransactionMapper transactionMapper;

    @ApiOperation("定期账单功能（如每月固定支出）")
    @GetMapping("/generateRecurringTransactions")
    @Scheduled(cron = "0 0 0 1 * ?") // 每月1号执行
    public void generateRecurringTransactions() {
        try {
            List<Transaction> recurringTransactions = transactionMapper.selectRecurringTransactions();
            for (Transaction transaction : recurringTransactions) {
                Transaction newTransaction = new Transaction();
                newTransaction.setUserId(transaction.getUserId());
                newTransaction.setType(transaction.getType());
                newTransaction.setCategoryId(transaction.getCategoryId());
                newTransaction.setAmount(transaction.getAmount());
                newTransaction.setDescription(transaction.getDescription());
                newTransaction.setTransactionDate(new Date());
                newTransaction.setCreatedAt(new Date());
                newTransaction.setUpdatedAt(new Date());
                transactionMapper.insert(newTransaction);
            }
        } catch (Exception e) {
            log.error("生成定期账单失败", e);
        }
    }
}