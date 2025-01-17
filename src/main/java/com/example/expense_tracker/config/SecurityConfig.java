
package com.example.expense_tracker.config;

import com.example.expense_tracker.common.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * 配置Spring Security的安全策略，包括认证管理、密码编码器、HTTP安全配置和静态资源放行。
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;

    /**
     * 配置认证管理器，使用自定义的UserDetailsService和BCrypt密码编码器进行用户认证。
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    /**
     * 提供认证管理器的Bean，用于其他组件注入。
     */
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    /**
     * 提供BCrypt密码编码器的Bean，用于加密和验证用户密码。
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 配置HTTP安全设置，包括禁用基本认证、CSRF保护，设置无状态会话管理，
     * 自定义未登录时的返回结果，配置权限规则，并添加JWT令牌过滤器。
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .httpBasic().disable()
                // 禁用 CSRF 保护，适用于基于 Token 的认证方式
                .csrf().disable()
                // 基于 Token 的认证方式，不需要创建 Session
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                // 设置未登录时的返回结果为 JSON 格式
                .exceptionHandling()
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setContentType("application/json;charset=UTF-8");
                    response.setStatus(401);
                    response.getWriter().write("{\"message\":\"未登录\"}");
                })
                .and()
                // 设置权限规则
                .authorizeRequests()
                // 测试接口无需认证
                .antMatchers("/api/test/**").permitAll()
                // 登录注册接口无需认证
                .antMatchers("/api/user/register", "/api/user/login").permitAll()
                // Swagger 相关接口放行
                .antMatchers("/swagger-ui.html").permitAll()
                .antMatchers("/swagger-resources/**").permitAll()
                .antMatchers("/v2/api-docs").permitAll()
                .antMatchers("/webjars/**").permitAll()
                // 其他所有请求需要身份认证
                .anyRequest().authenticated()
                .and()
                // 在 UsernamePasswordAuthenticationFilter 之前添加 JWT 令牌过滤器
                .addFilterBefore(new JwtTokenFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);
    }

    /**
     * 配置不需要经过安全过滤器的静态资源请求，确保这些资源可以直接访问。
     */
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
                // 放行 Swagger 相关静态资源
                .antMatchers("/swagger-ui.html")
                .antMatchers("/webjars/**")
                .antMatchers("/swagger-resources/**")
                .antMatchers("/v2/api-docs/**")
                // 放行 CSS、JS 和图片等静态资源
                .antMatchers("/css/**")
                .antMatchers("/js/**")
                .antMatchers("/images/**");
    }
}