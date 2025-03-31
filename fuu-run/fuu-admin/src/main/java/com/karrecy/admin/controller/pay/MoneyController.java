package com.karrecy.admin.controller.pay;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.karrecy.common.core.controller.BaseController;
import com.karrecy.common.core.domain.PageQuery;
import com.karrecy.common.core.domain.R;
import com.karrecy.common.core.page.TableDataInfo;
import com.karrecy.common.core.validate.AddGroup;
import com.karrecy.common.core.validate.EditGroup;
import com.karrecy.common.helper.LoginHelper;
import com.karrecy.payment.domain.po.CapitalFlow;
import com.karrecy.payment.domain.po.MoneyRecode;
import com.karrecy.payment.service.ICapitalFlowService;
import com.karrecy.payment.service.IMoneyRecodeService;
import com.karrecy.system.domain.po.Wallet;
import com.karrecy.system.service.IWalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/payment")
public class MoneyController extends BaseController{

    private final ICapitalFlowService capitalFlowService;
    private final IMoneyRecodeService moneyRecodeService;
    private final IWalletService walletService;

    /**
     * 提现申请查询
     */
    @GetMapping("/withdraw/page")
    @SaCheckPermission("payment:withdraw:page")
    public TableDataInfo<MoneyRecode> withdrawPage(MoneyRecode moneyRecode, PageQuery pageQuery) {
        Page<MoneyRecode> page = moneyRecodeService.page(
                pageQuery.build(),
                new LambdaQueryWrapper<MoneyRecode>()
                        .orderByDesc(MoneyRecode::getCreateTime)
                        .eq(ObjectUtil.isNotNull(moneyRecode.getUid()), MoneyRecode::getUid, moneyRecode.getUid())
                        .eq(ObjectUtil.isNotNull(moneyRecode.getPlatform()), MoneyRecode::getPlatform, moneyRecode.getPlatform())
                        .eq(ObjectUtil.isNotNull(moneyRecode.getStatus()), MoneyRecode::getStatus, moneyRecode.getStatus())
                        .eq(ObjectUtil.isNotNull(moneyRecode.getType()), MoneyRecode::getType, moneyRecode.getType())
                        .between(ObjectUtil.isNotNull(moneyRecode.getBeginTime()) && ObjectUtil.isNotNull(moneyRecode.getEndTime()),
                                MoneyRecode::getCreateTime, moneyRecode.getBeginTime(), moneyRecode.getEndTime())
        );
        return TableDataInfo.build(page);
    }

    /**
     * 当前用户钱包查询
     */
    @GetMapping("/wallet/curr")
    @SaCheckPermission("payment:wallet:curr")
    public R<Wallet> wallet() {
        Wallet wallet = walletService.getById(LoginHelper.getUserId());
        return R.ok(wallet);
    }

    /**
     * 用户钱包分页查询
     */
    @GetMapping("/wallet/page")
    @SaCheckPermission("payment:wallet:page")
    public TableDataInfo<Wallet> walletPage(Wallet wallet, PageQuery pageQuery) {
        Page<Wallet> page = walletService.page(
                pageQuery.build(),
                new LambdaQueryWrapper<Wallet>()
                        .orderByDesc(Wallet::getCreateTime)
                        .eq(ObjectUtil.isNotNull(wallet.getUid()), Wallet::getUid, wallet.getUid())
        );
        return TableDataInfo.build(page);
    }

    /**
     * 当前用户资金明细查询
     */
    @GetMapping("/capital/list")
    @SaCheckPermission("payment:flow:list")
    public TableDataInfo<CapitalFlow> listCurr(CapitalFlow capitalFlow, PageQuery pageQuery) {
        return capitalFlowService.listCurr(capitalFlow, pageQuery);
    }

    /**
     * 提交提现
     */
    @PostMapping("/recode")
    @SaCheckPermission("payment:recode")
    public R<Void> submitRecode(@Validated(AddGroup.class) @RequestBody MoneyRecode moneyRecode) {
        moneyRecodeService.submit(moneyRecode);
        return R.ok();
    }

    /**
     * 处理提现
     * @param moneyRecode
     * @return
     */
    @PutMapping("/recode/edit")
    @SaCheckPermission("payment:recode:edit")
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody MoneyRecode moneyRecode) {
        moneyRecodeService.handle(moneyRecode);
        return R.ok();
    }

    /**
     * 最后一次提现
     * @return
     */
    @GetMapping("/recode/last")
    @SaCheckPermission("payment:recode:last")
    public R<MoneyRecode> lastRecode() {
        MoneyRecode moneyRecode = moneyRecodeService.lastRecodeByUid(LoginHelper.getUserId());
        return R.ok(moneyRecode);
    }


}
