package com.example.expense_tracker.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author xiaoyang  测试环境搭建使用
 * @create 2025-01-16-19:35
 * @description
 */
@Slf4j
@Api(tags = "测试接口", description = "用于测试的相关接口")
@RestController
@RequestMapping("/api/test")
public class TestController {

    @ApiOperation("Hello接口")
    @GetMapping("/hello")
    public String hello() {
        log.info("访问hello接口");
        return "Hello, Expense Tracker!";
    }

    @ApiOperation("status接口")
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> status() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "OK");
        response.put("timestamp", System.currentTimeMillis());
        response.put("service", "Expense Tracker API");
        response.put("version", "1.0.0");

        return ResponseEntity.ok(response);
    }

    @ApiOperation("消息测试接口")
    @GetMapping("/echo/{message}")
    public ResponseEntity<Map<String, String>> echo(@PathVariable String message) {
        Map<String, String> response = new HashMap<>();
        response.put("message", message);
        response.put("timestamp", String.valueOf(System.currentTimeMillis()));

        return ResponseEntity.ok(response);
    }
    @ApiOperation("validate接口")
    @PostMapping("/validate")
    public ResponseEntity<Map<String, Object>> validate(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        response.put("received", request);
        response.put("valid", true);
        response.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.ok(response);
    }
    @ApiOperation("error-test接口")
    @GetMapping("/error-test")
    public ResponseEntity<Object> errorTest(@RequestParam(required = false) String type) {
        if ("404".equals(type)) {
            return ResponseEntity.notFound().build();
        } else if ("400".equals(type)) {
            return ResponseEntity.badRequest().body("Bad Request Test");
        } else if ("500".equals(type)) {
            throw new RuntimeException("Internal Server Error Test");
        }
        return ResponseEntity.ok("No Error");
    }
}