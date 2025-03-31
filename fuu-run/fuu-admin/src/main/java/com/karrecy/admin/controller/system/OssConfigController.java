package com.karrecy.admin.controller.system;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.karrecy.common.core.controller.BaseController;
import com.karrecy.common.core.domain.PageQuery;
import com.karrecy.common.core.domain.R;
import com.karrecy.common.core.page.TableDataInfo;
import com.karrecy.common.core.validate.AddGroup;
import com.karrecy.common.core.validate.EditGroup;
import com.karrecy.common.core.validate.QueryGroup;
import com.karrecy.system.domain.bo.OssConfigBo;
import com.karrecy.system.domain.vo.OssConfigVo;
import com.karrecy.system.service.OssConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Arrays;

/**
 * 对象存储配置
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/oss/config")
public class OssConfigController extends BaseController {

    private final OssConfigService ossConfigService;

    /**
     * 查询对象存储配置列表
     */
    @GetMapping("/list")
    @SaCheckPermission("system:oss:config:list")
    public TableDataInfo<OssConfigVo> list(@Validated(QueryGroup.class) OssConfigBo bo, PageQuery pageQuery) {
        return ossConfigService.queryPageList(bo, pageQuery);
    }

    /**
     * 获取对象存储配置详细信息
     *
     * @param ossConfigId OSS配置ID
     */
    @GetMapping("/{ossConfigId}")
    @SaCheckPermission("system:oss:config:get")
    public R<OssConfigVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long ossConfigId) {
        return R.ok(ossConfigService.queryById(ossConfigId));
    }

    /**
     * 新增对象存储配置
     */
    @PostMapping
    @SaCheckPermission("system:oss:config:add")
    public R<Void> add(@Validated(AddGroup.class) @RequestBody OssConfigBo bo) {
        return toAjax(ossConfigService.insertByBo(bo));
    }

    /**
     * 修改对象存储配置
     */
    @PutMapping
    @SaCheckPermission("system:oss:config:edit")
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody OssConfigBo bo) {
        return toAjax(ossConfigService.updateByBo(bo));
    }

    /**
     * 删除对象存储配置
     *
     * @param ossConfigIds OSS配置ID串
     */
    @DeleteMapping("/{ossConfigIds}")
    @SaCheckPermission("system:oss:config:delete")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ossConfigIds) {
        return toAjax(ossConfigService.deleteWithValidByIds(Arrays.asList(ossConfigIds), true));
    }

    /**
     * 状态修改
     */
    @PutMapping("/changeStatus")
    @SaCheckPermission("system:oss:config:changeStatus")
    public R<Void> changeStatus(@RequestBody OssConfigBo bo) {
        return toAjax(ossConfigService.updateOssConfigStatus(bo));
    }
}
