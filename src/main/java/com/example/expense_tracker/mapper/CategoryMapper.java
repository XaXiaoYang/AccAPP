package com.example.expense_tracker.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.expense_tracker.entity.Category;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
}