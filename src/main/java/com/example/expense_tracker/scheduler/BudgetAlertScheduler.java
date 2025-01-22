package com.example.expense_tracker.scheduler;

import com.example.expense_tracker.dto.BudgetExecutionDTO;
import com.example.expense_tracker.entity.User;
import com.example.expense_tracker.service.BudgetService;
import com.example.expense_tracker.service.NotificationService;
import com.example.expense_tracker.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 定时任务实现
 * @author xiaoyang
 * @create 2025-01-22-14:44
 * @description
 */
@Api(tags = "预算提醒定时任务")
@Component
@RestController
@RequestMapping("/api/scheduler")
public class BudgetAlertScheduler {

    private final BudgetService budgetService;
    private final NotificationService notificationService;
    private final UserService userService;

    public BudgetAlertScheduler(BudgetService budgetService, NotificationService notificationService, UserService userService) {
        this.budgetService = budgetService;
        this.notificationService = notificationService;
        this.userService = userService;
    }

//    预算使用率预警
    @ApiOperation("预算使用率预警")
    @GetMapping("/checkBudgetUsage")
    @Scheduled(cron = "0 0 9 * * ?") // 每天早上9点执行
    public void checkBudgetUsage() {
        List<User> users = userService.getAllUsers();
        for (User user : users) {
            List<BudgetExecutionDTO> executions = budgetService.getBudgetExecution(user.getId(), LocalDate.now().getYear(), LocalDate.now().getMonthValue());
            for (BudgetExecutionDTO execution : executions) {
                if (execution.getUsageRate().compareTo(new BigDecimal("80")) >= 0) {
                    notificationService.sendBudgetAlert(user, execution);
                }
            }
        }
    }

//  定期预算执行情况推送
    @ApiOperation("定期预算执行情况推送")
    @GetMapping("/sendMonthlyBudgetReport")
    @Scheduled(cron = "0 0 10 1 * ?") // 每月1号上午10点执行
    public void sendMonthlyBudgetReport() {
        List<User> users = userService.getAllUsers();
        for (User user : users) {
            List<BudgetExecutionDTO> executions = budgetService.getBudgetExecution(user.getId(), LocalDate.now().getYear(), LocalDate.now().getMonthValue() - 1);
            notificationService.sendMonthlyReport(user, executions);
        }
    }

//   超预算预警通知
    @ApiOperation("超预算预警通知")
    @GetMapping("/checkOverBudget")
    @Scheduled(cron = "0 0 9 * * ?") // 每天早上9点执行
    public void checkOverBudget() {
        List<User> users = userService.getAllUsers();
        for (User user : users) {
            List<BudgetExecutionDTO> executions = budgetService.getBudgetExecution(user.getId(), LocalDate.now().getYear(), LocalDate.now().getMonthValue());
            for (BudgetExecutionDTO execution : executions) {
                if (execution.getIsOverBudget()) {
                    notificationService.sendOverBudgetAlert(user, execution);
                }
            }
        }
    }
}
