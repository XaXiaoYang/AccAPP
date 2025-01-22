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
@Api(tags = "统计信息")
@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final TransactionService transactionService;
    private final JwtTokenProvider jwtTokenProvider;
    private final HttpServletRequest request;

    @ApiOperation("获取总体统计信息")
    @GetMapping
    public ResponseEntity<?> getStatistics() {
        try {
            String token = jwtTokenProvider.resolveToken(request);
            Long userId = jwtTokenProvider.getUserId(token);
            return ResponseEntity.ok(transactionService.getStatistics(userId));
        } catch (Exception e) {
            log.error("获取统计信息失败", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @ApiOperation("获取指定日期范围的统计信息")
    @GetMapping("/range")
    public ResponseEntity<?> getStatisticsByDateRange(
            @ApiParam("开始日期 (yyyy-MM-dd)") @RequestParam(required = false) String startDate,
            @ApiParam("结束日期 (yyyy-MM-dd)") @RequestParam(required = false) String endDate) {
        try {
            String token = jwtTokenProvider.resolveToken(request);
            Long userId = jwtTokenProvider.getUserId(token);
            return ResponseEntity.ok(transactionService.getStatisticsByDateRange(userId, startDate, endDate));
        } catch (Exception e) {
            log.error("获取统计信息失败", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @ApiOperation("获取收支趋势数据")
    @GetMapping("/trend")
    public ResponseEntity<?> getIncomeExpenseTrend(
            @RequestParam(required = false) String period) {
        try {
            String token = jwtTokenProvider.resolveToken(request);
            Long userId = jwtTokenProvider.getUserId(token);
            log.info("success**获取收支趋势数据");
            return ResponseEntity.ok(transactionService.getIncomeExpenseTrend(userId, period));
        } catch (Exception e) {
            log.error("获取收支趋势数据失败", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * // 示例代码：使用 ECharts 绘制收支趋势图
     * const chart = echarts.init(document.getElementById('trendChart'));
     * const option = {
     *     title: { text: '收支趋势' },
     *     tooltip: { trigger: 'axis' },
     *     xAxis: { type: 'category', data: ['Jan', 'Feb', 'Mar', ...] },
     *     yAxis: { type: 'value' },
     *     series: [
     *         { name: '收入', type: 'line', data: [1200, 1500, 1800, ...] },
     *         { name: '支出', type: 'line', data: [800, 900, 1000, ...] }
     *     ]
     * };
     * chart.setOption(option);
     */

    @ApiOperation("获取收入来源数据")
    @GetMapping("/income-source")
    public ResponseEntity<?> getIncomeSourceData() {
        try {
            String token = jwtTokenProvider.resolveToken(request);
            Long userId = jwtTokenProvider.getUserId(token);
            log.info("success**获取收入来源数据");
            return ResponseEntity.ok(transactionService.getIncomeSourceData(userId));
        } catch (Exception e) {
            log.error("获取收入来源数据失败", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @ApiOperation("获取同比/环比数据")
    @GetMapping("/comparison")
    public ResponseEntity<?> getComparisonData(
            @RequestParam(required = false) String type) {
        try {
            String token = jwtTokenProvider.resolveToken(request);
            Long userId = jwtTokenProvider.getUserId(token);
            log.info("success**获取同比/环比数据");
            return ResponseEntity.ok(transactionService.getComparisonData(userId, type));
        } catch (Exception e) {
            log.error("获取同比/环比数据失败", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}