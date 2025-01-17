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
@ApiModel("预算信息")
public class BudgetDTO {
    @ApiModelProperty("预算ID")
    private Long id;

    @ApiModelProperty(value = "分类ID", required = true)
    private Long categoryId;

    @ApiModelProperty(value = "预算金额", required = true)
    private BigDecimal amount;

    @ApiModelProperty(value = "年份", required = true)
    private Integer year;

    @ApiModelProperty(value = "月份", required = true, notes = "1-12")
    private Integer month;
}
