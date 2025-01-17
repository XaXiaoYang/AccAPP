package com.example.expense_tracker.common.util;

/**
 * @author xiaoyang
 * @create 2025-01-17-10:04
 * @description
 */
// 生成一个随机的32位密钥
import java.security.SecureRandom;
import java.util.Base64;

public class SecretKeyGenerator {
    public static void main(String[] args) {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        String secret = Base64.getEncoder().encodeToString(bytes);
        System.out.println("Generated secret key: " + secret);
    }
}