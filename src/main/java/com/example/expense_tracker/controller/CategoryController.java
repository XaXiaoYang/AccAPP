package com.example.expense_tracker.controller;

import com.example.expense_tracker.common.util.JwtTokenProvider;
import com.example.expense_tracker.dto.CategoryDTO;
import com.example.expense_tracker.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
/**
 * @author xiaoyang
 * @create 2025-01-17-14:29
 * @description
 */

@Slf4j
@Api(tags = "分类管理")
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    private final JwtTokenProvider jwtTokenProvider;
    private final HttpServletRequest request;

    @ApiOperation("获取所有分类")
    @GetMapping
    public ResponseEntity<?> getAllCategories() {
        try {
            String token = jwtTokenProvider.resolveToken(request);
            Long userId = jwtTokenProvider.getUserId(token);
            return ResponseEntity.ok(categoryService.getUserCategories(userId));
        } catch (Exception e) {
            log.error("获取分类列表失败", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @ApiOperation("获取指定类型的分类")
    @GetMapping("/type/{type}")
    public ResponseEntity<?> getCategoriesByType(
            @ApiParam(value = "分类类型", allowableValues = "income,expense")
            @PathVariable String type) {
        try {
            String token = jwtTokenProvider.resolveToken(request);
            Long userId = jwtTokenProvider.getUserId(token);
            return ResponseEntity.ok(categoryService.getCategoriesByType(userId, type));
        } catch (Exception e) {
            log.error("获取分类列表失败", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @ApiOperation("添加自定义分类")
    @PostMapping
    public ResponseEntity<?> addCategory(@RequestBody CategoryDTO categoryDTO) {
        try {
            String token = jwtTokenProvider.resolveToken(request);
            Long userId = jwtTokenProvider.getUserId(token);
            return ResponseEntity.ok(categoryService.addCategory(userId, categoryDTO));
        } catch (Exception e) {
            log.error("添加分类失败", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @ApiOperation("更新自定义分类")
    @PutMapping("/{categoryId}")
    public ResponseEntity<?> updateCategory(
            @PathVariable Long categoryId,
            @RequestBody CategoryDTO categoryDTO) {
        try {
            String token = jwtTokenProvider.resolveToken(request);
            Long userId = jwtTokenProvider.getUserId(token);
            return ResponseEntity.ok(categoryService.updateCategory(userId, categoryId, categoryDTO));
        } catch (Exception e) {
            log.error("更新分类失败", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @ApiOperation("删除自定义分类")
    @DeleteMapping("/{categoryId}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long categoryId) {
        try {
            String token = jwtTokenProvider.resolveToken(request);
            Long userId = jwtTokenProvider.getUserId(token);
            categoryService.deleteCategory(userId, categoryId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("删除分类失败", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
