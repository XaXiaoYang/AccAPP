package com.example.expense_tracker.controller;

import com.example.expense_tracker.common.util.JwtTokenProvider;
import com.example.expense_tracker.dto.TransactionDTO;
import com.example.expense_tracker.service.TransactionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @author xiaoyang
 * @create 2025-01-17-11:25
 * @description
 */

@Slf4j
@Api(tags = "交易记录")
@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;
    private final JwtTokenProvider jwtTokenProvider;
    private final HttpServletRequest request;

    @ApiOperation("添加交易记录")
    @PostMapping
    public ResponseEntity<?> addTransaction(@RequestBody TransactionDTO dto) {
        try {
            String token = jwtTokenProvider.resolveToken(request);
            Long userId = jwtTokenProvider.getUserId(token);
            return ResponseEntity.ok(transactionService.addTransaction(userId, dto));
        } catch (Exception e) {
            log.error("添加交易记录失败", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @ApiOperation("获取交易记录列表")
    @GetMapping
    public ResponseEntity<?> getTransactions() {
        try {
            String token = jwtTokenProvider.resolveToken(request);
            Long userId = jwtTokenProvider.getUserId(token);
            return ResponseEntity.ok(transactionService.getUserTransactions(userId));
        } catch (Exception e) {
            log.error("获取交易记录失败", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
