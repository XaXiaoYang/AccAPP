package com.example.expense_tracker.service;

import com.example.expense_tracker.dto.CategoryDTO;
import com.example.expense_tracker.entity.Category;

import java.util.List;

/**
 * @author xiaoyang
 * @create 2025-01-17-14:28
 * @description
 */

public interface CategoryService {
    // 获取用户的所有分类（包括系统预设和用户自定义）
    List<Category> getUserCategories(Long userId);

    // 获取指定类型的分类
    List<Category> getCategoriesByType(Long userId, String type);

    // 添加自定义分类
    Category addCategory(Long userId, CategoryDTO categoryDTO);

    // 更新自定义分类
    Category updateCategory(Long userId, Long categoryId, CategoryDTO categoryDTO);

    // 删除自定义分类
    void deleteCategory(Long userId, Long categoryId);
}
