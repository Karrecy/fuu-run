package com.karrecy.order.service;

import com.karrecy.order.domain.dto.OrderAppealDTO;
import com.karrecy.order.domain.po.OrderAppeal;
import com.baomidou.mybatisplus.extension.service.IService;
import com.karrecy.order.domain.vo.OrderAppealVO;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 */
public interface IOrderAppealService extends IService<OrderAppeal> {

    /**
     * 提交申诉
     * @param orderAppealDTO
     */
    void submit(OrderAppealDTO orderAppealDTO);

    /**
     * 根据orderId查询申诉
     * @param orderId
     * @return
     */
    List<OrderAppealVO> getListByOrderId(Long orderId);

    /**
     * 处理申诉
     * @param orderAppeal
     */
    void handle(OrderAppeal orderAppeal);
}
