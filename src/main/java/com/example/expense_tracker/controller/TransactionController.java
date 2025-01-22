package com.example.expense_tracker.controller;

import com.example.expense_tracker.common.util.JwtTokenProvider;
import com.example.expense_tracker.dto.TransactionDTO;
import com.example.expense_tracker.entity.Transaction;
import com.example.expense_tracker.service.TransactionService;
import com.example.expense_tracker.service.impl.FileStorageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

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
    private final FileStorageService fileStorageService;
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

    @ApiOperation("批量导入交易记录")
    @PostMapping("/import")
    public ResponseEntity<?> importTransactions(@RequestParam("file") MultipartFile file) {
        try {
            String token = jwtTokenProvider.resolveToken(request);
            Long userId = jwtTokenProvider.getUserId(token);
            transactionService.importTransactions(userId,file);
            return ResponseEntity.ok("导入成功");
        } catch (Exception e) {
            log.error("导入交易记录失败", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @ApiOperation("标签添加")
    @PostMapping("/{transactionId}/tags")
    public ResponseEntity<?> addTag(@PathVariable Long transactionId, @RequestParam String tag) {
        transactionService.addTag(transactionId, tag);
        return ResponseEntity.ok("标签添加成功");
    }

    @ApiOperation("标签删除")
    @DeleteMapping("/{transactionId}/tags")
    public ResponseEntity<?> removeTag(@PathVariable Long transactionId, @RequestParam String tag) {
        transactionService.removeTag(transactionId, tag);
        return ResponseEntity.ok("标签删除成功");
    }

    @ApiOperation("通过标签查询交易")
    @GetMapping("/tags")
    public ResponseEntity<List<Transaction>> findTransactionsByTag(@RequestParam Long userId, @RequestParam String tag) {
        List<Transaction> transactions = transactionService.findTransactionsByTag(userId, tag);
        return ResponseEntity.ok(transactions);
    }

    @ApiOperation("账单图片附件上传")
    @PostMapping("/{transactionId}/upload")
    public ResponseEntity<?> uploadAttachment(@PathVariable Long transactionId, @RequestParam("file") MultipartFile file) {
        try {
            String attachmentUrl = fileStorageService.storeFile(file);
            transactionService.addAttachment(transactionId, attachmentUrl);
            return ResponseEntity.ok("附件上传成功");
        } catch (Exception e) {
            log.error("上传附件失败", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
