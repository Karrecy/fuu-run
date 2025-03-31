package com.karrecy.admin.controller.system;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.util.ObjectUtil;
import com.karrecy.common.core.controller.BaseController;
import com.karrecy.common.core.domain.PageQuery;
import com.karrecy.common.core.domain.R;
import com.karrecy.common.core.domain.entity.User;
import com.karrecy.common.core.domain.entity.UserWx;
import com.karrecy.common.core.domain.model.LoginUser;
import com.karrecy.common.core.page.TableDataInfo;
import com.karrecy.common.helper.LoginHelper;
import com.karrecy.order.domain.po.School;
import com.karrecy.order.service.ISchoolService;
import com.karrecy.system.domain.bo.UserWxQuery;
import com.karrecy.system.service.IUserPcService;
import com.karrecy.system.service.IUserService;
import com.karrecy.system.service.IUserWxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/system/userwx")
public class UserWxController extends BaseController {

    private final IUserService userService;
    private final IUserPcService userPcService;
    private final IUserWxService userWxService;
    private final ISchoolService schoolService;


    /**
     * 获取WX用户列表
     */
    @GetMapping("/list")
    @SaCheckPermission("system:userwx:list")
    public TableDataInfo<User> listWx(UserWxQuery userWxQuery, PageQuery pageQuery) {
        return userWxService.selectPageUserWxList(userWxQuery, pageQuery);
    }
    /**
     * 修改xcx用户
     * @return
     */
    @PutMapping("/edit")
    @SaCheckPermission("system:userwx:edit")
    public R<Void> editAgent(@Validated @RequestBody UserWx userWx) {
        userService.checkUserAllowed(userWx.getUid());
        User user = userService.selectUserByUid(userWx.getUid());
        if (ObjectUtil.isNull(user)) {
            return R.fail("用户不存在");
        }
        UserWx userWxDB = user.getUserWx();
        //校区管理员只能修改资金跑腿员
        LoginUser loginUser = LoginHelper.getLoginUser();
        if (LoginHelper.isSchoolAdmin()) {
            Long schoolId = userWxDB.getSchoolId();
            School schoolDB = schoolService.getById(schoolId);
            if (ObjectUtil.isNull(schoolDB) || !schoolDB.getBelongUid().equals(loginUser.getUid())) {
                return R.fail("无权限修改");
            }
        }
        userWxDB.setCanOrder(userWx.getCanOrder());
        userWxDB.setCanTake(userWx.getCanTake());
        return toAjax(userPcService.updateWxUser(userWxDB));
    }
}
