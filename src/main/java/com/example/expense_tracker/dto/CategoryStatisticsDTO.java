package com.example.expense_tracker.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author xiaoyang
 * @create 2025-01-17-13:53
 * @description
 */

@Data
@ApiModel("分类统计信息")
public class CategoryStatisticsDTO {
    @ApiModelProperty("收入分类统计")
    private List<CategoryStats> incomeStats;

    @ApiModelProperty("支出分类统计")
    private List<CategoryStats> expenseStats;

    @Data
    public static class CategoryStats {
        @ApiModelProperty("分类ID")
        private Long categoryId;

        @ApiModelProperty("分类名称")
        private String categoryName;

        @ApiModelProperty("金额")
        private BigDecimal amount;

        @ApiModelProperty("占比")
        private BigDecimal percentage;

        @ApiModelProperty("交易笔数")
        private Integer count;
    }
}