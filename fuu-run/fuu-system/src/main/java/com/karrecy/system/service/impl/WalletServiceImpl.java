package com.karrecy.system.service.impl;

import com.karrecy.system.domain.po.Wallet;
import com.karrecy.system.mapper.WalletMapper;
import com.karrecy.system.service.IWalletService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户账户表 服务实现类
 * </p>
 */
@Service
public class WalletServiceImpl extends ServiceImpl<WalletMapper, Wallet> implements IWalletService {

}
