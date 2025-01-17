package com.example.expense_tracker.controller;

import com.example.expense_tracker.service.ExportService;
import com.example.expense_tracker.common.util.JwtTokenProvider;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/export")
@RequiredArgsConstructor
@Api(tags = "数据导出接口")
@Slf4j
public class ExportController {

    private final ExportService exportService;
    private final JwtTokenProvider jwtTokenProvider;
    private final HttpServletRequest request;

    private Long getCurrentUserId() {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        String token = authHeader.substring(7);
        return jwtTokenProvider.getUserIdFromToken(token);
    }

    @GetMapping("/transactions")
    @ApiOperation("导出交易记录")
    public ResponseEntity<Resource> exportTransactions(
            @ApiParam(value = "年份", example = "2024") @RequestParam(required = false) Integer year,
            @ApiParam(value = "月份", example = "3") @RequestParam(required = false) Integer month) {
        try {
            // 获取当前用户ID
            Long userId = getCurrentUserId();
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            // 如果未指定年月，使用当前年月
            if (year == null || month == null) {
                LocalDate now = LocalDate.now();
                year = now.getYear();
                month = now.getMonthValue();
            }

            // 生成文件名
            String filename = String.format("交易记录_%d年%d月.xlsx", year, month);
            String encodedFilename = URLEncoder.encode(filename, StandardCharsets.UTF_8.toString());

            // 导出数据
            Resource resource = exportService.exportTransactions(userId, year, month);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedFilename)
                    .body(resource);
        } catch (Exception e) {
            log.error("导出交易记录失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/budget-execution")
    @ApiOperation("导出预算执行情况")
    public ResponseEntity<Resource> exportBudgetExecution(
            @ApiParam(value = "年份", example = "2024") @RequestParam(required = false) Integer year,
            @ApiParam(value = "月份", example = "3") @RequestParam(required = false) Integer month) {
        try {
            // 获取当前用户ID
            Long userId = getCurrentUserId();
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            // 如果未指定年月，使用当前年月
            if (year == null || month == null) {
                LocalDate now = LocalDate.now();
                year = now.getYear();
                month = now.getMonthValue();
            }

            // 生成文件名
            String filename = String.format("预算执行情况_%d年%d月.xlsx", year, month);
            String encodedFilename = URLEncoder.encode(filename, StandardCharsets.UTF_8.toString());

            // 导出数据
            Resource resource = exportService.exportBudgetExecution(userId, year, month);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedFilename)
                    .body(resource);
        } catch (NumberFormatException e) {
            log.error("用户ID格式错误", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            log.error("导出预算执行情况失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/annual-report")
    @ApiOperation("导出年度报表")
    public ResponseEntity<Resource> exportAnnualReport(
            @ApiParam(value = "年份", example = "2024") @RequestParam(required = false) Integer year) {
        try {
            // 获取当前用户ID
            Long userId = getCurrentUserId();
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            // 如果未指定年份，使用当前年份
            if (year == null) {
                year = LocalDate.now().getYear();
            }

            // 生成文件名
            String filename = String.format("年度报表_%d年.xlsx", year);
            String encodedFilename = URLEncoder.encode(filename, StandardCharsets.UTF_8.toString());

            // 导出数据
            Resource resource = exportService.exportAnnualReport(userId, year);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedFilename)
                    .body(resource);
        } catch (NumberFormatException e) {
            log.error("用户ID格式错误", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            log.error("导出年度报表失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}