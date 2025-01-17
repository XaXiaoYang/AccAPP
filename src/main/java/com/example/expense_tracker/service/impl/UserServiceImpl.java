package com.example.expense_tracker.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.expense_tracker.common.util.JwtTokenProvider;
import com.example.expense_tracker.dto.LoginDTO;
import com.example.expense_tracker.dto.UserDTO;
import com.example.expense_tracker.entity.User;
import com.example.expense_tracker.mapper.UserMapper;
import com.example.expense_tracker.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author xiaoyang
 * @create 2025-01-17-09:59
 * @description
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    @Transactional
    public User register(UserDTO userDTO) {
        // 检查用户名是否已存在
        if (userMapper.selectCount(new QueryWrapper<User>()
                .eq("username", userDTO.getUsername())) > 0) {
            throw new RuntimeException("用户名已存在");
        }

        // 创建新用户
        User user = new User();
        user.setUsername(userDTO.getUsername());
        // 使用 passwordEncoder 加密密码
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setEmail(userDTO.getEmail());

        // 保存用户
        userMapper.insert(user);
        return user;
    }

    @Override
    public String login(LoginDTO loginDTO) {
        // 查找用户
        User user = userMapper.selectOne(new QueryWrapper<User>()
                .eq("username", loginDTO.getUsername()));

        if (user == null || !passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }

        return jwtTokenProvider.createToken(user.getUsername(), user.getId());

    }
}