package com.karrecy.admin.controller.address;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.karrecy.common.core.controller.BaseController;
import com.karrecy.common.core.domain.R;
import com.karrecy.common.helper.LoginHelper;
import com.karrecy.order.domain.po.School;
import com.karrecy.order.domain.po.SchoolRegion;
import com.karrecy.order.domain.vo.RegionVO;
import com.karrecy.order.service.ISchoolRegionService;
import com.karrecy.order.service.ISchoolService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 校区区域控制器
 * 处理应用程序的校区区域相关操作
 * @author Tao
 */
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/address/region")
public class RegionController extends BaseController {

    private final ISchoolService schoolService;
    private final ISchoolRegionService schoolRegionService;

    /**
     * 新增校区区域
     * @param schoolRegion 包含校区区域详细信息的校区区域对象
     * @return 表示成功或失败的响应
     */
    @PostMapping("/add")
    @SaCheckPermission("address:region:add")
    public R<Void> add(@Validated @RequestBody SchoolRegion schoolRegion) {
        if (LoginHelper.isSchoolAdmin()) {
            School school = getAgentSchool();
            schoolRegion.setSchoolId(school.getId());
        }
        schoolRegion.setCreateTime(LocalDateTime.now());
        schoolRegion.setUpdateTime(LocalDateTime.now());
        schoolRegion.setCreateId(getUserId());
        schoolRegion.setUpdateId(getUserId());
        return toAjax(schoolRegionService.save(schoolRegion));
    }

    /**
     * 修改校区区域
     * @param schoolRegion 包含更新后校区区域详细信息的校区区域对象
     * @return 表示成功或失败的响应
     */
    @PutMapping("/edit")
    @SaCheckPermission("address:region:edit")
    public R<Void> edit(@Validated @RequestBody SchoolRegion schoolRegion) {
        if (LoginHelper.isSchoolAdmin()) {
            School agentSchool = getAgentSchool();
            if (!agentSchool.getId().equals(schoolRegion.getSchoolId())) {
                return R.fail("没有权限");
            }
        }
        return toAjax(schoolRegionService.updateById(schoolRegion));
    }

    /**
     * 用户校区区域分页查询
     * @param schoolId 校区ID
     * @return 分页后的校区区域列表
     */
    @GetMapping("/list/{schoolId}")
    @SaCheckPermission("address:region:list")
    public R<List<RegionVO>> list(@PathVariable Long schoolId) {
        if (LoginHelper.isSchoolAdmin()) {
            School school = getAgentSchool();
            schoolId = school.getId();
        }
        return R.ok(schoolRegionService.selectSchoolRegionListAll(schoolId));
    }

    /**
     * 校区区域列表查询
     * @param schoolRegion 包含查询参数的校区区域对象
     * @return 校区区域列表
     */
    @GetMapping("/list/user")
    @SaCheckPermission("address:region:list:user")
    public R<List<SchoolRegion>> listUser(SchoolRegion schoolRegion) {

        List<SchoolRegion> schoolRegions = schoolRegionService.selectSchoolRegionList(schoolRegion);
        return R.ok(schoolRegions);
    }

    /**
     * 删除校区区域
     * @param schoolRegionIds 要删除的校区区域ID数组
     * @return 表示成功或失败的响应
     */
    @DeleteMapping("/{schoolRegionIds}")
    @SaCheckPermission("address:region:delete")
    public R<Void> remove(@PathVariable Long[] schoolRegionIds) {
        List<Long> ids = Arrays.asList(schoolRegionIds);
        for (Long schoolRegionId : schoolRegionIds) {
            List<SchoolRegion> list = schoolRegionService.list(new LambdaQueryWrapper<SchoolRegion>()
                    .eq(SchoolRegion::getParentId, schoolRegionId));
            if (!list.isEmpty()) {
                schoolRegionService.removeBatchByIds(list.stream().map(SchoolRegion::getId).collect(Collectors.toList()));
            }
        }
        return toAjax(schoolRegionService.removeBatchByIds(ids));
    }

    private School getAgentSchool() {
        School one = schoolService.getOne(new LambdaQueryWrapper<School>().eq(School::getBelongUid, LoginHelper.getUserId()));
        if (ObjectUtil.isNull(one)) {
            throw new RuntimeException("当前无代理校区，没有权限");
        }
        return one;
    }
}