package com.karrecy.admin.controller.order;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.karrecy.common.core.controller.BaseController;
import com.karrecy.common.core.domain.PageQuery;
import com.karrecy.common.core.domain.R;
import com.karrecy.common.core.page.TableDataInfo;
import com.karrecy.order.domain.dto.OrderAppealDTO;
import com.karrecy.order.domain.po.OrderAppeal;
import com.karrecy.order.domain.vo.OrderAppealVO;
import com.karrecy.order.service.IOrderAppealService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 订单申诉控制器
 * 处理应用程序的订单申诉相关操作
 * @author Tao
 */
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/order/appeal")
public class OrderAppealController extends BaseController {

    private final IOrderAppealService orderAppealService;

    /**
     * 申诉分页查询
     * @param orderAppeal 包含查询参数的订单申诉对象
     * @param pageQuery 包含分页详细信息的分页查询对象
     * @return 分页后的订单申诉列表
     */
    @GetMapping("/list")
    @SaCheckPermission("order:appeal:list")
    public TableDataInfo<OrderAppeal> list(OrderAppeal orderAppeal, PageQuery pageQuery) {
        Page<OrderAppeal> page = orderAppealService.page(
                pageQuery.build(),
                new LambdaQueryWrapper<OrderAppeal>()
                        .eq(ObjectUtil.isNotNull(orderAppeal.getOrderId()), OrderAppeal::getOrderId, orderAppeal.getOrderId())
                        .eq(ObjectUtil.isNotNull(orderAppeal.getAppealStatus()), OrderAppeal::getAppealStatus, orderAppeal.getAppealStatus())
                        .eq(ObjectUtil.isNotNull(orderAppeal.getSchoolId()), OrderAppeal::getSchoolId, orderAppeal.getSchoolId())
        );
        return TableDataInfo.build(page);
    }

    /**
     * 根据orderId查询申诉
     * @param orderId 要查询的订单ID
     * @return 包含订单申诉详细信息的订单申诉对象列表
     */
    @GetMapping("/{orderId}")
    @SaCheckPermission("order:appeal:get")
    public R<List<OrderAppealVO>> getAppeal(@PathVariable Long orderId) {
        List<OrderAppealVO> orderAppeals = orderAppealService.getListByOrderId(orderId);
        return R.ok(orderAppeals);
    }

    /**
     * 提交申诉
     * @param orderAppealDTO 包含申诉详细信息的订单申诉数据传输对象
     * @return 表示成功或失败的响应
     */
    @PostMapping
    @SaCheckPermission("order:appeal:submit")
    public R<Void> submitAppeal(@RequestBody OrderAppealDTO orderAppealDTO) {
        orderAppealService.submit(orderAppealDTO);
        return R.ok();
    }

    /**
     * 处理申诉
     * @param orderAppeal 包含处理后申诉详细信息的订单申诉对象
     * @return 表示成功或失败的响应
     */
    @PutMapping("/edit")
    @SaCheckPermission("order:appeal:edit")
    public R<Void> edit(@RequestBody OrderAppeal orderAppeal) {
        orderAppealService.handle(orderAppeal);
        return R.ok();
    }
}