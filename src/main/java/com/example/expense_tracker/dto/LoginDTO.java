package com.example.expense_tracker.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author xiaoyang
 * @create 2025-01-17-09:57
 * @description
 */
@Data
@ApiModel("登录请求")
public class LoginDTO {
    @ApiModelProperty(value = "用户名", required = true, example = "test")
    private String username;

    @ApiModelProperty(value = "密码", required = true, example = "123456")
    private String password;
}