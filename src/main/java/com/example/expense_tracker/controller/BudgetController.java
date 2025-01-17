package com.example.expense_tracker.controller;

import com.example.expense_tracker.common.util.JwtTokenProvider;
import com.example.expense_tracker.dto.BudgetDTO;
import com.example.expense_tracker.service.BudgetService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;

/**
 * @author xiaoyang
 * @create 2025-01-17-14:51
 * @description
 */

@Slf4j
@Api(tags = "预算管理")
@RestController
@RequestMapping("/api/budgets")
@RequiredArgsConstructor
public class BudgetController {

    private final BudgetService budgetService;
    private final JwtTokenProvider jwtTokenProvider;
    private final HttpServletRequest request;

    @ApiOperation("设置预算")
    @PostMapping
    public ResponseEntity<?> setBudget(@RequestBody BudgetDTO budgetDTO) {
        try {
            String token = jwtTokenProvider.resolveToken(request);
            Long userId = jwtTokenProvider.getUserId(token);
            return ResponseEntity.ok(budgetService.setBudget(userId, budgetDTO));
        } catch (Exception e) {
            log.error("设置预算失败", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @ApiOperation("获取月度预算")
    @GetMapping
    public ResponseEntity<?> getMonthlyBudgets(
            @ApiParam("年份") @RequestParam(required = false) Integer year,
            @ApiParam("月份") @RequestParam(required = false) Integer month) {
        try {
            String token = jwtTokenProvider.resolveToken(request);
            Long userId = jwtTokenProvider.getUserId(token);

            // 如果未指定年月，使用当前年月
            LocalDate now = LocalDate.now();
            year = year != null ? year : now.getYear();
            month = month != null ? month : now.getMonthValue();

            return ResponseEntity.ok(budgetService.getMonthlyBudgets(userId, year, month));
        } catch (Exception e) {
            log.error("获取预算失败", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @ApiOperation("获取预算执行情况")
    @GetMapping("/execution")
    public ResponseEntity<?> getBudgetExecution(
            @ApiParam("年份") @RequestParam(required = false) Integer year,
            @ApiParam("月份") @RequestParam(required = false) Integer month) {
        try {
            String token = jwtTokenProvider.resolveToken(request);
            Long userId = jwtTokenProvider.getUserId(token);

            // 如果未指定年月，使用当前年月
            LocalDate now = LocalDate.now();
            year = year != null ? year : now.getYear();
            month = month != null ? month : now.getMonthValue();

            return ResponseEntity.ok(budgetService.getBudgetExecution(userId, year, month));
        } catch (Exception e) {
            log.error("获取预算执行情况失败", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @ApiOperation("删除预算")
    @DeleteMapping("/{budgetId}")
    public ResponseEntity<?> deleteBudget(@PathVariable Long budgetId) {
        try {
            String token = jwtTokenProvider.resolveToken(request);
            Long userId = jwtTokenProvider.getUserId(token);
            budgetService.deleteBudget(userId, budgetId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("删除预算失败", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
