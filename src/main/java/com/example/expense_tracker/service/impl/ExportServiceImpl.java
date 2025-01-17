package com.example.expense_tracker.service.impl;

import com.example.expense_tracker.dto.BudgetExecutionDTO;
import com.example.expense_tracker.entity.Category;
import com.example.expense_tracker.entity.Transaction;
import com.example.expense_tracker.service.BudgetService;
import com.example.expense_tracker.service.CategoryService;
import com.example.expense_tracker.service.ExportService;
import com.example.expense_tracker.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExportServiceImpl implements ExportService {

    private final TransactionService transactionService;
    private final CategoryService categoryService;
    private final BudgetService budgetService;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public Resource exportTransactions(Long userId, Integer year, Integer month) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("交易记录");

            // 设置列宽
            sheet.setColumnWidth(0, 20 * 256); // 交易时间
            sheet.setColumnWidth(1, 10 * 256); // 类型
            sheet.setColumnWidth(2, 15 * 256); // 分类
            sheet.setColumnWidth(3, 15 * 256); // 金额
            sheet.setColumnWidth(4, 30 * 256); // 描述

            // 创建标题行样式
            CellStyle headerStyle = createHeaderStyle(workbook);

            // 创建数字格式
            CellStyle amountStyle = workbook.createCellStyle();
            amountStyle.setDataFormat(workbook.createDataFormat().getFormat("#,##0.00"));

            // 创建标题行
            Row headerRow = sheet.createRow(0);
            String[] headers = {"交易时间", "类型", "分类", "金额", "描述"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // 获取指定月份的起止时间
            LocalDate startDate = LocalDate.of(year, month, 1);
            LocalDate endDate = startDate.plusMonths(1).minusDays(1);
            Date start = Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
            Date end = Date.from(endDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());

            // 获取交易记录
            List<Transaction> transactions = transactionService.getUserTransactionsByDateRange(userId, start, end);

            // 获取分类信息
            Map<Long, Category> categoryMap = categoryService.getUserCategories(userId).stream()
                    .collect(Collectors.toMap(Category::getId, category -> category));

            // 填充数据
            int rowNum = 1;
            for (Transaction transaction : transactions) {
                Row row = sheet.createRow(rowNum++);

                // 交易时间
                Cell dateCell = row.createCell(0);
                dateCell.setCellValue(DATE_FORMAT.format(transaction.getTransactionDate()));

                // 类型
                Cell typeCell = row.createCell(1);
                typeCell.setCellValue("income".equals(transaction.getType()) ? "收入" : "支出");

                // 分类
                Cell categoryCell = row.createCell(2);
                Category category = categoryMap.get(transaction.getCategoryId());
                categoryCell.setCellValue(category != null ? category.getName() : "未知分类");

                // 金额
                Cell amountCell = row.createCell(3);
                amountCell.setCellValue(transaction.getAmount().doubleValue());
                amountCell.setCellStyle(amountStyle);

                // 描述
                Cell descCell = row.createCell(4);
                descCell.setCellValue(transaction.getDescription());
            }

            // 添加合计行
            Row totalRow = sheet.createRow(rowNum);
            totalRow.createCell(0).setCellValue("合计");
            Cell totalAmountCell = totalRow.createCell(3);
            totalAmountCell.setCellStyle(amountStyle);
            totalAmountCell.setCellFormula("SUM(D2:D" + rowNum + ")");

            return generateResource(workbook);
        } catch (Exception e) {
            log.error("导出交易记录失败", e);
            throw new RuntimeException("导出失败");
        }
    }

    @Override
    public Resource exportBudgetExecution(Long userId, Integer year, Integer month) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("预算执行情况");

            // 设置列宽
            sheet.setColumnWidth(0, 15 * 256); // 分类
            sheet.setColumnWidth(1, 15 * 256); // 预算金额
            sheet.setColumnWidth(2, 15 * 256); // 实际支出
            sheet.setColumnWidth(3, 15 * 256); // 剩余预算
            sheet.setColumnWidth(4, 12 * 256); // 使用率
            sheet.setColumnWidth(5, 12 * 256); // 是否超预算

            // 创建标题行样式
            CellStyle headerStyle = createHeaderStyle(workbook);

            // 创建数字格式
            CellStyle amountStyle = workbook.createCellStyle();
            amountStyle.setDataFormat(workbook.createDataFormat().getFormat("#,##0.00"));

            // 创建百分比格式
            CellStyle percentStyle = workbook.createCellStyle();
            percentStyle.setDataFormat(workbook.createDataFormat().getFormat("0.00%"));

            // 创建标题行
            Row headerRow = sheet.createRow(0);
            String[] headers = {"分类", "预算金额", "实际支出", "剩余预算", "使用率", "是否超预算"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // 获取预算执行情况
            List<BudgetExecutionDTO> executions = budgetService.getBudgetExecution(userId, year, month);

            // 填充数据
            int rowNum = 1;
            for (BudgetExecutionDTO execution : executions) {
                Row row = sheet.createRow(rowNum++);

                // 分类名称
                row.createCell(0).setCellValue(execution.getCategoryName());

                // 预算金额
                Cell budgetCell = row.createCell(1);
                budgetCell.setCellValue(execution.getBudgetAmount().doubleValue());
                budgetCell.setCellStyle(amountStyle);

                // 实际支出
                Cell actualCell = row.createCell(2);
                actualCell.setCellValue(execution.getActualAmount().doubleValue());
                actualCell.setCellStyle(amountStyle);

                // 剩余预算
                Cell remainingCell = row.createCell(3);
                remainingCell.setCellValue(execution.getRemainingAmount().doubleValue());
                remainingCell.setCellStyle(amountStyle);

                // 使用率
                Cell usageCell = row.createCell(4);
                usageCell.setCellValue(execution.getUsageRate().doubleValue() / 100);
                usageCell.setCellStyle(percentStyle);

                // 是否超预算
                row.createCell(5).setCellValue(execution.getIsOverBudget() ? "是" : "否");
            }

            // 添加合计行
            Row totalRow = sheet.createRow(rowNum);
            totalRow.createCell(0).setCellValue("合计");

            // 预算总额
            Cell totalBudgetCell = totalRow.createCell(1);
            totalBudgetCell.setCellStyle(amountStyle);
            totalBudgetCell.setCellFormula("SUM(B2:B" + rowNum + ")");

            // 实际支出总额
            Cell totalActualCell = totalRow.createCell(2);
            totalActualCell.setCellStyle(amountStyle);
            totalActualCell.setCellFormula("SUM(C2:C" + rowNum + ")");

            // 剩余预算总额
            Cell totalRemainingCell = totalRow.createCell(3);
            totalRemainingCell.setCellStyle(amountStyle);
            totalRemainingCell.setCellFormula("SUM(D2:D" + rowNum + ")");

            // 总体使用率
            Cell totalUsageCell = totalRow.createCell(4);
            totalUsageCell.setCellStyle(percentStyle);
            totalUsageCell.setCellFormula("C" + (rowNum + 1) + "/B" + (rowNum + 1));

            return generateResource(workbook);
        } catch (Exception e) {
            log.error("导出预算执行情况失败", e);
            throw new RuntimeException("导出失败");
        }
    }

    @Override
    public Resource exportAnnualReport(Long userId, Integer year) {
        try (Workbook workbook = new XSSFWorkbook()) {
            createMonthlySheet(workbook, userId, year);
            createCategorySheet(workbook, userId, year);
            return generateResource(workbook);
        } catch (Exception e) {
            log.error("导出年度报表失败", e);
            throw new RuntimeException("导出失败");
        }
    }

    private void createMonthlySheet(Workbook workbook, Long userId, Integer year) {
        Sheet sheet = workbook.createSheet("月度收支统计");

        // 设置列宽
        sheet.setColumnWidth(0, 12 * 256); // 月份
        sheet.setColumnWidth(1, 15 * 256); // 总收入
        sheet.setColumnWidth(2, 15 * 256); // 总支出
        sheet.setColumnWidth(3, 15 * 256); // 结余

        // 创建样式
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle amountStyle = workbook.createCellStyle();
        amountStyle.setDataFormat(workbook.createDataFormat().getFormat("#,##0.00"));

        // 创建标题行
        Row headerRow = sheet.createRow(0);
        String[] headers = {"月份", "总收入", "总支出", "结余"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // 获取每月数据并填充
        int rowNum = 1;
        BigDecimal yearTotalIncome = BigDecimal.ZERO;
        BigDecimal yearTotalExpense = BigDecimal.ZERO;

        for (int month = 1; month <= 12; month++) {
            // 获取月份的起止时间
            LocalDate startDate = LocalDate.of(year, month, 1);
            LocalDate endDate = startDate.plusMonths(1).minusDays(1);
            Date start = Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
            Date end = Date.from(endDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());

            // 获取交易记录
            List<Transaction> transactions = transactionService.getUserTransactionsByDateRange(userId, start, end);

            // 计算月度收支
            BigDecimal monthlyIncome = transactions.stream()
                    .filter(t -> "income".equals(t.getType()))
                    .map(Transaction::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal monthlyExpense = transactions.stream()
                    .filter(t -> "expense".equals(t.getType()))
                    .map(Transaction::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal monthlyBalance = monthlyIncome.subtract(monthlyExpense);

            // 更新年度总计
            yearTotalIncome = yearTotalIncome.add(monthlyIncome);
            yearTotalExpense = yearTotalExpense.add(monthlyExpense);

            // 填充数据行
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(month + "月");

            Cell incomeCell = row.createCell(1);
            incomeCell.setCellValue(monthlyIncome.doubleValue());
            incomeCell.setCellStyle(amountStyle);

            Cell expenseCell = row.createCell(2);
            expenseCell.setCellValue(monthlyExpense.doubleValue());
            expenseCell.setCellStyle(amountStyle);

            Cell balanceCell = row.createCell(3);
            balanceCell.setCellValue(monthlyBalance.doubleValue());
            balanceCell.setCellStyle(amountStyle);
        }

        // 添加年度总计行
        Row totalRow = sheet.createRow(rowNum);
        totalRow.createCell(0).setCellValue("全年");

        Cell totalIncomeCell = totalRow.createCell(1);
        totalIncomeCell.setCellValue(yearTotalIncome.doubleValue());
        totalIncomeCell.setCellStyle(amountStyle);

        Cell totalExpenseCell = totalRow.createCell(2);
        totalExpenseCell.setCellValue(yearTotalExpense.doubleValue());
        totalExpenseCell.setCellStyle(amountStyle);

        Cell totalBalanceCell = totalRow.createCell(3);
        totalBalanceCell.setCellValue(yearTotalIncome.subtract(yearTotalExpense).doubleValue());
        totalBalanceCell.setCellStyle(amountStyle);
    }

    private void createCategorySheet(Workbook workbook, Long userId, Integer year) {
        Sheet sheet = workbook.createSheet("分类统计");

        // 设置列宽
        sheet.setColumnWidth(0, 15 * 256); // 分类
        sheet.setColumnWidth(1, 12 * 256); // 交易笔数
        sheet.setColumnWidth(2, 15 * 256); // 总金额
        sheet.setColumnWidth(3, 12 * 256); // 占比

        // 创建样式
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle amountStyle = workbook.createCellStyle();
        amountStyle.setDataFormat(workbook.createDataFormat().getFormat("#,##0.00"));
        CellStyle percentStyle = workbook.createCellStyle();
        percentStyle.setDataFormat(workbook.createDataFormat().getFormat("0.00%"));

        // 创建收入和支出两个子表
        createCategorySubSheet(sheet, userId, year, "income", "收入分类统计", 0, headerStyle, amountStyle, percentStyle);
        createCategorySubSheet(sheet, userId, year, "expense", "支出分类统计",
                sheet.getLastRowNum() + 3, headerStyle, amountStyle, percentStyle);
    }

    private void createCategorySubSheet(Sheet sheet, Long userId, Integer year, String type,
                                        String title, int startRow, CellStyle headerStyle, CellStyle amountStyle, CellStyle percentStyle) {

        // 获取年度的起止时间
        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = LocalDate.of(year, 12, 31);
        Date start = Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date end = Date.from(endDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());

        // 获取交易记录
        List<Transaction> transactions = transactionService.getUserTransactionsByDateRange(userId, start, end)
                .stream()
                .filter(t -> type.equals(t.getType()))
                .collect(Collectors.toList());

        // 获取分类信息
        Map<Long, Category> categoryMap = categoryService.getUserCategories(userId).stream()
                .filter(c -> type.equals(c.getType()))
                .collect(Collectors.toMap(Category::getId, c -> c));

        // 按分类统计
        Map<Long, List<Transaction>> categoryTransactions = transactions.stream()
                .collect(Collectors.groupingBy(Transaction::getCategoryId));

        // 计算总金额
        BigDecimal total = transactions.stream()
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 创建标题
        Row titleRow = sheet.createRow(startRow);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue(title);
        titleCell.setCellStyle(headerStyle);

        // 创建表头
        Row headerRow = sheet.createRow(startRow + 1);
        String[] headers = {"分类", "交易笔数", "总金额", "占比"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // 填充数据
        int rowNum = startRow + 2;
        for (Map.Entry<Long, List<Transaction>> entry : categoryTransactions.entrySet()) {
            Category category = categoryMap.get(entry.getKey());
            if (category == null) continue;

            List<Transaction> categoryTrans = entry.getValue();
            BigDecimal categoryTotal = categoryTrans.stream()
                    .map(Transaction::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            Row row = sheet.createRow(rowNum++);

            // 分类名称
            row.createCell(0).setCellValue(category.getName());

            // 交易笔数
            row.createCell(1).setCellValue(categoryTrans.size());

            // 总金额
            Cell amountCell = row.createCell(2);
            amountCell.setCellValue(categoryTotal.doubleValue());
            amountCell.setCellStyle(amountStyle);

            // 占比
            Cell percentCell = row.createCell(3);
            if (total.compareTo(BigDecimal.ZERO) > 0) {
                percentCell.setCellValue(categoryTotal.divide(total, 4, RoundingMode.HALF_UP).doubleValue());
            } else {
                percentCell.setCellValue(0);
            }
            percentCell.setCellStyle(percentStyle);
        }

        // 添加合计行
        Row totalRow = sheet.createRow(rowNum);
        totalRow.createCell(0).setCellValue("合计");
        totalRow.createCell(1).setCellValue(transactions.size());

        Cell totalAmountCell = totalRow.createCell(2);
        totalAmountCell.setCellValue(total.doubleValue());
        totalAmountCell.setCellStyle(amountStyle);
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);

        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);

        return style;
    }

    private Resource generateResource(Workbook workbook) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        return new ByteArrayResource(outputStream.toByteArray());
    }
}