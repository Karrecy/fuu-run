package com.karrecy.admin.controller.order;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.util.ObjectUtil;
import com.karrecy.common.constant.UserConstants;
import com.karrecy.common.core.controller.BaseController;
import com.karrecy.common.core.domain.PageQuery;
import com.karrecy.common.core.domain.R;
import com.karrecy.common.core.domain.entity.UserPc;
import com.karrecy.common.core.domain.entity.UserWx;
import com.karrecy.common.core.page.TableDataInfo;
import com.karrecy.order.domain.po.OrderChat;
import com.karrecy.order.domain.po.OrderMain;
import com.karrecy.order.domain.po.School;
import com.karrecy.order.service.IOrderMainService;
import com.karrecy.order.service.ISchoolService;
import com.karrecy.system.service.IUserPcService;
import com.karrecy.system.service.IUserWxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 订单聊天控制器
 * 处理应用程序的订单聊天相关操作
 * @author Tao
 */
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/order/chat")
public class OrderChatController extends BaseController {

    private final ISchoolService schoolService;
    private final IOrderMainService orderMainService;
    private final IUserWxService userWxService;
    private final IUserPcService userPcService;

    /**
     * 订单聊天记录分页查询
     * @param orderId 订单ID
     * @param pageQuery 包含分页详细信息的分页查询对象
     * @return 分页后的订单聊天记录列表
     */
    @GetMapping("/list/{orderId}")
    @SaCheckPermission("order:chat:list")
    public TableDataInfo<OrderChat> pageChat(@PathVariable Long orderId, PageQuery pageQuery) {
        return orderMainService.pageChat(pageQuery, orderId);
    }

    /**
     * 获取聊天展示信息
     * @param orderId 订单ID
     * @return 包含聊天展示信息的响应
     */
    @GetMapping("/initchat/{orderId}")
    @SaCheckPermission("order:chat:initchat")
    public R<Map<String, Object>> initchat(@PathVariable Long orderId) {
        OrderMain orderMain = orderMainService.getById(orderId);
        School school = schoolService.getById(orderMain.getSchoolId());
        UserPc agent = userPcService.getByUid(school.getBelongUid());
        UserPc admin = userPcService.getByUid(UserConstants.ADMIN_ID);
        UserWx runner = userWxService.getByUid(orderMain.getRunnerId());
        UserWx user = userWxService.getByUid(orderMain.getUserId());

        Map<String, Object> map = new HashMap<>();
        if (ObjectUtil.isNotNull(school)) {
            map.put("schoolId", school.getId());
            map.put("schoolName", school.getName());
            map.put("schoolLogo", school.getLogo());
        }
        if (ObjectUtil.isNotNull(agent)) {
            map.put("agentId", agent.getUid());
            map.put("agentName", agent.getName());
            map.put("agentAvatar", agent.getAvatar());
        }
        if (ObjectUtil.isNotNull(admin)) {
            map.put("adminId", admin.getUid());
            map.put("adminName", admin.getName());
            map.put("adminAvatar", admin.getAvatar());
        }
        if (ObjectUtil.isNotNull(runner)) {
            map.put("runnerId", orderMain.getRunnerId());
            map.put("runnerName", runner.getNickname());
            map.put("runnerAvatar", runner.getAvatar());
        }
        if (ObjectUtil.isNotNull(user)) {
            map.put("userId", orderMain.getUserId());
            map.put("userName", user.getNickname());
            map.put("userAvatar", user.getAvatar());
        }

        return R.ok(map);
    }
}