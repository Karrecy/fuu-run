package com.karrecy.order.service.impl;

import cn.binarywang.wx.miniapp.bean.WxMaSubscribeMessage;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.karrecy.common.config.FuuConfig;
import com.karrecy.common.core.domain.entity.UserWx;
import com.karrecy.common.core.domain.model.LoginUser;
import com.karrecy.common.enums.*;
import com.karrecy.common.exception.OrderException;
import com.karrecy.common.helper.LoginHelper;
import com.karrecy.common.utils.wx.WxHelper;
import com.karrecy.order.domain.dto.OrderAppealDTO;
import com.karrecy.order.domain.po.OrderAppeal;
import com.karrecy.order.domain.po.OrderAttachment;
import com.karrecy.order.domain.po.OrderMain;
import com.karrecy.order.domain.po.OrderPayment;
import com.karrecy.order.domain.vo.OrderAppealVO;
import com.karrecy.order.mapper.OrderAppealMapper;
import com.karrecy.order.mapper.OrderAttachmentMapper;
import com.karrecy.order.mapper.OrderMainMapper;
import com.karrecy.order.mapper.OrderPaymentMapper;
import com.karrecy.order.service.IOrderAppealService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.karrecy.payment.service.IPayService;
import com.karrecy.system.domain.po.Oss;
import com.karrecy.system.mapper.OssMapper;
import com.karrecy.system.mapper.UserWxMapper;
import com.karrecy.system.service.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 */
@Service
@RequiredArgsConstructor
public class OrderAppealServiceImpl extends ServiceImpl<OrderAppealMapper, OrderAppeal> implements IOrderAppealService {


    private final OrderAppealMapper orderAppealMapper;
    private final OrderMainMapper orderMainMapper;
    private final OrderPaymentMapper orderPaymentMapper;
    private final OrderAttachmentMapper orderAttachmentMapper;
    private final UserWxMapper userWxMapper;
    private final OssMapper ossMapper;

    private final IPayService payService;
    private final WxHelper wxHelper;
    private final FuuConfig fuuConfig;

    /**
     * 提交申诉
     * @param orderAppealDTO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submit(OrderAppealDTO orderAppealDTO) {
        //判断用户权限
        Long uid = LoginHelper.getUserId();
        OrderMain orderMainDB = orderMainMapper.selectById(orderAppealDTO.getOrderId());
        if (!ObjectUtil.equals(orderMainDB.getUserId(),uid)) {
            throw new OrderException("没有该权限");
        }
        //是否有审核中 或者 审核通过的 存在就return
        List<OrderAppeal> orderAppealsDB = orderAppealMapper.selectList(new LambdaQueryWrapper<OrderAppeal>()
                .eq(OrderAppeal::getOrderId,orderAppealDTO.getOrderId())
                .in(OrderAppeal::getAppealStatus,Status.PENDING.getCode(),Status.OK.getCode()));
        if (ObjectUtil.isNotEmpty(orderAppealsDB)) {
            throw new OrderException("已存在申诉，不可重复提交");
        }
        OrderAppeal orderAppeal = new OrderAppeal();
        orderAppeal.setOrderId(String.valueOf(orderAppealDTO.getOrderId()));
        orderAppeal.setSchoolId(orderMainDB.getSchoolId());
        orderAppeal.setAppealReason(orderAppealDTO.getAppealReason());
        orderAppeal.setAppealStatus(Status.PENDING.getCode());
        orderAppeal.setAppealTime(LocalDateTime.now());
        orderAppeal.setUpdateId(uid);
        orderAppeal.setUpdateTime(LocalDateTime.now());
        orderAppeal.setUpdateType(UserType.COMMON_USER.getCode());
        orderAppealMapper.insert(orderAppeal);
        if (ObjectUtil.isNotEmpty(orderAppealDTO.getOssAppealList())) {
            List<Oss> ossesDB = ossMapper.selectBatchIds(orderAppealDTO.getOssAppealList());
            for (Oss oss : ossesDB) {
                OrderAttachment orderAttachment = new OrderAttachment();
                orderAttachment.setOrderId(orderAppealDTO.getOrderId());
                orderAttachment.setFileType(oss.getFileSuffix());
                orderAttachment.setFileName(oss.getFileName());
                orderAttachment.setFileSize(oss.getFileSize());
                orderAttachment.setFileUrl(oss.getUrl());
                orderAttachment.setType(oss.getType());
                orderAttachmentMapper.insert(orderAttachment);
            }
        }

        orderMainDB.setStatus(OrderStatus.CLOSED.getCode());
        orderMainMapper.updateById(orderMainDB);

    }

    /**
     * 根据orderId查询申诉
     * @param orderId
     * @return
     */
    @Override
    public List<OrderAppealVO> getListByOrderId(Long orderId) {
        //校验用户权限
        Long uid = LoginHelper.getUserId();
        OrderMain orderMainDB = orderMainMapper.selectById(orderId);
        if (ObjectUtil.isNull(orderMainDB)) {
            throw new OrderException("订单不存在");
        }
        LoginUser loginUser = LoginHelper.getLoginUser();
        if (loginUser.getDeviceType().equals(DeviceType.WX.getCode())) {
            if (!ObjectUtil.equals(orderMainDB.getUserId(),uid) && !ObjectUtil.equals(orderMainDB.getRunnerId(),uid)){
                throw new OrderException("没有该权限");
            }
        }

        List<OrderAppealVO> orderAppealVOS = new ArrayList<>();
        List<OrderAppeal> orderAppealsDB = orderAppealMapper.selectList(new LambdaQueryWrapper<OrderAppeal>()
                .eq(OrderAppeal::getOrderId, orderId)
                .orderByDesc(OrderAppeal::getAppealTime));
        for (OrderAppeal orderAppeal : orderAppealsDB) {
            List<OrderAttachment> orderAttachmentsDB = orderAttachmentMapper.selectList(new LambdaQueryWrapper<OrderAttachment>().
                    eq(OrderAttachment::getOrderId, orderAppeal.getOrderId())
                    .eq(OrderAttachment::getType, FileType.APPEAL_IMAGES.getCode()));
            OrderAppealVO orderAppealVO = new OrderAppealVO();
            orderAppealVO.setOrderAppeal(orderAppeal);
            List<String> urls = orderAttachmentsDB.stream()
                    .map(OrderAttachment::getFileUrl)
                    .collect(Collectors.toList());
            orderAppealVO.setImageUrls(urls);
            orderAppealVOS.add(orderAppealVO);
        }


        return orderAppealVOS;
    }

    /**
     * 处理申诉
     * @param orderAppeal
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handle(OrderAppeal orderAppeal) {
        String orderId = orderAppeal.getOrderId();
        OrderMain orderMainDB = orderMainMapper.selectById(orderId);
        OrderPayment orderPaymentDB = orderPaymentMapper.selectById(orderId);
        if (orderAppeal.getAppealStatus().equals(Status.OK.getCode())) {
            //退款 全额退款
            payService.refund(Long.valueOf(orderId), orderPaymentDB.getActualPayment(), orderPaymentDB.getActualPayment());
            //通知用户
            List<WxMaSubscribeMessage.MsgData> msgData = wxHelper.buildOrderStatusData(
                    Long.valueOf(orderId),
                    orderMainDB.getTag(),
                    "申诉已通过",
                    "正在全额退款中"
            );
            UserWx orderUser = userWxMapper.selectOne(new LambdaQueryWrapper<UserWx>().eq(UserWx::getUid, orderMainDB.getUserId()));
            wxHelper.sendSubMsg(
                    msgData,
                    WxHelper.PAGE_ORDER_DETAIL+"?id="+orderId,
                    WxHelper.TEMPLATE_ORDER_STATUS_CHANGE,
                    orderUser.getOpenid());
            // 扣除跑腿员信用分
            UserWx runner = userWxMapper.selectOne(new LambdaQueryWrapper<UserWx>().eq(UserWx::getUid, orderMainDB.getRunnerId()));
            if (ObjectUtil.isNotEmpty(runner)) {
                runner.setCreditScore(runner.getCreditScore() - fuuConfig.getCreditDeduction());
                userWxMapper.updateById(runner);
            }
        }
        if (orderAppeal.getAppealStatus().equals(Status.DISABLE.getCode())) {
            //通知用户
            List<WxMaSubscribeMessage.MsgData> msgData = wxHelper.buildOrderStatusData(
                    Long.valueOf(orderId),
                    orderMainDB.getTag(),
                    "申诉已驳回",
                    orderAppeal.getRemarks()
            );
            UserWx orderUser = userWxMapper.selectOne(new LambdaQueryWrapper<UserWx>().eq(UserWx::getUid, orderMainDB.getUserId()));
            wxHelper.sendSubMsg(
                    msgData,
                    WxHelper.PAGE_ORDER_DETAIL+"?id="+orderId,
                    WxHelper.TEMPLATE_ORDER_STATUS_CHANGE,
                    orderUser.getOpenid());
        }
        orderAppeal.setUpdateTime(LocalDateTime.now());
        orderAppeal.setUpdateId(LoginHelper.getUserId());
        orderAppeal.setUpdateType(LoginHelper.getUserType().getCode());
        orderAppealMapper.updateById(orderAppeal);
        orderPaymentDB.setPaymentStatus(PayStatus.REFUND_IN_PROGRESS.getCode());
        orderPaymentDB.setRefundPendingTime(LocalDateTime.now());
        orderPaymentMapper.updateById(orderPaymentDB);
    }
}
