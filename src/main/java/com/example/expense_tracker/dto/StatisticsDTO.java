package com.example.expense_tracker.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author xiaoyang
 * @create 2025-01-17-13:28
 * @description
 */

@Data
@ApiModel("统计信息")
public class StatisticsDTO {
    @ApiModelProperty("总收入")
    private BigDecimal totalIncome;

    @ApiModelProperty("总支出")
    private BigDecimal totalExpense;

    @ApiModelProperty("结余")
    private BigDecimal balance;

    @ApiModelProperty("月度统计")
    private List<MonthlyStatistics> monthlyStats;

    @Data
    public static class MonthlyStatistics {
        @ApiModelProperty("年月")
        private String yearMonth;  // 格式：YYYY-MM

        @ApiModelProperty("收入")
        private BigDecimal income;

        @ApiModelProperty("支出")
        private BigDecimal expense;

        @ApiModelProperty("结余")
        private BigDecimal balance;
    }
}
