package com.example.expense_tracker.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("分类信息")
public class CategoryDTO {
    @ApiModelProperty("分类ID")
    private Long id;

    @ApiModelProperty(value = "分类名称", required = true)
    private String name;

    @ApiModelProperty(value = "分类类型", required = true, notes = "income-收入/expense-支出")
    private String type;
}