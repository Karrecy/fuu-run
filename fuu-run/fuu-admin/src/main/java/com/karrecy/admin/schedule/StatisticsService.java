package com.karrecy.admin.schedule;

import com.karrecy.common.constant.CacheNames;
import com.karrecy.common.utils.redis.RedisUtils;
import com.karrecy.order.service.IOrderAppealService;
import com.karrecy.order.service.IOrderMainService;
import com.karrecy.order.service.IOrderPaymentService;
import com.karrecy.payment.service.ICapitalFlowService;
import com.karrecy.common.core.domain.entity.StatisticsDaily;
import com.karrecy.system.mapper.StatisticsDailyMapper;
import com.karrecy.system.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

/**
 * 自动统计数据
 */
@Component
@RequiredArgsConstructor
public class StatisticsService {

    private final IUserService userService;
    private final IOrderMainService orderMainService;

    private final StatisticsDailyMapper statisticsDailyMapper;

    @Scheduled(cron = "0 0 1 * * ?") // 每天凌晨1点执行
    public void generateDailyStatistics() {
        LocalDate today = LocalDate.now();
        today = today.minusDays(1);
        LocalDate lastDay = today.minusDays(2);
        StatisticsDaily statistics = new StatisticsDaily();
        statistics.setCreateTime(lastDay);

        // 统计订单数据
        orderMainService.calculateOrderStatistics(statistics,lastDay,today);

        // 统计金额数据
        orderMainService.calculateFinancialStatistics(statistics,lastDay,today);

        // 统计访问数据
        calculateVisitStatistics(statistics,lastDay);

        // 统计用户数据
        userService.calculateUserStatistics(statistics,lastDay,today);

        // 保存统计结果
        statisticsDailyMapper.insert(statistics);
    }

    private void calculateVisitStatistics(StatisticsDaily statistics, LocalDate lastDay) {
        long auCountRunner = RedisUtils.getAUCount(lastDay.toString(),CacheNames.DAILY_AU_RUNNER);
        long auCountUser = RedisUtils.getAUCount(lastDay.toString(),CacheNames.DAILY_AU_USER);

        long uvCount = RedisUtils.getUVCount(lastDay.toString());
        long totalVisits = RedisUtils.getAtomicValue(CacheNames.DAILY_TOTAL_VISITS);

        statistics.setActiveRunners((int) auCountRunner);
        statistics.setActiveUsers((int) auCountUser);
        statistics.setTotalVisits((int) totalVisits);
        statistics.setUniqueVisitors((int) uvCount);
    }

}