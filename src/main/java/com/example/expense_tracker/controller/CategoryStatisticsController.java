package com.example.expense_tracker.controller;

import com.example.expense_tracker.service.TransactionService;
import com.example.expense_tracker.common.util.JwtTokenProvider;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Api(tags = "分类统计")
@RestController
@RequestMapping("/api/statistics/category")
@RequiredArgsConstructor
public class CategoryStatisticsController {

    private final TransactionService transactionService;
    private final JwtTokenProvider jwtTokenProvider;
    private final HttpServletRequest request;

    @ApiOperation("获取分类统计信息")
    @GetMapping
    public ResponseEntity<?> getCategoryStatistics(
            @ApiParam("开始日期 (yyyy-MM-dd)") @RequestParam(required = false) String startDate,
            @ApiParam("结束日期 (yyyy-MM-dd)") @RequestParam(required = false) String endDate) {
        try {
            String token = jwtTokenProvider.resolveToken(request);
            Long userId = jwtTokenProvider.getUserId(token);
            return ResponseEntity.ok(transactionService.getCategoryStatistics(userId, startDate, endDate));
        } catch (Exception e) {
            log.error("获取分类统计信息失败", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}