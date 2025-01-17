package com.example.expense_tracker.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.expense_tracker.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author xiaoyang
 * @create 2025-01-17-09:58
 * @description
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
