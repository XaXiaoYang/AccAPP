package com.example.expense_tracker.service;

import com.example.expense_tracker.dto.LoginDTO;
import com.example.expense_tracker.dto.UserDTO;
import com.example.expense_tracker.entity.User;

import java.util.List;

/**
 * @author xiaoyang
 * @create 2025-01-17-09:58
 * @description
 */
public interface UserService {
    User register(UserDTO userDTO);
    String login(LoginDTO loginDTO);
    List<User> getAllUsers();
    void updateCurrencyUnit(Long userId, String currencyUnit);
}
