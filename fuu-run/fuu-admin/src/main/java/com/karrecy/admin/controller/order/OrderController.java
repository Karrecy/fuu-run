package com.karrecy.admin.controller.order;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.karrecy.common.core.controller.BaseController;
import com.karrecy.common.core.domain.PageQuery;
import com.karrecy.common.core.domain.R;
import com.karrecy.common.core.page.TableDataInfo;
import com.karrecy.order.domain.dto.OrderCancelDTO;
import com.karrecy.order.domain.dto.OrderCompleteDTO;
import com.karrecy.order.domain.dto.OrderQuery;
import com.karrecy.order.domain.dto.OrderSubmitDTO;
import com.karrecy.order.domain.po.OrderMain;
import com.karrecy.order.domain.vo.OrderDetailVO;
import com.karrecy.order.service.IOrderMainService;
import com.karrecy.payment.domain.vo.PayedVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * 订单控制器
 * 处理应用程序的订单相关操作
 * @author Tao
 */
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/order/order")
public class OrderController extends BaseController {

    private final IOrderMainService orderMainService;

    /**
     * 新建订单
     * @param orderSubmitDTO 包含订单详细信息的订单提交数据传输对象
     * @return 表示成功或失败的响应
     */
    @PostMapping("/add")
    @SaCheckPermission("order:order:add")
    public R<PayedVO> add(@Validated @RequestBody OrderSubmitDTO orderSubmitDTO) {
        log.info(orderSubmitDTO.toString());
        PayedVO result = orderMainService.submit(orderSubmitDTO);
        return R.ok(result);
    }

    /**
     * 继续支付
     * @param orderId 订单ID
     * @return 表示成功或失败的响应
     */
    @GetMapping("/payAgain/{orderId}")
    @SaCheckPermission("order:order:payAgain")
    public R<PayedVO> payAgain(@PathVariable Long orderId) {
        PayedVO result = orderMainService.payAgain(orderId);
        return R.ok(result);
    }

    /**
     * 订单退款
     * @param orderId 订单ID
     * @param amount 退款金额
     * @return 表示成功或失败的响应
     */
    @GetMapping("/refund")
    @SaCheckPermission("order:order:refund")
    public R<Void> refund(Long orderId, BigDecimal amount) {
        orderMainService.refund(orderId, amount);
        return R.ok();
    }

    /**
     * 取消订单
     * @param orderCancelDTO 包含取消订单详细信息的订单取消数据传输对象
     * @return 表示成功或失败的响应
     */
    @PostMapping("/cancel")
    @SaCheckPermission("order:order:cancel")
    public R<Void> cancel(@Validated @RequestBody OrderCancelDTO orderCancelDTO) {
        orderMainService.cancel(orderCancelDTO);
        return R.ok();
    }

    /**
     * 取消订单前置操作
     * @param orderId 订单ID
     * @return 表示成功或失败的响应
     */
    @GetMapping("/cancelbefore/{orderId}")
    @SaCheckPermission("order:order:cancelbefore")
    public R<Object> cancelbefore(@PathVariable Long orderId) {
        return R.ok((Object) orderMainService.cancelbefore(orderId));
    }

    /**
     * 大厅订单查询
     * @param orderQuery 包含查询参数的订单查询对象
     * @param pageQuery 包含分页详细信息的分页查询对象
     * @return 分页后的订单列表
     */
    @GetMapping("/list/hall")
    @SaCheckPermission("order:order:list:hall")
    public TableDataInfo<OrderMain> list(OrderQuery orderQuery, PageQuery pageQuery) {
        return orderMainService.pageHall(orderQuery, pageQuery);
    }

    /**
     * 我的订单查询
     * @param orderQuery 包含查询参数的订单查询对象
     * @param pageQuery 包含分页详细信息的分页查询对象
     * @return 分页后的订单列表
     */
    @GetMapping("/list/user")
    @SaCheckPermission("order:order:list:user")
    public TableDataInfo<OrderMain> listUser(OrderQuery orderQuery, PageQuery pageQuery) {
        return orderMainService.pageUser(orderQuery, pageQuery);
    }

    /**
     * 订单详情查询
     * @param orderId 订单ID
     * @return 包含订单详细信息的订单详情对象
     */
    @GetMapping("/detail/{orderId}")
    @SaCheckPermission("order:order:detail")
    public R<OrderDetailVO> detail(@PathVariable Long orderId) {
        return R.ok(orderMainService.getDetail(orderId));
    }

    /**
     * 跑腿员接单
     * @param orderId 订单ID
     * @return 表示成功或失败的响应
     */
    @GetMapping("/accept/{orderId}")
    @SaCheckPermission("order:order:accept")
    public R<Void> accept(@PathVariable Long orderId) {
        orderMainService.accept(orderId);
        return R.ok();
    }

    /**
     * 跑腿员配送
     * @param orderId 订单ID
     * @return 表示成功或失败的响应
     */
    @GetMapping("/delivery/{orderId}")
    @SaCheckPermission("order:order:delivery")
    public R<Void> delivery(@PathVariable Long orderId) {
        orderMainService.delivery(orderId);
        return R.ok();
    }

    /**
     * 完成订单
     * @param orderCompleteDTO 包含完成订单详细信息的订单完成数据传输对象
     * @return 表示成功或失败的响应
     */
    @PostMapping("/complete")
    @SaCheckPermission("order:order:complete")
    public R<Void> complete(@Validated @RequestBody OrderCompleteDTO orderCompleteDTO) {
        orderMainService.complete(orderCompleteDTO);
        return R.ok();
    }

    /**
     * 补充凭证
     * @param orderCompleteDTO 包含补充凭证详细信息的订单完成数据传输对象
     * @return 表示成功或失败的响应
     */
    @PostMapping("/updateImages")
    @SaCheckPermission("order:order:updateImages")
    public R<Void> suppleImages(@Validated @RequestBody OrderCompleteDTO orderCompleteDTO) {
        orderMainService.updateImages(orderCompleteDTO);
        return R.ok();
    }

    /**
     * 确定送达
     * @param orderId 订单ID
     * @return 表示成功或失败的响应
     */
    @GetMapping("/confirm/{orderId}")
    @SaCheckPermission("order:order:confirm")
    public R<Void> confirm(@PathVariable Long orderId) {
        orderMainService.confirm(orderId);
        return R.ok();
    }

    /**
     * 获取用户/跑腿员电话
     * @param orderId 订单ID
     * @return 包含用户/跑腿员电话的响应
     */
    @GetMapping("/phone/{orderId}")
    @SaCheckPermission("order:order:phone")
    public R<Map<String, String>> phone(@PathVariable Long orderId) {
        String phone = orderMainService.phone(orderId);
        Map<String, String> ajax = new HashMap<>();
        ajax.put("phone", phone);
        return R.ok(ajax);
    }
}