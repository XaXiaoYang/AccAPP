package com.example.expense_tracker.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author xiaoyang
 * @create 2025-01-17-14:49
 * @description
 */

@Data
@ApiModel("预算执行情况")
public class BudgetExecutionDTO {
    @ApiModelProperty("分类ID")
    private Long categoryId;

    @ApiModelProperty("分类名称")
    private String categoryName;

    @ApiModelProperty("预算金额")
    private BigDecimal budgetAmount;

    @ApiModelProperty("实际支出")
    private BigDecimal actualAmount;

    @ApiModelProperty("剩余预算")
    private BigDecimal remainingAmount;

    @ApiModelProperty("预算使用率")
    private BigDecimal usageRate;  // 百分比

    @ApiModelProperty("是否超预算")
    private Boolean isOverBudget;
}
