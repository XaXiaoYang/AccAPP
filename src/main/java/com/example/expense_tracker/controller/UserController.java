package com.example.expense_tracker.controller;

import com.example.expense_tracker.dto.LoginDTO;
import com.example.expense_tracker.dto.UserDTO;
import com.example.expense_tracker.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author xiaoyang
 * @create 2025-01-17-10:00
 * @description
 */
@Slf4j
@Api(tags = "用户管理")
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @ApiOperation("用户注册")
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserDTO userDTO) {
        userService.register(userDTO);
        return ResponseEntity.ok().body("注册成功");
    }

    @ApiOperation("用户登录")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginDTO) {
        // 记录非敏感信息的日志
        log.info("收到登录请求: 用户名={}", loginDTO.getUsername());
        try {
            String token = userService.login(loginDTO);
            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("登录失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @ApiOperation("货币单位偏好设置")
    @PutMapping("/{userId}/currency")
    public ResponseEntity<String> updateCurrencyUnit(@PathVariable Long userId, @RequestBody String currencyUnit) {
        userService.updateCurrencyUnit(userId, currencyUnit);
        return ResponseEntity.ok("货币单位设置成功");
    }
}