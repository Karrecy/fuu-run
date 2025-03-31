package com.karrecy.order.service;

import com.karrecy.common.core.domain.PageQuery;
import com.karrecy.common.core.domain.entity.StatisticsDaily;
import com.karrecy.common.core.page.TableDataInfo;
import com.karrecy.order.domain.dto.OrderCancelDTO;
import com.karrecy.order.domain.dto.OrderCompleteDTO;
import com.karrecy.order.domain.dto.OrderQuery;
import com.karrecy.order.domain.dto.OrderSubmitDTO;
import com.karrecy.order.domain.po.OrderChat;
import com.karrecy.order.domain.po.OrderMain;
import com.baomidou.mybatisplus.extension.service.IService;
import com.karrecy.order.domain.vo.OrderDetailVO;
import com.karrecy.payment.domain.vo.PayedVO;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * <p>
 *  服务类
 * </p>
 */
public interface IOrderMainService extends IService<OrderMain> {

    /**
     * 新建订单
     *
     * @param orderSubmitDTO
     * @return
     */
    PayedVO submit(OrderSubmitDTO orderSubmitDTO);

    /**
     * 大厅订单查询
     * @param orderQuery
     * @param pageQuery
     * @return
     */
    TableDataInfo<OrderMain> pageHall(OrderQuery orderQuery, PageQuery pageQuery);

    /**
     * 我的订单查询
     * @param orderQuery
     * @param pageQuery
     * @return
     */
    TableDataInfo<OrderMain> pageUser(OrderQuery orderQuery, PageQuery pageQuery);

    /**
     * 订单详情查询
     * @param orderId
     * @return
     */
    OrderDetailVO getDetail(Long orderId);

    /**
     * 继续支付
     * @param orderId
     * @return
     */
    PayedVO payAgain(Long orderId);

    /**
     * 取消订单
     * @param orderCancelDTO
     */
    void cancel(OrderCancelDTO orderCancelDTO);

    /**
     * 跑腿员接单
     * @param orderId
     */
    void accept(Long orderId);

    void delivery(Long orderId);

    void complete(OrderCompleteDTO orderCompleteDTO);

    /**
     * 补充凭证
     * @param orderCompleteDTO
     */
    void updateImages(OrderCompleteDTO orderCompleteDTO);

    /**
     * 确定送达
     * @param orderId
     */
    void confirm(Long orderId);

    /**
     *
     * @param orderId
     * @return
     */
    String phone(Long orderId);

    /**
     * 订单聊天记录分页查询
     * @param pageQuery
     * @param orderId
     * @return
     */
    TableDataInfo<OrderChat> pageChat(PageQuery pageQuery, Long orderId);

    /**
     * 取消订单前置操作
     * @param orderId
     * @return
     */
    String cancelbefore(Long orderId);

    /**
     * 订单退款
     * @param orderId
     * @param amount
     */
    void refund(Long orderId, BigDecimal amount);

    /**
     * 统计订单数据
     * @param statistics
     */
    void calculateOrderStatistics(StatisticsDaily statistics, LocalDate lastDay, LocalDate today);

    /**
     * 统计金额数据
     * @param statistics
     */
    void calculateFinancialStatistics(StatisticsDaily statistics, LocalDate lastDay, LocalDate today);

    void confirmTest(Long orderId);
}
