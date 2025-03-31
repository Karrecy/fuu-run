package com.karrecy.order.service.impl;

import cn.binarywang.wx.miniapp.bean.WxMaSubscribeMessage;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.binarywang.wxpay.service.WxPayService;
import com.karrecy.common.config.FuuConfig;
import com.karrecy.common.constant.CacheNames;
import com.karrecy.common.constant.QueueNames;
import com.karrecy.common.constant.UserConstants;
import com.karrecy.common.core.domain.PageQuery;
import com.karrecy.common.core.domain.entity.StatisticsDaily;
import com.karrecy.common.core.domain.entity.User;
import com.karrecy.common.core.domain.entity.UserWx;
import com.karrecy.common.core.domain.model.LoginUser;
import com.karrecy.common.core.page.TableDataInfo;
import com.karrecy.common.enums.*;
import com.karrecy.common.exception.OrderException;
import com.karrecy.common.helper.LoginHelper;
import com.karrecy.common.utils.BigDecimalUtils;
import com.karrecy.common.utils.StringUtils;
import com.karrecy.common.utils.redis.QueueUtils;
import com.karrecy.common.utils.redis.RedisUtils;
import com.karrecy.common.utils.wx.WxHelper;
import com.karrecy.order.domain.dto.OrderCancelDTO;
import com.karrecy.order.domain.dto.OrderCompleteDTO;
import com.karrecy.order.domain.dto.OrderQuery;
import com.karrecy.order.domain.dto.OrderSubmitDTO;
import com.karrecy.order.domain.po.*;
import com.karrecy.order.domain.vo.AddressVO;
import com.karrecy.order.domain.vo.OrderDetailVO;
import com.karrecy.order.mapper.*;
import com.karrecy.order.service.IOrderMainService;
import com.karrecy.order.util.OrderCancelRule;
import com.karrecy.payment.domain.po.CapitalFlow;
import com.karrecy.payment.domain.vo.PayedVO;
import com.karrecy.payment.mapper.CapitalFlowMapper;
import com.karrecy.payment.properties.WxPayProperties;
import com.karrecy.payment.service.IPayService;
import com.karrecy.system.domain.po.Oss;
import com.karrecy.system.domain.po.Wallet;
import com.karrecy.system.mapper.OssMapper;
import com.karrecy.system.mapper.UserMapper;
import com.karrecy.system.mapper.UserWxMapper;
import com.karrecy.system.mapper.WalletMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.karrecy.common.enums.OrderStatus.CANCELED;

/**
 * <p>
 *  服务实现类
 * </p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderMainServiceImpl extends ServiceImpl<OrderMainMapper, OrderMain> implements IOrderMainService {

    private final FuuConfig fuuConfig;
    private final WxHelper wxHelper;

    private final SchoolMapper schoolMapper;
    private final UserMapper userMapper;
    private final UserWxMapper userWxMapper;
    private final OrderAttachmentMapper orderAttachmentMapper;
    private final OrderMainMapper orderMainMapper;
    private final OrderPaymentMapper orderPaymentMapper;
    private final OrderProgressMapper orderProgressMapper;
    private final OrderChatMapper orderChatMapper;

    private final WxPayProperties wxPayProperties;
    private final WxPayService wxPayService;

    private final IPayService payService;

    private final OssMapper ossMapper;

    private final WalletMapper walletMapper;
    private final CapitalFlowMapper capitalFlowMapper;
    private final OrderAppealMapper orderAppealMapper;

    private final RedissonClient redissonClient;

    /**
     * 支付成功
     * @param event
     */
//    @Async
//    @EventListener
//    @Transactional(rollbackFor = Exception.class)
//    public void paySuccess(PaymentSuccessEvent event) {
//        Long orderId = event.getOrderId();
//        log.info("事件监听，订单id为：{}",orderId);
//        try {
//            //更新基本订单状态为 待接单
//            OrderMain orderMainDB = orderMainMapper.selectById(orderId);
//            orderMainDB.setStatus(OrderStatus.PENDING_ACCEPTANCE.getCode());
//            orderMainMapper.updateById(orderMainDB);
//            //更新订单支付表
//            OrderPayment orderPaymentDB = orderPaymentMapper.selectById(orderId);
//            orderPaymentDB.setPaymentStatus(PayStatus.PAID.getCode());
//            orderPaymentDB.setPaymentTime(LocalDateTime.now());
//            orderPaymentMapper.updateById(orderPaymentDB);
//
//            //添加到延迟队列
//            Integer autoCancelTtl = orderMainDB.getAutoCancelTtl();
//            QueueUtils.addDelayedQueueObject(
//                    QueueNames.ORDER_PENDING_ACCEPT_CANCEL,
//                    orderId,autoCancelTtl,
//                    TimeUnit.MINUTES);
//        }
//        catch (Exception e) {
//            log.error("用户支付成功，但更新订单失败，{}",e.getMessage());
//        }
//
//    }

    /**
     * 新建订单
     *
     * @param orderSubmitDTO
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public PayedVO submit(OrderSubmitDTO orderSubmitDTO) {
        //判断学校状态
        School schoolDB = schoolMapper.selectById(orderSubmitDTO.getSchoolId());
        if (schoolDB == null) {
            log.error("没有找到学校,id:{}",schoolDB.getId());
            throw new OrderException("没有找到该学校");
        }
        if (ObjectUtil.equals(schoolDB.getStatus(), Status.DISABLE.getCode())) {
            log.error("当前学校:{} 已关闭，不可下单",schoolDB.getId());
            throw new OrderException("当前学校已关闭，不可下单");
        }
        //判断用户状态
        Long uid = LoginHelper.getUserId();
        UserWx userWx = userWxMapper.selectOne(new LambdaQueryWrapper<UserWx>().eq(UserWx::getUid, uid));
        if (ObjectUtil.isEmpty(userWx)) {
            log.error("该微信用户不存在，uid:{}",uid);
            throw new OrderException("该微信用户不存在");
        }
        if (ObjectUtil.isNull(userWx.getPhone())) {
            log.error("账号:{} 未绑定手机号，不可下单",userWx.getUid());
            throw new OrderException("未绑定手机号，不可下单");
        }
        if (ObjectUtil.equals(userWx.getCanOrder(),Status.DISABLE.getCode())) {
            log.error("账号:{} 被拉黑，不可下单",userWx.getUid());
            throw new OrderException("账号被拉黑，不可下单");
        }
        Integer serviceType = orderSubmitDTO.getServiceType(); // 服务类型
        //构建order基本表
        OrderMain orderMain = new OrderMain();
        orderMain.setSchoolId(schoolDB.getId()); //学校id
        orderMain.setServiceType(serviceType); //服务类型
        orderMain.setTag(orderSubmitDTO.getTag()); //tag
        orderMain.setWeight(orderSubmitDTO.getWeight()); //物品重量
        orderMain.setDetail(orderSubmitDTO.getDetail()); //订单详情
        orderMain.setGender(orderSubmitDTO.getGender()); //性别限制

        Integer isTimed = orderSubmitDTO.getIsTimed();
        orderMain.setIsTimed(isTimed); //是否指定配送时间
        //指定了配送时间
        if (ObjectUtil.equals(isTimed, Status.OK.getCode())) {
            // 加入到队列

            orderMain.setSpecifiedTime(orderSubmitDTO.getSpecifiedTime()); //指定配送时间
        }
        else orderMain.setSpecifiedTime(null); //指定配送时间


        orderMain.setAutoCancelTtl(orderSubmitDTO.getAutoCancelTtl()); //未接单字段取消时间
        // 加入到队列


        BigDecimal floorPrice = schoolDB.getFloorPrice(); //当前学校的底价
        BigDecimal additionalAmount = orderSubmitDTO.getAdditionalAmount(); //追加的金额
        BigDecimal totalAmount = BigDecimalUtils.add(floorPrice,additionalAmount); //订单总金额

        orderMain.setTotalAmount(totalAmount); //总金额
        orderMain.setStatus(OrderStatus.PENDING_PAYMENT.getCode()); //订单状态:未付款
        orderMain.setCreateTime(LocalDateTime.now()); //创建时间
        orderMain.setUserId(uid); //下单用户id
        orderMain.setRunnerId(null);

        AddressVO startAddress = orderSubmitDTO.getStartAddress();
        AddressVO endAddress = orderSubmitDTO.getEndAddress();
        if (ObjectUtil.isEmpty(endAddress)) {
            throw new OrderException("终点地址不可为空");
        }
        orderMain.setStartAddress(null); //起点
        orderMain.setEndAddress(endAddress); //终点

        orderMain.setEstimatedPrice(null);
        // 如果是帮取送订单
        if (ObjectUtil.equals(serviceType, ServiceType.PICKUP_DELIVERY.getCode())) {
            orderMain.setStartAddress(orderSubmitDTO.getStartAddress());
        }
        // 帮买订单
        else if (ObjectUtil.equals(serviceType, ServiceType.PURCHASE.getCode())) {

            orderMain.setStartAddress(StringUtils.isBlank(startAddress.getLat()) ? null : startAddress);
            orderMain.setEstimatedPrice(orderSubmitDTO.getEstimatedPrice()); //预估商品价格
        }
        // 万能帮订单
        else if (ObjectUtil.equals(serviceType, ServiceType.PURCHASE.getCode())) {

        }

        orderMainMapper.insert(orderMain);
        Long orderId = orderMain.getId();

        //构建order支付表
        OrderPayment orderPayment = new OrderPayment();
        orderPayment.setOrderId(orderId);
        orderPayment.setAdditionalAmount(orderSubmitDTO.getAdditionalAmount()); //追加金额
        orderPayment.setActualPayment(totalAmount); //实付金额
        orderPayment.setPaymentStatus(PayStatus.UNPAID.getCode()); //付款状态 未付款
        orderPayment.setRefundPendingTime(null); //开始退款时间
        orderPayment.setRefundTime(null); //退款到账时间
        orderPayment.setIsCouponed(Status.DISABLE.getCode()); //是否使用优惠券
        orderPayment.setCouponId(null); //优惠券id
        orderPayment.setDiscountAmount(null); //优惠金额
        orderPaymentMapper.insert(orderPayment);

        //构建order流程表
        OrderProgress orderProgress = new OrderProgress();
        orderProgress.setOrderId(orderId);
        orderProgress.setAcceptedTime(null); //接单时间
        orderProgress.setDeliveredTime(null); //送达时间
        orderProgress.setCompletedTime(null); //完成时间
        orderProgress.setCompletedType(null); //由谁完成
        orderProgress.setCancelTime(null); //取消时间
        orderProgress.setCancelReason(null); //取消原因
        orderProgress.setCancelUserType(null); //取消人类型
        orderProgress.setCancelUserId(null); //取消人id
        orderProgressMapper.insert(orderProgress);

        //构建order附件表
        List<Long> attachImages = orderSubmitDTO.getAttachImages();
        if (ObjectUtil.isNotEmpty(attachImages)) {
            List<Oss> ossImages = ossMapper.selectBatchIds(attachImages);
            if (!ObjectUtil.isEmpty(ossImages)) {
                for (Oss ossDB : ossImages) {
                    OrderAttachment orderAttachment = new OrderAttachment();
                    orderAttachment.setOrderId(orderId);
                    orderAttachment.setType(ossDB.getType()); // 附件类型
                    orderAttachment.setFileName(ossDB.getOriginalName()); //原名
                    orderAttachment.setFileUrl(ossDB.getUrl()); // url
                    orderAttachment.setFileSize(ossDB.getFileSize()); //文件大小 字节
                    orderAttachment.setFileType(ossDB.getFileSuffix()); //后缀
                    orderAttachmentMapper.insert(orderAttachment);
                }
            }
        }
        List<Long> attachFiles = orderSubmitDTO.getAttachFiles();
        if (ObjectUtil.isNotEmpty(attachFiles)) {
            List<Oss> ossFiles = ossMapper.selectBatchIds(attachFiles);
            if (!ObjectUtil.isEmpty(ossFiles)) {
                for (Oss ossDB : ossFiles) {
                    OrderAttachment orderAttachment = new OrderAttachment();
                    orderAttachment.setOrderId(orderId);
                    orderAttachment.setType(ossDB.getType()); // 附件类型
                    orderAttachment.setFileName(ossDB.getOriginalName()); //原名
                    orderAttachment.setFileUrl(ossDB.getUrl()); // url
                    orderAttachment.setFileSize(ossDB.getFileSize()); //文件大小 字节
                    orderAttachment.setFileType(ossDB.getFileSuffix()); //后缀
                    orderAttachmentMapper.insert(orderAttachment);
                }
            }
        }

        //请求微信支付
        PayedVO payedVO = payService.pay("跑腿订单",orderId,totalAmount,userWx.getOpenid());


        //加入延时队列
        QueueUtils.addDelayedQueueObject(
                QueueNames.ORDER_PAY_CANCEL,
                String.valueOf(orderId),
                fuuConfig.getPayCancelTtl(),
                TimeUnit.MINUTES);

        return payedVO;
    }

    @Override
    public TableDataInfo<OrderMain> pageHall(OrderQuery orderQuery, PageQuery pageQuery) {
        LambdaQueryWrapper<OrderMain> lqm = new LambdaQueryWrapper<OrderMain>()
                .select(
                        OrderMain::getId,
                        OrderMain::getSchoolId,
                        OrderMain::getServiceType,
                        OrderMain::getStatus,
                        OrderMain::getCreateTime,
                        OrderMain::getStartAddress,
                        OrderMain::getEndAddress,
                        OrderMain::getTag,
                        OrderMain::getDetail,
                        OrderMain::getTotalAmount)
                .orderByDesc(OrderMain::getCreateTime)
                .eq(ObjectUtil.isNotNull(orderQuery.getId()), OrderMain::getId, orderQuery.getId())
                .eq(ObjectUtil.isNotNull(orderQuery.getSchoolId()), OrderMain::getSchoolId, orderQuery.getSchoolId())
                .eq(ObjectUtil.isNotNull(orderQuery.getOrderUid()), OrderMain::getUserId, orderQuery.getOrderUid())
                .eq(ObjectUtil.isNotNull(orderQuery.getTakerUid()), OrderMain::getRunnerId, orderQuery.getTakerUid())
                .eq(ObjectUtil.isNotNull(orderQuery.getServiceType()), OrderMain::getServiceType, orderQuery.getServiceType())
                .between(ObjectUtil.isNotNull(orderQuery.getBeginTime()), OrderMain::getCreateTime, orderQuery.getBeginTime(), orderQuery.getEndTime())
                .eq(ObjectUtil.isNotNull(orderQuery.getStatus()), OrderMain::getStatus, orderQuery.getStatus());

        //如果是校区管理员 只能查看自己校区
        if (LoginHelper.isSchoolAdmin()) {
            School school = schoolMapper.selectOne(new LambdaQueryWrapper<School>().eq(School::getBelongUid, LoginHelper.getUserId()));
            lqm.eq(OrderMain::getSchoolId,school.getId());
        }
        Page<OrderMain> orderMainPage = orderMainMapper.selectPage(pageQuery.build(), lqm);
        return TableDataInfo.build(orderMainPage);
    }

    @Override
    public TableDataInfo<OrderMain> pageUser(OrderQuery orderQuery, PageQuery pageQuery) {
        LambdaQueryWrapper<OrderMain> lqm = new LambdaQueryWrapper<OrderMain>()
                .select(
                        OrderMain::getId,
//                        OrderMain::getSchoolId,
                        OrderMain::getDetail,
                        OrderMain::getServiceType,
                        OrderMain::getStatus,
                        OrderMain::getCreateTime,
                        OrderMain::getStartAddress,
                        OrderMain::getEndAddress,
                        OrderMain::getTag,
                        OrderMain::getTotalAmount)
                .orderByDesc(OrderMain::getCreateTime)
                .eq(ObjectUtil.isNotNull(orderQuery.getStatus()), OrderMain::getStatus, orderQuery.getStatus());

        if (ObjectUtil.equals(orderQuery.getOrderOrTake(),0)) {
            lqm.eq(OrderMain::getUserId,LoginHelper.getUserId());
            lqm.eq(OrderMain::getSchoolId, orderQuery.getSchoolId());

        }
        else {
            lqm.eq(OrderMain::getRunnerId,LoginHelper.getUserId());
        }

        if (ObjectUtil.isNotNull(orderQuery.getBeginTime()) && ObjectUtil.isNotNull(orderQuery.getEndTime())) {
            lqm.between(OrderMain::getCreateTime,orderQuery.getBeginTime(),orderQuery.getEndTime());
        }
        Page<OrderMain> orderMainPage = orderMainMapper.selectPage(pageQuery.build(), lqm);
        return TableDataInfo.build(orderMainPage);
    }

    /**
     * 订单详情查询
     * @param orderId
     * @return
     */
    @Override
    public OrderDetailVO getDetail(Long orderId) {
        Long uid = LoginHelper.getUserId();
        OrderMain orderMainDB = orderMainMapper.selectById(orderId); //订单基本表
        Long userId = orderMainDB.getUserId();
        LoginUser loginUser = LoginHelper.getLoginUser();
        Long runnerId = orderMainDB.getRunnerId();
        UserWx userWxDB = userWxMapper.selectOne(new LambdaQueryWrapper<UserWx>().eq(UserWx::getUid, uid));

        if (loginUser.getDeviceType().equals(DeviceType.WX.getCode())) {
            if (!ObjectUtil.equals(userId,uid) && !ObjectUtil.equals(userWxDB.getIsRunner(),Status.OK.getCode())) {
                throw new OrderException("没有权限查看订单");
            }
        }
        else {

        }

        //订单基本信息
        OrderDetailVO orderDetailVO = new OrderDetailVO();
        orderDetailVO.setOrderMain(orderMainDB);

        if (ObjectUtil.isNotNull(runnerId)) {
            UserWx runnerDB = userWxMapper.selectOne(new LambdaQueryWrapper<UserWx>().eq(UserWx::getUid,runnerId));
            orderDetailVO.setAvatarRunner(runnerDB.getAvatar());
            orderDetailVO.setNicknameRunner(runnerDB.getNickname());
        }
        UserWx userDB = userWxMapper.selectOne(new LambdaQueryWrapper<UserWx>().eq(UserWx::getUid,orderMainDB.getUserId()));
        orderDetailVO.setAvatarUser(userDB.getAvatar());
        orderDetailVO.setNicknameUser(userDB.getNickname());

        //订单流程信息
        OrderProgress orderProgressDB = orderProgressMapper.selectById(orderId);
        orderDetailVO.setProgress(orderProgressDB);
        //订单支付信息
        OrderPayment orderPaymentDB = orderPaymentMapper.selectById(orderId);
        orderDetailVO.setOrderPayment(orderPaymentDB);
        //附件图片
        LambdaQueryWrapper<OrderAttachment> lqmAttachImages = new LambdaQueryWrapper<OrderAttachment>()
                .eq(OrderAttachment::getOrderId, orderId)
                .eq(OrderAttachment::getType, FileType.ADDITIONAL_IMAGES.getCode());
        List<OrderAttachment> attachImagesDB = orderAttachmentMapper.selectList(lqmAttachImages);
        orderDetailVO.setAttachImages(attachImagesDB);
        //附件文件
        LambdaQueryWrapper<OrderAttachment> lqmAttachFiles = new LambdaQueryWrapper<OrderAttachment>()
                .eq(OrderAttachment::getOrderId, orderId)
                .eq(OrderAttachment::getType, FileType.ADDITIONAL_FILES.getCode());
        List<OrderAttachment> attachFilesDB = orderAttachmentMapper.selectList(lqmAttachFiles);
        orderDetailVO.setAttachFiles(attachFilesDB);
        //完成凭证
        LambdaQueryWrapper<OrderAttachment> lqmCompletionImages = new LambdaQueryWrapper<OrderAttachment>()
                .eq(OrderAttachment::getOrderId, orderId)
                .eq(OrderAttachment::getType, FileType.COMPLETION_IMAGES.getCode());
        List<OrderAttachment> completionImagesDB = orderAttachmentMapper.selectList(lqmCompletionImages);
        orderDetailVO.setCompletionImages(completionImagesDB);

//        if (orderMainDB.getStatus().equals(OrderStatus.CLOSED.getCode())) {
//            List<OrderAppeal> orderAppeals = orderAppealMapper.selectList(new LambdaQueryWrapper<OrderAppeal>()
//                    .eq(OrderAppeal::getOrderId, orderId));
//            orderDetailVO.setor
//        }

        return orderDetailVO;
    }

    /**
     * 继续支付
     * @param orderId
     * @return
     */
    @Override
    public PayedVO payAgain(Long orderId) {
        OrderMain orderMainDB = orderMainMapper.selectById(orderId);
        if (!orderMainDB.getStatus().equals(OrderStatus.PENDING_PAYMENT.getCode())) {
            throw new OrderException("订单状态已改变，无法支付");
        }
        OrderPayment orderPaymentDB = orderPaymentMapper.selectById(orderId);
        UserWx userWx = userWxMapper.selectOne(new LambdaQueryWrapper<UserWx>()
                .eq(UserWx::getUid,LoginHelper.getUserId()));
        PayedVO pay = payService.pay("跑腿订单", orderId, orderPaymentDB.getActualPayment(), userWx.getOpenid());
        return pay;
    }

    /**
     * 取消订单
     * @param orderCancelDTO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancel(OrderCancelDTO orderCancelDTO) {
        Long orderId = orderCancelDTO.getOrderId();
        String cancelReason = orderCancelDTO.getCancelReason();
        if (StringUtils.isBlank(cancelReason)) cancelReason = "未给出理由";
        Long uid = LoginHelper.getUserId();
        OrderMain orderMainDB = orderMainMapper.selectById(orderId);
        OrderPayment orderPaymentDB = orderPaymentMapper.selectById(orderId);
        OrderProgress orderProgressDB = orderProgressMapper.selectById(orderId);
        Integer status = orderMainDB.getStatus();

        //待支付取消
        if (ObjectUtil.equals(status,OrderStatus.PENDING_PAYMENT.getCode())) {
            //删除延迟队列
            QueueUtils.removeDelayedQueueObject(
                    QueueNames.ORDER_PAY_CANCEL,
                    orderId);
        }
        //待接单取消
        if (ObjectUtil.equals(status,OrderStatus.PENDING_ACCEPTANCE.getCode())) {
            //删除延迟队列
            QueueUtils.removeDelayedQueueObject(
                    QueueNames.ORDER_PENDING_ACCEPT_CANCEL,
                    orderId);
        }

        //下单用户取消
        if (ObjectUtil.equals(orderMainDB.getUserId(),uid)) {

            //待接单取消
            if (ObjectUtil.equals(status,OrderStatus.PENDING_ACCEPTANCE.getCode())) {
                //退款
                payService.refund(
                        orderId,
                        orderPaymentDB.getActualPayment(),
                        orderPaymentDB.getActualPayment() //全额退款
                );
            }
            //待配送取消
            else if (ObjectUtil.equals(status,OrderStatus.PENDING_DELIVER.getCode())) {
                UserWx runner = userWxMapper.selectOne(new LambdaQueryWrapper<UserWx>().eq(UserWx::getUid, orderMainDB.getRunnerId()));
                //通知跑腿员
                List<WxMaSubscribeMessage.MsgData> msgData = wxHelper.buildOrderStatusData(
                        orderId,
                        orderMainDB.getTag(),
                        OrderStatus.getString(CANCELED.getCode()),
                        "取消原因:"+cancelReason
                );
                wxHelper.sendSubMsg(
                        msgData,
                        WxHelper.PAGE_ORDER_DETAIL+"?id="+orderId,
                        WxHelper.TEMPLATE_ORDER_STATUS_CHANGE,
                        runner.getOpenid()
                );
                //退款
                payService.refund(
                        orderId,
                        orderPaymentDB.getActualPayment(),
                        orderPaymentDB.getActualPayment() //全额退款
                );
            }
            //配送中取消
            else if (ObjectUtil.equals(status,OrderStatus.DELIVERING.getCode())) {
                //通知跑腿员
                UserWx runner = userWxMapper.selectOne(new LambdaQueryWrapper<UserWx>().eq(UserWx::getUid, orderMainDB.getRunnerId()));
                List<WxMaSubscribeMessage.MsgData> msgData = wxHelper.buildOrderStatusData(
                        orderId,
                        orderMainDB.getTag(),
                        OrderStatus.getString(CANCELED.getCode()),
                        "取消原因:"+cancelReason
                );
                wxHelper.sendSubMsg(
                        msgData,
                        WxHelper.PAGE_ORDER_DETAIL+"?id="+orderId,
                        WxHelper.TEMPLATE_ORDER_STATUS_CHANGE,
                        runner.getOpenid()
                );
                //计算手续费
                //退款
                payService.refund(
                        orderId,
                        orderPaymentDB.getActualPayment(),
                        OrderCancelRule.calculateRefund(orderPaymentDB.getActualPayment(),orderProgressDB.getAcceptedTime())
                );
            }
            else {
                throw new OrderException("当前状态不可取消");
            }

        }
        //跑腿员取消
        else if (ObjectUtil.equals(orderMainDB.getRunnerId(),uid)) {
            if (status.equals(OrderStatus.PENDING_DELIVER.getCode())
            || status.equals(OrderStatus.DELIVERING.getCode())) {
                UserWx user = userWxMapper.selectOne(new LambdaQueryWrapper<UserWx>().eq(UserWx::getUid, orderMainDB.getUserId()));
                UserWx runner = userWxMapper.selectOne(new LambdaQueryWrapper<UserWx>().eq(UserWx::getUid, orderMainDB.getRunnerId()));
                List<WxMaSubscribeMessage.MsgData> msgData = wxHelper.buildOrderStatusData(
                        orderId,
                        orderMainDB.getTag(),
                        OrderStatus.getString(CANCELED.getCode()),
                        "取消原因:"+cancelReason
                );
                wxHelper.sendSubMsg(
                        msgData,
                        WxHelper.PAGE_ORDER_DETAIL+"?id="+orderId,
                        WxHelper.TEMPLATE_ORDER_STATUS_CHANGE,
                        user.getOpenid()
                );
                //退款
                payService.refund(
                        orderId,
                        orderPaymentDB.getActualPayment(),
                        orderPaymentDB.getActualPayment() //全额退款
                );
                //扣除信用分
                runner.setCreditScore(runner.getCreditScore() - fuuConfig.getCreditDeduction());
                userWxMapper.updateById(runner);
            }
            else {
                throw new OrderException("当前状态不可取消");
            }
        } else if (LoginHelper.isAdmin(uid)) {
            //管理员取消 全额退款 不罚跑腿员
            if (orderPaymentDB.getPaymentStatus().equals(PayStatus.PAID.getCode())) {
                //退款
                payService.refund(
                        orderId,
                        orderPaymentDB.getActualPayment(),
                        orderPaymentDB.getActualPayment() //全额退款
                );
            }
        } else {
            throw new OrderException("没有权限取消订单");
        }

        orderMainDB.setStatus(CANCELED.getCode());
        orderMainMapper.updateById(orderMainDB);

        orderProgressDB.setCancelUserType(UserType.COMMON_USER.getCode());
        orderProgressDB.setCancelReason(cancelReason);
        orderProgressDB.setCancelTime(LocalDateTime.now());
        orderProgressDB.setCancelUserId(uid);
        orderProgressMapper.updateById(orderProgressDB);
    }

    /**
     * 跑腿员接单
     * @param orderId
     */
    @Override
    public void accept(Long orderId) {
        // 创建分布式锁key
        String lockKey = "order:accept:" + orderId;
        RLock lock = redissonClient.getLock(lockKey);
        try {
            // 尝试获取锁，等待5秒，30秒后自动释放
            boolean locked = lock.tryLock(5, 30, TimeUnit.SECONDS);
            if (!locked) {
                throw new OrderException("订单正在处理中，请稍后重试");
            }
            OrderMain orderMainDB = orderMainMapper.selectById(orderId);
            School schoolDB = schoolMapper.selectById(orderMainDB.getSchoolId());
            //判断当前学校是否运营中
            if (!ObjectUtil.equals(schoolDB.getStatus(),Status.OK.getCode())) {
                throw new OrderException("当前校区已暂停运营，不可接单");
            }
            //判断当前用户是否可以接单
            Long uid = LoginHelper.getUserId();
            UserWx userWxDB = userWxMapper.selectOne(new LambdaQueryWrapper<UserWx>().eq(UserWx::getUid, uid));
            if (!ObjectUtil.equals(userWxDB.getCanTake(),Status.OK.getCode())) {
                throw new OrderException("当前状态不可接单");
            }
            if (!userWxDB.getSchoolId().equals(schoolDB.getId())) {
                throw new OrderException("不能接受非绑定校区");
            }
            //判断跑腿员是否为订单的创建者
            if (ObjectUtil.equals(orderMainDB.getUserId(),uid)) {
                throw new OrderException("自己的订单不可接单");
            }
            //判断当前订单状态 是否为待接单
            if (!ObjectUtil.equals(orderMainDB.getStatus(),OrderStatus.PENDING_ACCEPTANCE.getCode())) {
                throw new OrderException("订单状态已更改，请重试");
            }
            // 在更新订单状态前再次检查订单状态（双重检查）
            OrderMain currentOrder = orderMainMapper.selectById(orderId);
            if (!ObjectUtil.equals(currentOrder.getStatus(), OrderStatus.PENDING_ACCEPTANCE.getCode())) {
                throw new OrderException("订单状态已更改，请重试");
            }

            //绑定跑腿员
            orderMainDB.setRunnerId(uid);
            //更改主订单状态
            orderMainDB.setStatus(OrderStatus.PENDING_DELIVER.getCode());
            orderMainMapper.updateById(orderMainDB);
            //更新流程订单
            OrderProgress orderProgressDB = orderProgressMapper.selectById(orderId);
            orderProgressDB.setAcceptedTime(LocalDateTime.now());
            orderProgressMapper.updateById(orderProgressDB);


            List<WxMaSubscribeMessage.MsgData> msgData = wxHelper.buildOrderStatusData(
                    orderId,
                    orderMainDB.getTag(),
                    OrderStatus.getString(OrderStatus.PENDING_DELIVER.getCode()),
                    "跑腿员已接单，等待配送"
            );
            UserWx orderUser = userWxMapper.selectOne(new LambdaQueryWrapper<UserWx>().eq(UserWx::getUid, orderMainDB.getUserId()));
            wxHelper.sendSubMsg(
                    msgData,
                    WxHelper.PAGE_ORDER_DETAIL+"?id="+orderId,
                    WxHelper.TEMPLATE_ORDER_STATUS_CHANGE,
                    orderUser.getOpenid()
            );

            //记录活跃跑腿员
            RedisUtils.recordAU(
                    LocalDate.now().toString(),
                    orderMainDB.getRunnerId().toString(),
                    CacheNames.DAILY_AU_RUNNER
            );
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new OrderException("接单操作被中断");
        } finally {
            // 确保释放锁
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }

    }

    /**
     * 跑腿员配送
     * @param orderId
     */
    @Override
    public void delivery(Long orderId) {
        OrderMain orderMainDB = orderMainMapper.selectById(orderId);
        School schoolDB = schoolMapper.selectById(orderMainDB.getSchoolId());
        //判断当前学校是否运营中
        if (!ObjectUtil.equals(schoolDB.getStatus(),Status.OK.getCode())) {
            throw new OrderException("当前校区已暂停运营，不可接单");
        }
        Long uid = LoginHelper.getUserId();
        //判断该用户是否是该订单的跑腿员
        if (!ObjectUtil.equals(orderMainDB.getRunnerId(),uid)) {
            throw new OrderException("没有该权限");
        }

        //更改主订单状态
        orderMainDB.setStatus(OrderStatus.DELIVERING.getCode());
        orderMainMapper.updateById(orderMainDB);
        //更新流程订单
        OrderProgress orderProgressDB = orderProgressMapper.selectById(orderId);
        orderProgressDB.setDeliveringTime(LocalDateTime.now());
        orderProgressMapper.updateById(orderProgressDB);

        List<WxMaSubscribeMessage.MsgData> msgData = wxHelper.buildOrderStatusData(
                orderId,
                orderMainDB.getTag(),
                OrderStatus.getString(OrderStatus.DELIVERING.getCode()),
                "跑腿员开始配送"
        );
        UserWx orderUser = userWxMapper.selectOne(new LambdaQueryWrapper<UserWx>().eq(UserWx::getUid, orderMainDB.getUserId()));
        wxHelper.sendSubMsg(
                msgData,
                WxHelper.PAGE_ORDER_DETAIL+"?id="+orderId,
                WxHelper.TEMPLATE_ORDER_STATUS_CHANGE,
                orderUser.getOpenid()
        );
    }

    /**
     * 完成订单
     * @param orderCompleteDTO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void complete(OrderCompleteDTO orderCompleteDTO) {

        Long orderId = orderCompleteDTO.getOrderId();
        Long uid = LoginHelper.getUserId();
        OrderMain orderMainDB = orderMainMapper.selectById(orderId);
        if (!ObjectUtil.equals(orderMainDB.getStatus(),OrderStatus.DELIVERING.getCode())) {
            throw new OrderException("当前状态不可完成");
        }
        if (!ObjectUtil.equals(orderMainDB.getRunnerId(),uid)) {
            throw new OrderException("没有该权限");
        }

        List<Long> completionImages = orderCompleteDTO.getCompletionImages();
        if (ObjectUtil.isEmpty(completionImages)) {
            throw new OrderException("请先上传完成凭证");
        }
        if (completionImages.size() > fuuConfig.getCompletionImagesLimit()) {
            throw new OrderException("最多上传" + fuuConfig.getCompletionImagesLimit() + "张");
        }
        List<Oss> ossImages = ossMapper.selectBatchIds(completionImages);
        if (!ObjectUtil.isEmpty(ossImages)) {
            for (Oss ossDB : ossImages) {
                OrderAttachment orderAttachment = new OrderAttachment();
                orderAttachment.setOrderId(orderId);
                orderAttachment.setType(ossDB.getType()); // 附件类型
                orderAttachment.setFileName(ossDB.getOriginalName()); //原名
                orderAttachment.setFileUrl(ossDB.getUrl()); // url
                orderAttachment.setFileSize(ossDB.getFileSize()); //文件大小 字节
                orderAttachment.setFileType(ossDB.getFileSuffix()); //后缀
                orderAttachmentMapper.insert(orderAttachment);
            }
        }
        //更新主订单
        orderMainDB.setStatus(OrderStatus.DELIVERED.getCode());
        orderMainMapper.updateById(orderMainDB);
        //更新流程订单
        OrderProgress orderProgressDB = orderProgressMapper.selectById(orderId);
        orderProgressDB.setDeliveredTime(LocalDateTime.now());
        orderProgressMapper.updateById(orderProgressDB);

        //加入到延迟队列
        QueueUtils.addDelayedQueueObject(
                QueueNames.ORDER_DELIVERED_AUTO_COMPLETE,
                orderId,
                fuuConfig.getAutoCompleteTtl(),
                TimeUnit.HOURS
        );

        //通知用户
        List<WxMaSubscribeMessage.MsgData> msgData = wxHelper.buildOrderStatusData(
                orderId,
                orderMainDB.getTag(),
                OrderStatus.getString(CANCELED.getCode()),
                "订单已送达，请及时查看凭证"
        );
        UserWx orderUser = userWxMapper.selectOne(new LambdaQueryWrapper<UserWx>().eq(UserWx::getUid, orderMainDB.getUserId()));
        wxHelper.sendSubMsg(
                msgData,
                WxHelper.PAGE_ORDER_DETAIL+"?id="+orderId,
                WxHelper.TEMPLATE_ORDER_STATUS_CHANGE,
                orderUser.getOpenid()
        );
    }

    /**
     * 补充凭证
     * @param orderCompleteDTO
     */
    @Override
    public void updateImages(OrderCompleteDTO orderCompleteDTO) {
        Long orderId = orderCompleteDTO.getOrderId();
        Long uid = LoginHelper.getUserId();
        OrderMain orderMainDB = orderMainMapper.selectById(orderId);
        // 校验用户权限
        if (!ObjectUtil.equals(orderMainDB.getRunnerId(),uid)) {
            throw new OrderException("没有该权限");
        }
        // 校验订单状态
        if (!ObjectUtil.equals(orderMainDB.getStatus(),OrderStatus.DELIVERED.getCode())) {
            throw new OrderException("该状态不可补充");
        }
        // 校验凭证数量
        List<OrderAttachment> orderAttachmentsDB = orderAttachmentMapper.selectList(new LambdaQueryWrapper<OrderAttachment>().eq(OrderAttachment::getOrderId, orderId));

        if (orderCompleteDTO.getCompletionImages().size() > fuuConfig.getCompletionImagesLimit()
                || (orderAttachmentsDB.size() + orderCompleteDTO.getCompletionImages().size()) > fuuConfig.getCompletionImagesLimit()) {
            throw new OrderException("最多上传" + fuuConfig.getCompletionImagesLimit() + "张");
        }
        List<Oss> ossImages = ossMapper.selectBatchIds(orderCompleteDTO.getCompletionImages());
        if (!ObjectUtil.isEmpty(ossImages)) {
            for (Oss ossDB : ossImages) {
                OrderAttachment orderAttachment = new OrderAttachment();
                orderAttachment.setOrderId(orderId);
                orderAttachment.setType(ossDB.getType()); // 附件类型
                orderAttachment.setFileName(ossDB.getOriginalName()); //原名
                orderAttachment.setFileUrl(ossDB.getUrl()); // url
                orderAttachment.setFileSize(ossDB.getFileSize()); //文件大小 字节
                orderAttachment.setFileType(ossDB.getFileSuffix()); //后缀
                orderAttachmentMapper.insert(orderAttachment);
            }
        }

    }

    /**
     * 确定送达
     * @param orderId
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void confirm(Long orderId) {
        OrderMain orderMainDB = orderMainMapper.selectById(orderId);
        Long uid = LoginHelper.getUserId();
        User user = userMapper.selectById(uid);
        //校验用户权限
        if (!ObjectUtil.equals(orderMainDB.getUserId(),uid)
        && !LoginHelper.isAdmin(uid)) {
            throw new OrderException("没有该权限");
        }
        //校验订单状态
        if (!ObjectUtil.equals(orderMainDB.getStatus(),OrderStatus.DELIVERED.getCode())) {
            throw new OrderException("当前状态不可确认");
        }

        //更新主订单
        orderMainDB.setStatus(OrderStatus.COMPLETED.getCode());
        orderMainMapper.updateById(orderMainDB);
        //更新流程订单
        OrderProgress orderProgressDB = orderProgressMapper.selectById(orderId);
        orderProgressDB.setCompletedTime(LocalDateTime.now());
        orderProgressDB.setCompletedType(user.getUserType());
        orderProgressMapper.updateById(orderProgressDB);

        //收益分发
        School schoolDB = schoolMapper.selectById(orderMainDB.getSchoolId());
        Integer profitPlat = schoolDB.getProfitPlat(); //平台收益占比
        Integer profitAgent = schoolDB.getProfitAgent(); //代理收益占比
        Integer profitRunner = schoolDB.getProfitRunner(); //跑腿收益占比
        Integer totalProfit = profitRunner + profitAgent + profitPlat;
        //计算收益
        OrderPayment orderPaymentDB = orderPaymentMapper.selectById(orderId);
        BigDecimal totalAmount = orderMainDB.getTotalAmount();
        BigDecimal profitPlatAmount = BigDecimalUtils.multiply(totalAmount,
                BigDecimalUtils.divide(new BigDecimal(profitPlat),new BigDecimal(totalProfit)));
        BigDecimal profitAgentAmount = BigDecimalUtils.multiply(totalAmount,
                BigDecimalUtils.divide(new BigDecimal(profitAgent),new BigDecimal(totalProfit)));
        BigDecimal profitRunnerAmount = BigDecimalUtils.multiply(totalAmount,
                BigDecimalUtils.divide(new BigDecimal(profitRunner),new BigDecimal(totalProfit)));
        Long adminId = UserConstants.ADMIN_ID;
        Long runnerId = orderMainDB.getRunnerId();
        Long agentUid = schoolDB.getBelongUid();
        Wallet walletAdmin = walletMapper.selectById(adminId);
        Wallet walletRunner = walletMapper.selectById(runnerId);
        Wallet walletAgent = walletMapper.selectById(agentUid);
        walletAdmin.setBalance(BigDecimalUtils.add(walletAdmin.getBalance(),profitPlatAmount));
        walletAdmin.setUpdateTime(LocalDateTime.now());
        walletRunner.setBalance(BigDecimalUtils.add(walletRunner.getBalance(),profitRunnerAmount));
        walletRunner.setUpdateTime(LocalDateTime.now());
        walletAgent.setBalance(BigDecimalUtils.add(walletAgent.getBalance(),profitAgentAmount));
        walletAgent.setUpdateTime(LocalDateTime.now());
        walletMapper.updateById(walletAdmin);
        walletMapper.updateById(walletRunner);
        walletMapper.updateById(walletAgent);
        //插入资金流动表
        CapitalFlow capitalFlow = new CapitalFlow();
        capitalFlow.setOrderId(orderId);
        capitalFlow.setUserId(orderMainDB.getUserId());
        capitalFlow.setAgentId(agentUid);
        capitalFlow.setRunnerId(runnerId);
        capitalFlow.setProfitPlat(profitPlatAmount);
        capitalFlow.setProfitAgent(profitAgentAmount);
        capitalFlow.setProfitRunner(profitRunnerAmount);
        capitalFlow.setProfitUser(BigDecimalUtils.multiply(orderPaymentDB.getActualPayment(),new BigDecimal(-1)));
        capitalFlow.setType(CapitalType.ORDER_COMPLETE.getCode());
        capitalFlow.setCreateTime(LocalDateTime.now());
        capitalFlowMapper.insert(capitalFlow);

        //通知跑腿员
        List<WxMaSubscribeMessage.MsgData> msgData = wxHelper.buildOrderStatusData(
                orderId,
                orderMainDB.getTag(),
                OrderStatus.getString(OrderStatus.COMPLETED.getCode()),
                "用户已确认，该订单完成"
        );
        UserWx orderUser = userWxMapper.selectOne(new LambdaQueryWrapper<UserWx>().eq(UserWx::getUid, orderMainDB.getRunnerId()));
        wxHelper.sendSubMsg(
                msgData,
                WxHelper.PAGE_ORDER_RUNNER+"?id="+orderId,
                WxHelper.TEMPLATE_ORDER_STATUS_CHANGE,
                orderUser.getOpenid()
        );
    }
    @Override
    public void confirmTest(Long orderId) {
        OrderMain orderMainDB = orderMainMapper.selectById(orderId);
        Long uid = LoginHelper.getUserId();
        User user = userMapper.selectById(uid);
        //校验用户权限
        if (!ObjectUtil.equals(orderMainDB.getUserId(),uid)
                && !LoginHelper.isAdmin(uid)) {
            throw new OrderException("没有该权限");
        }
        //校验订单状态
        if (!ObjectUtil.equals(orderMainDB.getStatus(),OrderStatus.DELIVERED.getCode())) {
            throw new OrderException("当前状态不可确认");
        }

        //更新主订单
        orderMainDB.setStatus(OrderStatus.DELIVERED.getCode());
        orderMainMapper.updateById(orderMainDB);
        //更新流程订单
        OrderProgress orderProgressDB = orderProgressMapper.selectById(orderId);
        orderProgressDB.setCompletedTime(LocalDateTime.now());
        orderProgressDB.setCompletedType(user.getUserType());
        orderProgressMapper.updateById(orderProgressDB);
        //收益分发
        School schoolDB = schoolMapper.selectById(orderMainDB.getSchoolId());
        Integer profitPlat = schoolDB.getProfitPlat(); //平台收益占比
        Integer profitAgent = schoolDB.getProfitAgent(); //代理收益占比
        Integer profitRunner = schoolDB.getProfitRunner(); //跑腿收益占比
        Integer totalProfit = profitRunner + profitAgent + profitPlat;
        //计算收益
        OrderPayment orderPaymentDB = orderPaymentMapper.selectById(orderId);
        BigDecimal totalAmount = orderMainDB.getTotalAmount();
        BigDecimal profitPlatAmount = BigDecimalUtils.multiply(totalAmount,
                BigDecimalUtils.divide(new BigDecimal(profitPlat),new BigDecimal(totalProfit)));
        BigDecimal profitAgentAmount = BigDecimalUtils.multiply(totalAmount,
                BigDecimalUtils.divide(new BigDecimal(profitAgent),new BigDecimal(totalProfit)));
        BigDecimal profitRunnerAmount = BigDecimalUtils.multiply(totalAmount,
                BigDecimalUtils.divide(new BigDecimal(profitRunner),new BigDecimal(totalProfit)));
        Long adminId = UserConstants.ADMIN_ID;
        Long runnerId = orderMainDB.getRunnerId();
        Long agentUid = schoolDB.getBelongUid();
        Wallet walletAdmin = walletMapper.selectById(adminId);
        Wallet walletRunner = walletMapper.selectById(runnerId);
        Wallet walletAgent = walletMapper.selectById(agentUid);
        walletAdmin.setBalance(BigDecimalUtils.add(walletAdmin.getBalance(),profitPlatAmount));
        walletAdmin.setUpdateTime(LocalDateTime.now());
        walletRunner.setBalance(BigDecimalUtils.add(walletRunner.getBalance(),profitRunnerAmount));
        walletRunner.setUpdateTime(LocalDateTime.now());
        walletAgent.setBalance(BigDecimalUtils.add(walletAgent.getBalance(),profitAgentAmount));
        walletAgent.setUpdateTime(LocalDateTime.now());
        walletMapper.updateById(walletAdmin);
        walletMapper.updateById(walletRunner);
        walletMapper.updateById(walletAgent);
        //插入资金流动表
        CapitalFlow capitalFlow = new CapitalFlow();
        capitalFlow.setOrderId(orderId);
        capitalFlow.setUserId(orderMainDB.getUserId());
        capitalFlow.setAgentId(agentUid);
        capitalFlow.setRunnerId(runnerId);
        capitalFlow.setProfitPlat(profitPlatAmount);
        capitalFlow.setProfitAgent(profitAgentAmount);
        capitalFlow.setProfitRunner(profitRunnerAmount);
        capitalFlow.setProfitUser(BigDecimalUtils.multiply(orderPaymentDB.getActualPayment(),new BigDecimal(-1)));
        capitalFlow.setType(CapitalType.ORDER_COMPLETE.getCode());
        capitalFlow.setCreateTime(LocalDateTime.now());
        capitalFlowMapper.insert(capitalFlow);
        UserWx orderUser = userWxMapper.selectOne(new LambdaQueryWrapper<UserWx>().eq(UserWx::getUid, orderMainDB.getRunnerId()));
    }
    /**
     * 获取用户/跑腿员电话
     * @param orderId
     * @return
     */
    @Override
    public String phone(Long orderId) {
        OrderMain orderMainDB = orderMainMapper.selectById(orderId);
        Long uid = LoginHelper.getUserId();
        //用户获取跑腿员
        if (ObjectUtil.equals(orderMainDB.getUserId(),uid)) {
            UserWx runnerDB = userWxMapper.selectOne(new LambdaQueryWrapper<UserWx>().eq(UserWx::getUid, orderMainDB.getRunnerId()));
            return runnerDB.getPhone();
        }
        //跑腿员获取用户
        else if (ObjectUtil.equals(orderMainDB.getRunnerId(),uid)) {
            UserWx userDB = userWxMapper.selectOne(new LambdaQueryWrapper<UserWx>().eq(UserWx::getUid, orderMainDB.getUserId()));
            return userDB.getPhone();
        }
        else {
            throw new OrderException("没有该权限");
        }
    }

    /**
     * 订单聊天记录分页查询
     * @param pageQuery
     * @param orderId
     * @return
     */
    @Override
    public TableDataInfo<OrderChat> pageChat(PageQuery pageQuery, Long orderId) {
        LambdaQueryWrapper<OrderChat> lqm = new LambdaQueryWrapper<OrderChat>()
                .orderByDesc(OrderChat::getCreateTime)
                .eq(OrderChat::getOrderId, orderId)
                ;
        Page<OrderChat> orderChatPage = orderChatMapper.selectPage(pageQuery.build(), lqm);
        return TableDataInfo.build(orderChatPage);
    }

    /**
     * 取消订单前置操作
     * @param orderId
     * @return
     */
    @Override
    public String cancelbefore(Long orderId) {
        OrderMain orderMainDB = orderMainMapper.selectById(orderId);
        Long uid = LoginHelper.getUserId();
        String result = "";
        if (ObjectUtil.equals(orderMainDB.getUserId(),uid)) {
            //用户取消
            if (ObjectUtil.equals(orderMainDB.getStatus(),OrderStatus.DELIVERING.getCode())) {
                OrderProgress orderProgress = orderProgressMapper.selectById(orderId);
                BigDecimal bigDecimal = OrderCancelRule.calculateRefund(orderMainDB.getTotalAmount(), orderProgress.getAcceptedTime());
                result = "此时取消预计退款￥"+bigDecimal;
            }
        } else if (ObjectUtil.equals(orderMainDB.getRunnerId(), uid)) {
            //跑腿取消
            if (ObjectUtil.equals(orderMainDB.getStatus(),OrderStatus.DELIVERING.getCode())
            || ObjectUtil.equals(orderMainDB.getStatus(),OrderStatus.PENDING_DELIVER.getCode())) {
                result = "此时取消将会扣除"+fuuConfig.getCreditDeduction()+"信用分";
            }


        } else if (LoginHelper.isAdmin(uid)) {

        }
        else {
            throw new OrderException("没有该权限");
        }
        return result;
    }

    /**
     * 订单退款
     * @param orderId
     * @param amount
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void refund(Long orderId, BigDecimal amount) {
        OrderMain orderMainDB = orderMainMapper.selectById(orderId);
        OrderPayment orderPaymentDB = orderPaymentMapper.selectById(orderId);
        if (orderPaymentDB.getPaymentStatus().equals(PayStatus.REFUND_IN_PROGRESS)
        || orderPaymentDB.getPaymentStatus().equals(PayStatus.REFUNDED)) {
            throw new OrderException("订单已退款,不可退款");
        }
        if (orderPaymentDB.getActualPayment().compareTo(amount) < 0) {
            throw new OrderException("退款金额不能大于用户支付金额");
        }
        payService.refund(orderId,orderPaymentDB.getActualPayment(),amount);
        orderPaymentDB.setRefundPendingTime(LocalDateTime.now());
        orderPaymentDB.setPaymentStatus(PayStatus.REFUND_IN_PROGRESS.getCode());
        orderPaymentMapper.updateById(orderPaymentDB);
        //如果订单未关闭状态 则改为关闭
        if (!orderMainDB.getStatus().equals(OrderStatus.CLOSED.getCode())) {
            orderMainDB.setStatus(OrderStatus.CLOSED.getCode());
            orderMainMapper.updateById(orderMainDB);
        }
    }

    /**
     * 统计订单数据
     * @param statistics
     */
    @Override
    public void calculateOrderStatistics(StatisticsDaily statistics, LocalDate lastDay, LocalDate today) {
        List<OrderMain> orderMains = orderMainMapper.selectList(
                new LambdaQueryWrapper<OrderMain>()
                        .between(OrderMain::getCreateTime, lastDay, today)
        );
        //总订单量
        statistics.setTotalOrders(orderMains.size());
        //取消订单量
        Integer cancelOrderCounts = 0;
        //申诉订单量
        Integer appealOrderCounts = 0;
        //已完成订单量
        Integer completedOrderCounts = 0;
        //帮取送订单量
        Integer deliveryOrderCounts = 0;
        //帮买订单量
        Integer purchaseOrderCounts = 0;
        //万能帮订单量
        Integer universalOrderCounts = 0;
        for (OrderMain orderMain : orderMains) {
            Integer status = orderMain.getStatus();
            Integer serviceType = orderMain.getServiceType();
            if (ObjectUtil.equals(status,OrderStatus.CANCELED.getCode())) cancelOrderCounts++;
            if (ObjectUtil.equals(status,OrderStatus.CLOSED.getCode())) appealOrderCounts++;
            if (ObjectUtil.equals(status,OrderStatus.COMPLETED.getCode())) completedOrderCounts++;
            if (ObjectUtil.equals(serviceType,ServiceType.PICKUP_DELIVERY.getCode())) deliveryOrderCounts++;
            if (ObjectUtil.equals(serviceType,ServiceType.PURCHASE.getCode())) purchaseOrderCounts++;
            if (ObjectUtil.equals(serviceType,ServiceType.GENERAL_HELP.getCode())) universalOrderCounts++;
        }
        //完单率
        BigDecimal completionRate = new BigDecimal(0);
        if (completedOrderCounts != 0) {
            completionRate = BigDecimalUtils.divide(new BigDecimal(completedOrderCounts), new BigDecimal(orderMains.size()));
        }
        //帮取送订单占比(%)
        BigDecimal deliveryRate = new BigDecimal(0);
        if (deliveryOrderCounts != 0) {
            deliveryRate = BigDecimalUtils.divide(new BigDecimal(deliveryOrderCounts), new BigDecimal(orderMains.size()));
        }
        //帮买订单占比(%)
        BigDecimal purchaseRate = new BigDecimal(0);
        if (purchaseOrderCounts != 0) {
            purchaseRate = BigDecimalUtils.divide(new BigDecimal(purchaseOrderCounts), new BigDecimal(orderMains.size()));
        }
        //万能帮订单占比(%)
        BigDecimal universalRate = new BigDecimal(0);
        if (universalOrderCounts != 0) {
            universalRate = BigDecimalUtils.divide(new BigDecimal(universalOrderCounts), new BigDecimal(orderMains.size()));
        }
        statistics.setCanceledOrders(cancelOrderCounts);
        statistics.setAppealedOrders(appealOrderCounts);
        statistics.setCompletedOrders(completedOrderCounts);
        statistics.setDeliveryOrders(deliveryOrderCounts);
        statistics.setPurchaseOrders(purchaseOrderCounts);
        statistics.setUniversalOrders(universalOrderCounts);
        statistics.setCompletionRate(completionRate);
        statistics.setDeliveryRate(deliveryRate);
        statistics.setPurchaseRate(purchaseRate);
        statistics.setUniversalRate(universalRate);

    }

    /**
     * 统计金额数据
     * @param statistics
     */
    @Override
    public void calculateFinancialStatistics(StatisticsDaily statistics,LocalDate lastDay,LocalDate today) {
        List<OrderPayment> orderPayments = orderPaymentMapper.selectList(
                new LambdaQueryWrapper<OrderPayment>()
                        .between(OrderPayment::getPaymentTime, lastDay, today)
        );
        List<CapitalFlow> capitalFlows = capitalFlowMapper.selectList(
                new LambdaQueryWrapper<CapitalFlow>()
                        .between(CapitalFlow::getCreateTime, lastDay, today)
                        .eq(CapitalFlow::getType, CapitalType.ORDER_COMPLETE.getCode())
        );
        //总收款金额
        BigDecimal totalPayment = new BigDecimal(0);
        //总退款金额
        BigDecimal totalRefund = new BigDecimal(0);
        //平台利润
        BigDecimal platformProfit = new BigDecimal(0);
        //跑腿员利润
        BigDecimal runnerProfit = new BigDecimal(0);
        //代理商利润
        BigDecimal agentProfit = new BigDecimal(0);
        for (OrderPayment orderPayment : orderPayments) {
            if (ObjectUtil.equals(orderPayment.getPaymentStatus(),PayStatus.REFUNDED.getCode())) {
                totalRefund = BigDecimalUtils.add(totalRefund,orderPayment.getActualPayment());
            }
            totalPayment = BigDecimalUtils.add(totalPayment,orderPayment.getActualPayment());
        }
        for (CapitalFlow capitalFlow : capitalFlows) {
            if (ObjectUtil.isNotNull(capitalFlow.getRunnerId())) {
                runnerProfit = BigDecimalUtils.add(runnerProfit,capitalFlow.getProfitRunner());
            }
            if (ObjectUtil.isNotNull(capitalFlow.getAgentId())) {
                agentProfit = BigDecimalUtils.add(agentProfit,capitalFlow.getProfitAgent());
            }
            platformProfit = BigDecimalUtils.add(platformProfit,capitalFlow.getProfitPlat());
        }

        statistics.setTotalPayment(totalPayment);
        statistics.setTotalRefund(totalRefund);
        statistics.setPlatformProfit(platformProfit);
        statistics.setRunnerProfit(runnerProfit);
        statistics.setAgentProfit(agentProfit);
    }



    /**
     * 检查字段是否合法
     * @param orderSubmitDTO
     */
    private void checkOrdreFields(OrderSubmitDTO orderSubmitDTO) {


    }
}
