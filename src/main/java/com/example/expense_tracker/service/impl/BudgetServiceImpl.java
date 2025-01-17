package com.example.expense_tracker.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.expense_tracker.dto.BudgetDTO;
import com.example.expense_tracker.dto.BudgetExecutionDTO;
import com.example.expense_tracker.entity.Budget;
import com.example.expense_tracker.entity.Category;
import com.example.expense_tracker.entity.Transaction;
import com.example.expense_tracker.mapper.BudgetMapper;
import com.example.expense_tracker.mapper.CategoryMapper;
import com.example.expense_tracker.mapper.TransactionMapper;
import com.example.expense_tracker.service.BudgetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author xiaoyang
 * @create 2025-01-17-14:50
 * @description
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class BudgetServiceImpl implements BudgetService {

    private final BudgetMapper budgetMapper;
    private final CategoryMapper categoryMapper;
    private final TransactionMapper transactionMapper;

    @Override
    @Transactional
    public Budget setBudget(Long userId, BudgetDTO budgetDTO) {
        // 验证分类是否存在且为支出类型
        Category category = categoryMapper.selectById(budgetDTO.getCategoryId());
        if (category == null) {
            throw new RuntimeException("分类不存在");
        }
        if (!"expense".equals(category.getType())) {
            throw new RuntimeException("只能为支出类别设置预算");
        }

        // 验证月份是否有效
        if (budgetDTO.getMonth() < 1 || budgetDTO.getMonth() > 12) {
            throw new RuntimeException("无效的月份");
        }

        // 查找已存在的预算
        Budget existingBudget = budgetMapper.selectOne(
                new QueryWrapper<Budget>()
                        .eq("user_id", userId)
                        .eq("category_id", budgetDTO.getCategoryId())
                        .eq("year", budgetDTO.getYear())
                        .eq("month", budgetDTO.getMonth())
        );

        if (existingBudget != null) {
            // 更新已存在的预算
            existingBudget.setAmount(budgetDTO.getAmount());
            existingBudget.setUpdatedAt(new Date());
            budgetMapper.updateById(existingBudget);
            return existingBudget;
        } else {
            // 创建新预算
            Budget budget = new Budget();
            budget.setUserId(userId);
            budget.setCategoryId(budgetDTO.getCategoryId());
            budget.setAmount(budgetDTO.getAmount());
            budget.setYear(budgetDTO.getYear());
            budget.setMonth(budgetDTO.getMonth());
            budget.setCreatedAt(new Date());
            budget.setUpdatedAt(new Date());

            budgetMapper.insert(budget);
            return budget;
        }
    }

    @Override
    public List<Budget> getMonthlyBudgets(Long userId, Integer year, Integer month) {
        return budgetMapper.selectList(
                new QueryWrapper<Budget>()
                        .eq("user_id", userId)
                        .eq("year", year)
                        .eq("month", month)
        );
    }

    @Override
    public List<BudgetExecutionDTO> getBudgetExecution(Long userId, Integer year, Integer month) {
        // 获取所有预算
        List<Budget> budgets = getMonthlyBudgets(userId, year, month);
        Map<Long, Budget> budgetMap = budgets.stream()
                .collect(Collectors.toMap(Budget::getCategoryId, budget -> budget));

        // 获取所有分类
        List<Category> categories = categoryMapper.selectList(
                new QueryWrapper<Category>()
                        .eq("type", "expense")
                        .and(wrapper -> wrapper
                                .isNull("user_id")
                                .or()
                                .eq("user_id", userId)
                        )
        );

        // 获取当月所有支出交易
        List<Transaction> transactions = transactionMapper.selectList(
                new QueryWrapper<Transaction>()
                        .eq("user_id", userId)
                        .eq("type", "expense")
                        .apply("YEAR(transaction_date) = {0}", year)
                        .apply("MONTH(transaction_date) = {0}", month)
        );

        // 按分类汇总支出
        Map<Long, BigDecimal> categoryExpenses = transactions.stream()
                .collect(Collectors.groupingBy(
                        Transaction::getCategoryId,
                        Collectors.reducing(
                                BigDecimal.ZERO,
                                Transaction::getAmount,
                                BigDecimal::add
                        )
                ));

        // 生成预算执行情况报告
        return categories.stream().map(category -> {
            BudgetExecutionDTO execution = new BudgetExecutionDTO();
            execution.setCategoryId(category.getId());
            execution.setCategoryName(category.getName());

            Budget budget = budgetMap.get(category.getId());
            execution.setBudgetAmount(budget != null ? budget.getAmount() : BigDecimal.ZERO);

            BigDecimal actualAmount = categoryExpenses.getOrDefault(category.getId(), BigDecimal.ZERO);
            execution.setActualAmount(actualAmount);

            BigDecimal remainingAmount = execution.getBudgetAmount().subtract(actualAmount);
            execution.setRemainingAmount(remainingAmount);

            if (execution.getBudgetAmount().compareTo(BigDecimal.ZERO) > 0) {
                execution.setUsageRate(actualAmount.multiply(new BigDecimal("100"))
                        .divide(execution.getBudgetAmount(), 2, RoundingMode.HALF_UP));
            } else {
                execution.setUsageRate(BigDecimal.ZERO);
            }

            execution.setIsOverBudget(remainingAmount.compareTo(BigDecimal.ZERO) < 0);

            return execution;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteBudget(Long userId, Long budgetId) {
        Budget budget = budgetMapper.selectById(budgetId);

        if (budget == null || !userId.equals(budget.getUserId())) {
            throw new RuntimeException("预算不存在或无权删除");
        }

        budgetMapper.deleteById(budgetId);
    }
}
