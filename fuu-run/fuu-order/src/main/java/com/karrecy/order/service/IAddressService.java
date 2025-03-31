package com.karrecy.order.service;

import com.karrecy.common.core.domain.PageQuery;
import com.karrecy.common.core.page.TableDataInfo;
import com.karrecy.order.domain.po.Address;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 用户地址表 服务类
 * </p>
 */
public interface IAddressService extends IService<Address> {

    /**
     * 地址分页查询
     * @param address
     * @param pageQuery
     * @return
     */
    TableDataInfo<Address> selectPageAddressList(Address address, PageQuery pageQuery);

    /**
     * 当前用户地址列表
     * @return
     */
    List<Address> selectListByUser();
}
