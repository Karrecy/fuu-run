package com.karrecy.payment.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.karrecy.common.core.domain.entity.User;
import com.karrecy.common.enums.CapitalType;
import com.karrecy.common.enums.DeviceType;
import com.karrecy.common.enums.Status;
import com.karrecy.common.enums.UserType;
import com.karrecy.common.exception.ServiceException;
import com.karrecy.common.helper.LoginHelper;
import com.karrecy.common.utils.BigDecimalUtils;
import com.karrecy.payment.domain.po.CapitalFlow;
import com.karrecy.payment.domain.po.MoneyRecode;
import com.karrecy.payment.mapper.CapitalFlowMapper;
import com.karrecy.payment.mapper.MoneyRecodeMapper;
import com.karrecy.payment.service.IMoneyRecodeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.karrecy.system.domain.po.Wallet;
import com.karrecy.system.mapper.UserMapper;
import com.karrecy.system.mapper.WalletMapper;
import com.karrecy.system.service.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * <p>
 * 提现审核表 服务实现类
 * </p>
 */
@Service
@RequiredArgsConstructor
public class MoneyRecodeServiceImpl extends ServiceImpl<MoneyRecodeMapper, MoneyRecode> implements IMoneyRecodeService {


    private final MoneyRecodeMapper moneyRecodeMapper;
    private final WalletMapper walletMapper;
    private final CapitalFlowMapper capitalFlowMapper;
    private final IUserService userService;

    /**
     * 提交提现
     * @param moneyRecode
     */
    @Override
    public void submit(MoneyRecode moneyRecode) {
        Long uid = LoginHelper.getUserId();
        User user = userService.selectUserByUid(uid);
        Wallet wallet = walletMapper.selectById(uid);

        if (wallet.getBalance().compareTo(moneyRecode.getCash()) < 0) {
            throw new ServiceException("余额不足");
        }
        moneyRecode.setUid(uid);
        moneyRecode.setType(user.getUserType());
        moneyRecode.setCreateTime(LocalDateTime.now());
        moneyRecode.setStatus(Status.PENDING.getCode());
        moneyRecode.setUpdateId(uid);
        moneyRecode.setUpdateTime(LocalDateTime.now());
        moneyRecodeMapper.insert(moneyRecode);
    }

    /**
     * 处理提现
     * @param moneyRecode
     */
    @Override
    public void  handle(MoneyRecode moneyRecode) {
        Long uid = LoginHelper.getUserId();
        //是否有正处理的申请
        MoneyRecode lastCode = lastRecodeByUid(uid);
        if (ObjectUtil.isNotNull(lastCode) && ObjectUtil.equals(lastCode.getStatus(), Status.PENDING.getCode())) {
            throw new ServiceException("有正在处理的提现申请");
        }
        MoneyRecode moneyRecodeDB = moneyRecodeMapper.selectById(moneyRecode.getId());
        User user = userService.selectUserByUid(moneyRecodeDB.getUid());
        moneyRecodeDB.setStatus(moneyRecode.getStatus());
        moneyRecodeDB.setUpdateId(uid);
        moneyRecodeDB.setUpdateTime(LocalDateTime.now());
        if (moneyRecode.getStatus().equals(Status.DISABLE.getCode())) {
            moneyRecodeDB.setFeedback(moneyRecode.getFeedback());
            moneyRecodeMapper.updateById(moneyRecodeDB);
            return;
        }
        if (moneyRecodeDB.getStatus().equals(Status.OK.getCode())) {
            //更新资金流动表
            CapitalFlow capitalFlow = new CapitalFlow();
            capitalFlow.setCreateTime(LocalDateTime.now());
            capitalFlow.setProfitPlat(moneyRecodeDB.getCash().negate());
            //跑腿提现
            if (user.getUserType().equals(UserType.RUNNER_USER.getCode())) {
                capitalFlow.setType(CapitalType.RECODE_RUNNER.getCode());
                capitalFlow.setRunnerId(moneyRecodeDB.getUid());
                capitalFlow.setProfitRunner(moneyRecodeDB.getCash());
            }
            //代理提现
            if (user.getUserType().equals(UserType.SCHOOL_AGENT.getCode())) {
                capitalFlow.setType(CapitalType.RECODE_AGENT.getCode());
                capitalFlow.setAgentId(moneyRecodeDB.getUid());
                capitalFlow.setProfitAgent(moneyRecodeDB.getCash());
            }
            capitalFlowMapper.insert(capitalFlow);
            //更新钱包
            Wallet walletDB = walletMapper.selectById(moneyRecodeDB.getUid());
            walletDB.setBalance(walletDB.getBalance().subtract(moneyRecodeDB.getCash()));
            walletDB.setWithdrawn(walletDB.getWithdrawn().add(moneyRecodeDB.getCash()));
            walletDB.setUpdateTime(LocalDateTime.now());
            walletMapper.updateById(walletDB);
            //更新提现记录
            moneyRecodeMapper.updateById(moneyRecodeDB);

        }
    }

    /**
     * 最后一次提现
     * @param userId
     * @return
     */
    @Override
    public MoneyRecode lastRecodeByUid(Long userId) {
        return moneyRecodeMapper.selectOne(new LambdaQueryWrapper<MoneyRecode>()
                .orderByDesc(MoneyRecode::getCreateTime).last("limit 1")
                .eq(MoneyRecode::getUid, userId));
    }
}
