package com.karrecy.admin.controller.system;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.karrecy.common.core.controller.BaseController;
import com.karrecy.common.core.domain.PageQuery;
import com.karrecy.common.core.domain.R;
import com.karrecy.common.core.page.TableDataInfo;
import com.karrecy.common.core.validate.AddGroup;
import com.karrecy.common.core.validate.EditGroup;
import com.karrecy.order.domain.po.RunnerApply;
import com.karrecy.order.service.IRunnerApplyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/system/runnerApply")
public class RunnerApplyController extends BaseController {

    private final IRunnerApplyService runnerApplyService;


    /**
     * 跑腿申请分页查询
     */
    @GetMapping("/list")
    @SaCheckPermission("system:runnerApply:list")
    public TableDataInfo<RunnerApply> list(RunnerApply runnerApply, PageQuery pageQuery) {
        return runnerApplyService.selectPageRunnerApplyList(runnerApply, pageQuery);
    }

    /**
     * 跑腿申请
     * @param runnerApply
     * @return
     */
    @PostMapping("/submit")
    @SaCheckPermission("system:runnerApply:submit")
    public R<Void> submit(@Validated(AddGroup.class) @RequestBody RunnerApply runnerApply) {
        runnerApplyService.apply(runnerApply);

        return R.ok();
    }

    /**
     * 处理申请
     * @param runnerApply
     * @return
     */
    @PutMapping("/edit")
    @SaCheckPermission("system:runnerApply:edit")
    public R<Void> edit(@Validated(EditGroup.class)@RequestBody RunnerApply runnerApply) {
        runnerApplyService.handle(runnerApply);
        return R.ok();
    }

    /**
     * 查询自己的申请进度
     */
    @GetMapping("/process")
    @SaCheckPermission("system:runnerApply:process")
    public R<List<RunnerApply>> process() {
        return R.ok(runnerApplyService.list(new LambdaQueryWrapper<RunnerApply>()
                .eq(RunnerApply::getUid,getUserId())
                .orderByDesc(RunnerApply::getCreateTime)));
    }



}
