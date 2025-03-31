package com.karrecy.admin.controller.system;

import com.karrecy.common.core.controller.BaseController;
import com.karrecy.system.service.IUserPcService;
import com.karrecy.system.service.IUserService;
import com.karrecy.system.service.IUserWxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/system/user")
public class UserController extends BaseController {

    private final IUserService userService;
    private final IUserPcService userPcService;
    private final IUserWxService userWxService;




}
