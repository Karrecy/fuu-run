package com.karrecy.order.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.karrecy.common.core.domain.PageQuery;
import com.karrecy.common.core.page.TableDataInfo;
import com.karrecy.common.helper.LoginHelper;
import com.karrecy.order.domain.po.Address;
import com.karrecy.order.mapper.AddressMapper;
import com.karrecy.order.service.IAddressService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 用户地址表 服务实现类
 * </p>
 */
@Service
@RequiredArgsConstructor
public class AddressServiceImpl extends ServiceImpl<AddressMapper, Address> implements IAddressService {

    private final AddressMapper addressMapper;

    /**
     * 地址分页查询
     * @param address
     * @param pageQuery
     * @return
     */
    @Override
    public TableDataInfo<Address> selectPageAddressList(Address address, PageQuery pageQuery) {
        LambdaQueryWrapper<Address> lqm = new LambdaQueryWrapper<Address>()
                .eq(ObjectUtil.isNotNull(address.getId()), Address::getId, address.getId())
                .like(ObjectUtil.isNotNull(address.getName()), Address::getName, address.getName())
                .eq(ObjectUtil.isNotNull(address.getUid()), Address::getUid, address.getUid());
        Page<Address> addressPage = addressMapper.selectPage(pageQuery.build(), lqm);
        return TableDataInfo.build(addressPage);
    }

    /**
     * 当前用户地址列表
     * @return
     */
    @Override
    public List<Address> selectListByUser() {
        LambdaQueryWrapper<Address> lqm = new LambdaQueryWrapper<Address>()
                .eq(Address::getUid, LoginHelper.getUserId())
                .orderByDesc(Address::getIsDefault);
//        Page<Address> addressPage = addressMapper.selectPage(pageQuery.build(), lqm);
        return addressMapper.selectList(lqm);
    }
}
