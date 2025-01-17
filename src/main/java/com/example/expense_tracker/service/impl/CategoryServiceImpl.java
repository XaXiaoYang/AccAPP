package com.example.expense_tracker.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.expense_tracker.dto.CategoryDTO;
import com.example.expense_tracker.entity.Category;
import com.example.expense_tracker.entity.Transaction;
import com.example.expense_tracker.mapper.CategoryMapper;
import com.example.expense_tracker.mapper.TransactionMapper;
import com.example.expense_tracker.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @author xiaoyang
 * @create 2025-01-17-14:29
 * @description
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper categoryMapper;
    private final TransactionMapper transactionMapper;

    @Override
    public List<Category> getUserCategories(Long userId) {
        return categoryMapper.selectList(
                new QueryWrapper<Category>()
                        .and(wrapper -> wrapper
                                .isNull("user_id")  // 系统预设分类
                                .or()
                                .eq("user_id", userId)  // 用户自定义分类
                        )
                        .orderByAsc("type", "id")
        );
    }

    @Override
    public List<Category> getCategoriesByType(Long userId, String type) {
        return categoryMapper.selectList(
                new QueryWrapper<Category>()
                        .eq("type", type)
                        .and(wrapper -> wrapper
                                .isNull("user_id")
                                .or()
                                .eq("user_id", userId)
                        )
                        .orderByAsc("id")
        );
    }

    @Override
    @Transactional
    public Category addCategory(Long userId, CategoryDTO categoryDTO) {
        // 验证同类型下分类名称是否重复
        if (isCategoryNameDuplicate(userId, categoryDTO.getType(), categoryDTO.getName(), null)) {
            throw new RuntimeException("该分类名称已存在");
        }

        Category category = new Category();
        category.setName(categoryDTO.getName());
        category.setType(categoryDTO.getType());
        category.setUserId(userId);
        category.setCreatedAt(new Date());
        category.setUpdatedAt(new Date());

        categoryMapper.insert(category);
        return category;
    }

    @Override
    @Transactional
    public Category updateCategory(Long userId, Long categoryId, CategoryDTO categoryDTO) {
        Category category = categoryMapper.selectById(categoryId);

        // 验证分类是否存在且属于该用户
        if (category == null || !userId.equals(category.getUserId())) {
            throw new RuntimeException("分类不存在或无权修改");
        }

        // 验证是否为系统预设分类
        if (category.getUserId() == null) {
            throw new RuntimeException("系统预设分类不能修改");
        }

        // 验证同类型下分类名称是否重复
        if (isCategoryNameDuplicate(userId, categoryDTO.getType(), categoryDTO.getName(), categoryId)) {
            throw new RuntimeException("该分类名称已存在");
        }

        category.setName(categoryDTO.getName());
        category.setType(categoryDTO.getType());
        category.setUpdatedAt(new Date());

        categoryMapper.updateById(category);
        return category;
    }

    @Override
    @Transactional
    public void deleteCategory(Long userId, Long categoryId) {
        Category category = categoryMapper.selectById(categoryId);

        // 验证分类是否存在且属于该用户
        if (category == null || !userId.equals(category.getUserId())) {
            throw new RuntimeException("分类不存在或无权删除");
        }

        // 验证是否为系统预设分类
        if (category.getUserId() == null) {
            throw new RuntimeException("系统预设分类不能删除");
        }

        // 检查是否有关联的交易记录
        Integer count = transactionMapper.selectCount(
                new QueryWrapper<Transaction>()
                        .eq("category_id", categoryId)
        );

        if (count > 0) {
            throw new RuntimeException("该分类下有关联的交易记录，不能删除");
        }

        categoryMapper.deleteById(categoryId);
    }

    private boolean isCategoryNameDuplicate(Long userId, String type, String name, Long excludeId) {
        QueryWrapper<Category> queryWrapper = new QueryWrapper<Category>()
                .eq("type", type)
                .eq("name", name)
                .and(wrapper -> wrapper
                        .isNull("user_id")
                        .or()
                        .eq("user_id", userId)
                );

        if (excludeId != null) {
            queryWrapper.ne("id", excludeId);
        }

        return categoryMapper.selectCount(queryWrapper) > 0;
    }
}
