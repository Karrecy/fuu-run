package com.karrecy.admin.controller.address;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.karrecy.common.core.controller.BaseController;
import com.karrecy.common.core.domain.PageQuery;
import com.karrecy.common.core.domain.R;
import com.karrecy.common.core.page.TableDataInfo;
import com.karrecy.order.domain.po.School;
import com.karrecy.order.service.ISchoolService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * 校区控制器
 * 处理应用程序的校区相关操作
 * @author Tao
 */
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/address/school")
public class SchoolController extends BaseController {

    private final ISchoolService schoolService;

    /**
     * 新增校区
     * @param school 包含校区详细信息的校区对象
     * @return 表示成功或失败的响应
     */
    @PostMapping("/add")
    @SaCheckPermission("address:school:add")
    public R<Void> addAgent(@Validated @RequestBody School school) {
        school.setCreateTime(LocalDateTime.now());
        school.setUpdateTime(LocalDateTime.now());
        return toAjax(schoolService.save(school));
    }

    /**
     * 修改校区
     * @param school 包含更新后校区详细信息的校区对象
     * @return 表示成功或失败的响应
     */
    @PutMapping("/edit")
    @SaCheckPermission("address:school:edit")
    public R<Void> editAgent(@Validated @RequestBody School school) {
        return toAjax(schoolService.updateById(school));
    }

    /**
     * 校区分页查询
     * @param school 包含查询参数的校区对象
     * @param pageQuery 包含分页详细信息的分页查询对象
     * @return 分页后的校区列表
     */
    @GetMapping("/list")
    @SaCheckPermission("address:school:list")
    public TableDataInfo<School> list(School school, PageQuery pageQuery) {
        return schoolService.selectPageSchoolList(school, pageQuery);
    }

    /**
     * 根据校区ID获取校区
     * @param id 要获取的校区ID
     * @return 包含校区详细信息的校区对象
     */
    @GetMapping("/{id}")
    @SaCheckPermission("address:school:get")
    public R<School> get(@PathVariable Long id) {
        return R.ok(schoolService.getById(id));
    }
}