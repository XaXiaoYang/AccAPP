package com.example.expense_tracker.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author xiaoyang
 * @create 2025-01-17-09:27
 * @description
 */
@Data
@ApiModel("交易记录")
public class TransactionDTO {
    @ApiModelProperty("交易ID")
    private Long id;

    @ApiModelProperty(value = "交易类型", required = true, notes = "income-收入/expense-支出")
    private String type;

    @ApiModelProperty(value = "分类ID", required = true)

    private Long categoryId;

    @ApiModelProperty(value = "金额", required = true)
    private BigDecimal amount;

    @ApiModelProperty("描述")
    private String description;

    @ApiModelProperty("交易时间")
    private Date transactionDate;
}
