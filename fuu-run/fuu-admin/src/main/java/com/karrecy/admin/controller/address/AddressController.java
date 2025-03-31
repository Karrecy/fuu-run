package com.karrecy.admin.controller.address;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.karrecy.common.config.FuuConfig;
import com.karrecy.common.core.controller.BaseController;
import com.karrecy.common.core.domain.PageQuery;
import com.karrecy.common.core.domain.R;
import com.karrecy.common.core.page.TableDataInfo;
import com.karrecy.common.exception.ServiceException;
import com.karrecy.order.domain.po.Address;
import com.karrecy.order.service.IAddressService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * 地址控制器
 * 处理应用程序的地址相关操作
 * @author Tao
 */
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/address/address")
public class AddressController extends BaseController {

    private final IAddressService addressService;
    private final FuuConfig fuuConfig;

    /**
     * 新增用户地址
     * @param address 包含地址详细信息的地址对象
     * @return 表示成功或失败的响应
     */
    @SaCheckPermission("address:address:add")
    @PostMapping("/add")
    public R<Void> add(@Validated @RequestBody Address address) {
        address.setUid(getUserId());
        address.setCreateTime(LocalDateTime.now());
        address.setUpdateTime(LocalDateTime.now());
        address.setCreateId(getUserId());
        address.setUpdateId(getUserId());
        if (ObjectUtil.equals(address.getIsDefault(), 1)) {
            LambdaUpdateWrapper<Address> luw = new LambdaUpdateWrapper<Address>()
                    .eq(Address::getUid, getUserId())
                    .eq(Address::getIsDefault, 1)
                    .set(Address::getIsDefault, 0);
            addressService.update(luw);
        }
        List<Address> list = addressService.list(new LambdaQueryWrapper<Address>().eq(Address::getUid, address.getUid()));
        if (list.size() >= fuuConfig.getMaxAddress()) {
            throw new ServiceException("地址数量已达上限");
        }
        return toAjax(addressService.saveOrUpdate(address));
    }

    /**
     * 更新用户地址
     * @param address 包含更新后地址详细信息的地址对象
     * @return 表示成功或失败的响应
     */
    @PutMapping("/edit")
    @SaCheckPermission("address:address:edit")
    public R<Void> edit(@Validated @RequestBody Address address) {
        if (ObjectUtil.equals(address.getIsDefault(), 1)) {
            LambdaUpdateWrapper<Address> luw = new LambdaUpdateWrapper<Address>()
                    .eq(Address::getUid, getUserId())
                    .eq(Address::getIsDefault, 1)
                    .set(Address::getIsDefault, 0);
            addressService.update(luw);
        }
        return toAjax(addressService.updateById(address));
    }

    /**
     * 分页查询地址
     * @param address 包含查询参数的地址对象
     * @param pageQuery 包含分页详细信息的分页查询对象
     * @return 分页后的地址列表
     */
    @GetMapping("/list")
    @SaCheckPermission("address:address:list")
    public TableDataInfo<Address> list(Address address, PageQuery pageQuery) {
        return addressService.selectPageAddressList(address, pageQuery);
    }

    /**
     * 获取当前用户的地址列表
     * @param pageQuery 包含分页详细信息的分页查询对象
     * @return 当前用户的地址列表
     */
    @GetMapping("/list/curr")
    @SaCheckPermission("address:address:list:curr")
    public R<List<Address>> listCurr(PageQuery pageQuery) {
        return R.ok(addressService.selectListByUser());
    }

    /**
     * 根据地址ID删除地址
     * @param addressIds 要删除的地址ID数组
     * @return 表示成功或失败的响应
     */
    @DeleteMapping("/{addressIds}")
    @SaCheckPermission("address:address:delete")
    public R<Void> remove(@PathVariable Long[] addressIds) {
        List<Long> ids = Arrays.asList(addressIds);
        return toAjax(addressService.removeBatchByIds(ids));
    }

    /**
     * 根据地址ID删除当前用户的地址
     * @param addressId 要删除的地址ID
     * @return 表示成功或失败的响应
     */
    @DeleteMapping("/curr/{addressId}")
    @SaCheckPermission("address:address:curr:delete")
    public R<Void> removeById(@PathVariable Long addressId) {
        LambdaUpdateWrapper<Address> lmq = new LambdaUpdateWrapper<Address>()
                .eq(Address::getId, addressId)
                .eq(Address::getUid, getUserId());
        return toAjax(addressService.remove(lmq));
    }

    /**
     * 根据地址ID获取当前用户的地址
     * @param addressId 要获取的地址ID
     * @return 包含地址详细信息的地址对象
     */
    @GetMapping("/curr/{addressId}")
    @SaCheckPermission("address:address:curr:get")
    public R<Address> getById(@PathVariable Long addressId) {
        LambdaUpdateWrapper<Address> lmq = new LambdaUpdateWrapper<Address>()
                .eq(Address::getId, addressId)
                .eq(Address::getUid, getUserId());
        Address one = addressService.getOne(lmq);
        return R.ok(one);
    }
}