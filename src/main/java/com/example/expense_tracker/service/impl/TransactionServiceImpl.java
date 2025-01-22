package com.example.expense_tracker.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.expense_tracker.dto.CategoryStatisticsDTO;
import com.example.expense_tracker.dto.StatisticsDTO;
import com.example.expense_tracker.dto.TransactionDTO;
import com.example.expense_tracker.entity.Category;
import com.example.expense_tracker.entity.Transaction;
import com.example.expense_tracker.mapper.CategoryMapper;
import com.example.expense_tracker.mapper.TransactionMapper;
import com.example.expense_tracker.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author xiaoyang
 * @create 2025-01-17-09:27
 * @description
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionServiceImpl implements TransactionService {

    private final TransactionMapper transactionMapper;
    private final CategoryMapper categoryMapper;
    private static final SimpleDateFormat MONTH_FORMAT = new SimpleDateFormat("yyyy-MM");

    @Override
    @Transactional
    public Transaction addTransaction(Long userId, TransactionDTO dto) {
        // 验证分类是否存在且属于该用户或是系统预设分类
        Category category = categoryMapper.selectById(dto.getCategoryId());
        if (category == null || (category.getUserId() != null && !category.getUserId().equals(userId))) {
            throw new RuntimeException("无效的分类ID");
        }
        // 验证分类类型与交易类型是否匹配
        if (!category.getType().equals(dto.getType())) {
            throw new RuntimeException("分类类型与交易类型不匹配");
        }
        Transaction transaction = new Transaction();
        transaction.setUserId(userId);
        transaction.setType(dto.getType());
        transaction.setCategoryId(dto.getCategoryId());
        transaction.setAmount(dto.getAmount());
        transaction.setDescription(dto.getDescription());
        transaction.setTransactionDate(new Date());
        transaction.setCreatedAt(new Date());
        transaction.setUpdatedAt(new Date());
        transactionMapper.insert(transaction);
        return transaction;
    }

    @Override
    public List<Transaction> getUserTransactions(Long userId) {
        return transactionMapper.selectList(
                new QueryWrapper<Transaction>()
                        .eq("user_id", userId)
                        .orderByDesc("transaction_date")
        );
    }

    @Override
    public StatisticsDTO getStatistics(Long userId) {
        List<Transaction> transactions = getUserTransactions(userId);
        return calculateStatistics(transactions);
    }

    @Override
    public StatisticsDTO getStatisticsByDateRange(Long userId, String startDate, String endDate) {
        QueryWrapper<Transaction> queryWrapper = new QueryWrapper<Transaction>()
                .eq("user_id", userId)
                .ge(startDate != null, "transaction_date", startDate)
                .le(endDate != null, "transaction_date", endDate)
                .orderByDesc("transaction_date");

        List<Transaction> transactions = transactionMapper.selectList(queryWrapper);
        return calculateStatistics(transactions);
    }

    private StatisticsDTO calculateStatistics(List<Transaction> transactions) {
        StatisticsDTO statistics = new StatisticsDTO();
        // 计算总收入和支出
        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal totalExpense = BigDecimal.ZERO;
        // 按月份分组的统计数据
        Map<String, StatisticsDTO.MonthlyStatistics> monthlyStatsMap = new HashMap<>();
        for (Transaction transaction : transactions) {
            String monthKey = MONTH_FORMAT.format(transaction.getTransactionDate());
            // 获取或创建月度统计对象
            StatisticsDTO.MonthlyStatistics monthStats = monthlyStatsMap.computeIfAbsent(
                    monthKey,
                    k -> {
                        StatisticsDTO.MonthlyStatistics stats = new StatisticsDTO.MonthlyStatistics();
                        stats.setYearMonth(k);
                        stats.setIncome(BigDecimal.ZERO);
                        stats.setExpense(BigDecimal.ZERO);
                        return stats;
                    }
            );

            // 更新统计数据
            if ("income".equals(transaction.getType())) {
                totalIncome = totalIncome.add(transaction.getAmount());
                monthStats.setIncome(monthStats.getIncome().add(transaction.getAmount()));
            } else if ("expense".equals(transaction.getType())) {
                totalExpense = totalExpense.add(transaction.getAmount());
                monthStats.setExpense(monthStats.getExpense().add(transaction.getAmount()));
            }
            // 计算月度结余
            monthStats.setBalance(monthStats.getIncome().subtract(monthStats.getExpense()));
        }
        // 设置总计数据
        statistics.setTotalIncome(totalIncome);
        statistics.setTotalExpense(totalExpense);
        statistics.setBalance(totalIncome.subtract(totalExpense));
        // 设置月度统计数据（按月份倒序排序）
        List<StatisticsDTO.MonthlyStatistics> monthlyStatsList = new ArrayList<>(monthlyStatsMap.values());
        monthlyStatsList.sort((a, b) -> b.getYearMonth().compareTo(a.getYearMonth()));
        statistics.setMonthlyStats(monthlyStatsList);
        return statistics;
    }

    @Override

    public CategoryStatisticsDTO getCategoryStatistics(Long userId, String startDate, String endDate) {
        // 获取交易记录
        QueryWrapper<Transaction> queryWrapper = new QueryWrapper<Transaction>()
                .eq("user_id", userId)
                .ge(startDate != null, "transaction_date", startDate)
                .le(endDate != null, "transaction_date", endDate);
        List<Transaction> transactions = transactionMapper.selectList(queryWrapper);
        // 获取所有分类
        List<Category> categories = categoryMapper.selectList(null);
        Map<Long, Category> categoryMap = categories.stream()
                .collect(Collectors.toMap(Category::getId, category -> category));
        // 按类型和分类ID分组统计
        Map<String, Map<Long, List<Transaction>>> typeAndCategoryTransactions = transactions.stream()
                .collect(Collectors.groupingBy(
                        Transaction::getType,
                        Collectors.groupingBy(Transaction::getCategoryId)
                ));
        CategoryStatisticsDTO result = new CategoryStatisticsDTO();
        // 计算收入统计
        BigDecimal totalIncome = calculateTotalAmount(transactions, "income");
        result.setIncomeStats(calculateCategoryStats(
                typeAndCategoryTransactions.getOrDefault("income", new HashMap<>()),
                categoryMap,
                totalIncome
        ));

        // 计算支出统计
        BigDecimal totalExpense = calculateTotalAmount(transactions, "expense");
        result.setExpenseStats(calculateCategoryStats(
                typeAndCategoryTransactions.getOrDefault("expense", new HashMap<>()),
                categoryMap,
                totalExpense
        ));
        return result;
    }
    @Override
    public List<Transaction> getUserTransactionsByDateRange(Long userId, Date startDate, Date endDate) {
        return transactionMapper.selectList(new QueryWrapper<Transaction>()
                .eq("user_id", userId)
                .ge("transaction_date", startDate)
                .lt("transaction_date", endDate)
                .orderByAsc("transaction_date"));
    }

    @Override
    public StatisticsDTO getIncomeExpenseTrend(Long userId, String period) {
        return null;
    }

    @Override
    public StatisticsDTO getExpenseCategoryData(Long userId) {
        return null;
    }

    @Override
    public StatisticsDTO getIncomeSourceData(Long userId) {
        return null;
    }

    @Override
    public StatisticsDTO getComparisonData(Long userId, String type) {
        return null;
    }

    @Override
    @Transactional
    public void importTransactions(Long userId, MultipartFile file) throws Exception {
        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0); // 获取第一个工作表
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // 跳过标题行

                // 解析每一行数据
                String type = row.getCell(0).getStringCellValue();
                Long categoryId = (long) row.getCell(1).getNumericCellValue();
                BigDecimal amount = BigDecimal.valueOf(row.getCell(2).getNumericCellValue());
                String description = row.getCell(3).getStringCellValue();
                Date transactionDate = row.getCell(4).getDateCellValue();

                // 创建 Transaction 对象
                Transaction transaction = new Transaction();
                transaction.setUserId(userId);
                transaction.setType(type);
                transaction.setCategoryId(categoryId);
                transaction.setAmount(amount);
                transaction.setDescription(description);
                transaction.setTransactionDate(transactionDate);
                transaction.setCreatedAt(new Date());
                transaction.setUpdatedAt(new Date());

                // 保存到数据库
                transactionMapper.insert(transaction);
            }
        }
    }


    private List<CategoryStatisticsDTO.CategoryStats> calculateCategoryStats(

            Map<Long, List<Transaction>> categoryTransactions,
            Map<Long, Category> categoryMap,
            BigDecimal total) {
        return categoryTransactions.entrySet().stream()
                .map(entry -> {
                    CategoryStatisticsDTO.CategoryStats stats = new CategoryStatisticsDTO.CategoryStats();
                    Long categoryId = entry.getKey();
                    List<Transaction> transactions = entry.getValue();
                    Category category = categoryMap.get(categoryId);
                    stats.setCategoryId(categoryId);
                    stats.setCategoryName(category != null ? category.getName() : "未知分类");
                    BigDecimal amount = transactions.stream()
                            .map(Transaction::getAmount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    stats.setAmount(amount);
                    stats.setCount(transactions.size());

                    // 计算百分比
                    if (total.compareTo(BigDecimal.ZERO) > 0) {
                        stats.setPercentage(amount.multiply(new BigDecimal("100"))
                                .divide(total, 2, RoundingMode.HALF_UP));
                    } else {
                        stats.setPercentage(BigDecimal.ZERO);
                    }
                    return stats;
                })
                .sorted((a, b) -> b.getAmount().compareTo(a.getAmount()))
                .collect(Collectors.toList());
    }


    private BigDecimal calculateTotalAmount(List<Transaction> transactions, String type) {
        return transactions.stream()
                .filter(t -> type.equals(t.getType()))
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public void addTag(Long transactionId, String tag) {
        Transaction transaction = transactionMapper.selectById(transactionId);
        if (transaction != null) {
            String currentTags = transaction.getTags();
            if (currentTags == null || currentTags.isEmpty()) {
                transaction.setTags(tag);
            } else if (!currentTags.contains(tag)) {
                transaction.setTags(currentTags + "," + tag);
            }
            transactionMapper.updateById(transaction);
        }
    }

    @Override
    public void removeTag(Long transactionId, String tag) {
        Transaction transaction = transactionMapper.selectById(transactionId);
        if (transaction != null && transaction.getTags() != null) {
            List<String> tags = new ArrayList<>(Arrays.asList(transaction.getTags().split(",")));
            tags.remove(tag);
            transaction.setTags(String.join(",", tags));
            transactionMapper.updateById(transaction);
        }
    }

    @Override
    public List<Transaction> findTransactionsByTag(Long userId, String tag) {
        return transactionMapper.selectList(
                new QueryWrapper<Transaction>()
                        .eq("user_id", userId)
                        .like("tags", tag)
        );
    }

    @Override
    public void addAttachment(Long transactionId, String attachmentUrl) {
        Transaction transaction = transactionMapper.selectById(transactionId);
        if (transaction != null) {
            transaction.setAttachmentUrl(attachmentUrl);
            transactionMapper.updateById(transaction);
        }
    }

}