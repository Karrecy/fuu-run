package com.karrecy.admin.controller.order;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.karrecy.common.core.controller.BaseController;
import com.karrecy.common.core.domain.PageQuery;
import com.karrecy.common.core.domain.R;
import com.karrecy.common.core.page.TableDataInfo;
import com.karrecy.common.helper.LoginHelper;
import com.karrecy.common.utils.StringUtils;
import com.karrecy.order.domain.po.School;
import com.karrecy.order.domain.po.Tags;
import com.karrecy.order.service.ISchoolService;
import com.karrecy.order.service.ITagsService;
import com.karrecy.system.service.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/order/tag")
public class TagController extends BaseController {

    private final ISchoolService schoolService;
    private final ITagsService tagsService;

    /**
     * 新增tag
     * @param tags
     * @return
     */
    @PostMapping("/add")
    @SaCheckPermission("order:tag:add")
    public R<Void> add(@Validated @RequestBody Tags tags) {
        Long uid = getUserId();
        tags.setCreateId(uid);
        tags.setCreateTime(LocalDateTime.now());
        tags.setUpdateId(uid);
        tags.setUpdateTime(LocalDateTime.now());
        if (LoginHelper.isSchoolAdmin()) {
            School school = getAgentSchool();
            tags.setSchoolId(school.getId());
        }
        return toAjax(tagsService.save(tags));
    }

    /**
     * 修改tag
     * @param tags
     * @return
     */
    @PutMapping("/edit")
    @SaCheckPermission("order:tag:edit")
    public R<Void> edit(@Validated @RequestBody Tags tags) {
        Long uid = getUserId();
        tags.setUpdateId(uid);
        tags.setUpdateTime(LocalDateTime.now());
        if (LoginHelper.isSchoolAdmin()) {
            School school = getAgentSchool();
            if (!school.getId().equals(tags.getSchoolId())) {
                return R.fail("无权限修改该校区的标签");
            }
        }
        return toAjax(tagsService.updateById(tags));
    }

    /**
     * tag分页查询
     */
    @GetMapping("/list")
    @SaCheckPermission("order:tag:list")
    public TableDataInfo<Tags> list(Tags tags, PageQuery pageQuery) {
        LambdaQueryWrapper<Tags> eq = new LambdaQueryWrapper<Tags>()
                .like(StringUtils.isNotBlank(tags.getName()), Tags::getName, tags.getName())
                .eq(ObjectUtil.isNotNull(tags.getServiceType()), Tags::getServiceType, tags.getServiceType())
                .eq(ObjectUtil.isNotNull(tags.getId()), Tags::getId, tags.getId())
                .eq(ObjectUtil.isNotNull(tags.getSchoolId()), Tags::getSchoolId, tags.getSchoolId());
        //如果是校区管理员就只能看到自己校区的
        if (LoginHelper.isSchoolAdmin()) {
            School school = getAgentSchool();
            eq.eq(Tags::getSchoolId,school.getId());
        }
        Map<String, Object> params = tags.getParams();
        if (params.get("createTimeBegin") != null) {
            eq.between(Tags::getCreateTime, params.get("createTimeBegin"), params.get("createTimeEnd"));
        }
        if (params.get("updateTimeBegin") != null) {
            eq.between(Tags::getUpdateTime, params.get("updateTimeBegin"), params.get("updateTimeEnd"));
        }
        Page<Tags> page = tagsService.page(
                pageQuery.build(),
                eq
        );
        return TableDataInfo.build(page);
    }

    /**
     * 根据school和ordertype查询list
     */
    @GetMapping("/list/user")
    @SaCheckPermission("order:tag:list:user")
    public R<List<Tags>> get(Long schoolId, Integer serviceType) {
        LambdaQueryWrapper<Tags> lqm = new LambdaQueryWrapper<Tags>()
                .eq(Tags::getSchoolId, schoolId)
                .eq(Tags::getServiceType, serviceType);
        return R.ok(tagsService.list(lqm));
    }

    /**
     * 删除tags
     *
     * @param tagIds tagsID
     */
    @DeleteMapping("/{schoolRegionIds}")
    @SaCheckPermission("order:tag:delete")
    public R<Void> remove(@PathVariable Long[] tagIds) {
        List<Long> ids = Arrays.asList(tagIds);
        return toAjax(tagsService.removeBatchByIds(ids));
    }


    private School getAgentSchool() {
        School one = schoolService.getOne(new LambdaQueryWrapper<School>().eq(School::getBelongUid, LoginHelper.getUserId()));
        if (ObjectUtil.isNull(one)) {
            throw new RuntimeException("当前无代理校区，没有权限");
        }
        return one;
    }
}