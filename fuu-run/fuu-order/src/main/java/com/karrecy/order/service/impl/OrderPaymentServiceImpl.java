package com.karrecy.order.service.impl;

import com.karrecy.order.domain.po.OrderPayment;
import com.karrecy.order.mapper.OrderPaymentMapper;
import com.karrecy.order.service.IOrderPaymentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 */
@Service
public class OrderPaymentServiceImpl extends ServiceImpl<OrderPaymentMapper, OrderPayment> implements IOrderPaymentService {

}
