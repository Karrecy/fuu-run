package com.karrecy.admin.controller.system;

import cn.binarywang.wx.miniapp.bean.WxMaSubscribeMessage;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.annotation.SaMode;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.karrecy.common.config.FuuConfig;
import com.karrecy.common.constant.CacheNames;
import com.karrecy.common.core.controller.BaseController;
import com.karrecy.common.core.domain.PageQuery;
import com.karrecy.common.core.domain.R;
import com.karrecy.common.core.domain.entity.StatisticsDaily;
import com.karrecy.common.core.domain.entity.User;
import com.karrecy.common.core.domain.model.EmailBody;
import com.karrecy.common.core.domain.model.LoginBody;
import com.karrecy.common.core.domain.model.LoginUser;
import com.karrecy.common.core.page.TableDataInfo;
import com.karrecy.common.core.service.CarouselImageService;
import com.karrecy.common.enums.DeviceType;
import com.karrecy.common.enums.Status;
import com.karrecy.common.enums.UserType;
import com.karrecy.common.helper.LoginHelper;
import com.karrecy.common.utils.redis.RedisUtils;
import com.karrecy.common.utils.wx.WxHelper;
import com.karrecy.order.domain.po.School;
import com.karrecy.order.service.ISchoolService;
import com.karrecy.system.service.IStatisticsDailyService;
import com.karrecy.system.service.IUserService;
import com.karrecy.system.service.LoginService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * 登录方法
 */
@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
public class LoginController extends BaseController {

    private final LoginService loginService;
    private final IUserService userService;
    private final ISchoolService schoolService;
    private final IStatisticsDailyService statisticsDailyService;
    private final FuuConfig fuuConfig;
    private final CarouselImageService carouselImageService;
    private final WxHelper wxHelper;

    /**
     * 登录方法
     * @param loginBody 登录信息
     * @return 结果
     */
    @PostMapping("/login")
    @SaIgnore
    public R<Map<String, Object>> login(@Validated @RequestBody LoginBody loginBody) {
        log.info(loginBody.toString());
        String token = loginService.pcLogin(loginBody);
        Map<String, Object> map = new HashMap<>();
        map.put("token", token);
        return R.ok(map);
    }

    /**
     * 登录方法
     * @param emailBody 登录信息
     * @return 结果
     */
    @PostMapping("/emailLogin")
    @SaIgnore
    public R<Map<String, Object>> emaillogin(@Validated @RequestBody EmailBody emailBody) {
        log.info(emailBody.toString());
        String token = loginService.emailLogin(emailBody);
        Map<String, Object> map = new HashMap<>();
        map.put("token", token);
        return R.ok(map);
    }


    /**
     * 登录方法
     * @param xcxCode 登录信息
     * @return 结果
     */
    @PostMapping("/xcxLogin")
    @SaIgnore
    public R xcxLogin(@NotBlank(message = "{xcx.code.not.blank}") String xcxCode) {
        log.info(xcxCode);
        String token = loginService.xcxLogin(xcxCode);
        Map<String, Object> map = new HashMap<>();
        map.put("token", token);
        return R.ok(map);
    }

    /**
     * 小程序检查是否需要登录
     * @return 结果
     */
    @GetMapping("/xcxCheckLogin")
    @SaIgnore
    public R xcxCheckLogin() {
        boolean login = StpUtil.isLogin();
        System.out.println(StpUtil.getLoginId());
        if (login) {
            long tokenTimeout = StpUtil.getTokenTimeout();
            if (tokenTimeout > 3600) {
                return R.ok(true);
            }
        }
        return R.ok(false);
    }

    /**
     * 获取用户信息
     *
     * @return 用户信息
     */
    @GetMapping("getInfo")
    @SaCheckPermission(value = {"getInfo","*:*:*"},mode = SaMode.OR)
    public R<Map<String, Object>> getInfo() {
        LoginUser loginUser = LoginHelper.getLoginUser();
        User user = userService.selectUserByUid(loginUser.getUid());
        if (user.getDeviceType().equals(DeviceType.PC.getCode())) {
            if (user.getUserType().equals(UserType.SCHOOL_AGENT.getCode())) {
                School one = schoolService.getOne(new LambdaQueryWrapper<School>().eq(School::getBelongUid, user.getUid()));
                user.getUserPc().setAgentSchool(ObjectUtil.isNotNull(one) ? one.getName() : "暂无代理校区");
                user.getUserPc().setAgentSchoolId(ObjectUtil.isNotNull(one) ? one.getId() : null);
            }

        } else if (user.getDeviceType().equals(DeviceType.WX.getCode())) {
            if (ObjectUtil.equals(user.getUserWx().getIsRunner(), Status.OK.getCode())) {
                //如果是跑腿员就查找学校名称
                School schoolDB = schoolService.getById(user.getUserWx().getSchoolId());
                user.getUserWx().setSchoolName(schoolDB.getName());
            }
        }

        Map<String, Object> ajax = new HashMap<>();
        ajax.put("user", user);
        ajax.put("permissions", loginUser.getMenuPermission());
        ajax.put("config",fuuConfig);

        //记录访问
        LocalDate now = LocalDate.now();
        RedisUtils.incrAtomicValue(CacheNames.DAILY_TOTAL_VISITS + now);
        RedisUtils.recordUV(now.toString(), String.valueOf(loginUser.getUid()));
        return R.ok(ajax);
    }

    @GetMapping("/statistic")
    @SaCheckPermission(value = {"getInfo","*:*:*"},mode = SaMode.OR)
    public TableDataInfo<StatisticsDaily> statistic(StatisticsDaily statisticsDaily, PageQuery pageQuery) {
        Page<StatisticsDaily> page = statisticsDailyService.page(
                pageQuery.build(),
                new LambdaQueryWrapper<StatisticsDaily>()
                        .orderByDesc(StatisticsDaily::getCreateTime)
                        .between(ObjectUtil.isNotNull(statisticsDaily.getBeginTime()) && ObjectUtil.isNotNull(statisticsDaily.getEndTime()),
                                StatisticsDaily::getCreateTime, statisticsDaily.getBeginTime(), statisticsDaily.getEndTime())
        );
        return TableDataInfo.build(page);
    }


    @GetMapping("/agreement")
    @SaIgnore
    public R<Object> getAgreement() throws Exception{
        Resource resource = new ClassPathResource("static/agreement.html");
        InputStream inputStream = resource.getInputStream();
        byte[] bytes = new byte[inputStream.available()];
        inputStream.read(bytes);
        String content = new String(bytes, StandardCharsets.UTF_8);
        return R.ok((Object) content);
    }
    @GetMapping("/carousel")
    @SaIgnore
    public R<List<String>> getCarouselImages() {
        List<String> images = carouselImageService.getCarouselImages();
        return R.ok(images);
    }
    @PostMapping("/carousel")
    @SaCheckPermission("system:system:carousel:add")
    public R<Void> addCarouselImage(@RequestBody Map<String, String> request) {
        String base64Content = request.get("base64Content");
        carouselImageService.addCarouselImage(base64Content);
        return R.ok();
    }
    @DeleteMapping("/carousel/{index}")
    @SaCheckPermission("system:system:carousel:del")
    public R<Void> deleteCarouselImage(@PathVariable int index) {
        carouselImageService.deleteCarouselImage(index);
        return R.ok();
    }
    /**
     * 修改指定索引的轮播图图片
     * 请求体应为 JSON 格式，例如：
     * {
     *   "base64Content": "新的图片Base64编码，不包含前缀"
     * }
     */
    @PutMapping("/carousel/{index}")
    @SaCheckPermission("system:system:carousel:update")
    public R<Void> updateCarouselImage(@PathVariable int index,
                                                    @RequestBody Map<String, String> request) {
        String base64Content = request.get("base64Content");
        carouselImageService.updateCarouselImage(index, base64Content);
        return R.ok();
    }
}
