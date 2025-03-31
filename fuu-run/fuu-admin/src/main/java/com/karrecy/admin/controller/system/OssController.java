package com.karrecy.admin.controller.system;


import cn.dev33.satoken.annotation.SaCheckPermission;
import com.karrecy.common.core.controller.BaseController;
import com.karrecy.common.core.domain.PageQuery;
import com.karrecy.common.core.domain.R;
import com.karrecy.common.core.page.TableDataInfo;
import com.karrecy.common.core.validate.QueryGroup;
import com.karrecy.system.domain.bo.OssBo;
import com.karrecy.system.domain.vo.OssVo;
import com.karrecy.system.service.OssService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 文件上传 控制层
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/oss")
public class OssController extends BaseController {

    private final OssService iSysOssService;

    /**
     * 查询OSS对象存储列表
     */
    @GetMapping("/list")
    @SaCheckPermission("system:oss:list")
    public TableDataInfo<OssVo> list(@Validated(QueryGroup.class) OssBo bo, PageQuery pageQuery) {
        return iSysOssService.queryPageList(bo, pageQuery);
    }
//    /**
//     * 查询OSS对象基于id串
//     *
//     * @param ossIds OSS对象ID串
//     */
//    @GetMapping("/listByIds/{ossIds}")
//    @SaCheckPermission("system:oss:list")
//    public R<List<OssVo>> listByIds(@NotEmpty(message = "主键不能为空")
//                                       @PathVariable Long[] ossIds) {
//        List<OssVo> list = iSysOssService.listByIds(Arrays.asList(ossIds));
//        return R.ok(list);
//    }

    /**
     * 上传OSS对象存储
     *
     * @param file 文件
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @SaCheckPermission("system:oss:upload")
    public R<Map<String, String>> upload(
            @RequestPart("file") MultipartFile file,
            @RequestParam("type") Integer type,
            @RequestParam("name") String name
    ) {
        if (file.getSize() == 0) {
            return R.fail("上传文件不能为空");
        }

        OssVo oss = iSysOssService.upload(file,type,name);
        Map<String, String> map = new HashMap<>(2);
        map.put("url", oss.getUrl());
        map.put("fileName", oss.getOriginalName());
        map.put("ossId", oss.getOssId().toString());
        map.put("fileSize", oss.getFileSize().toString());
        return R.ok(map);
    }

    /**
     * 下载OSS对象
     *
     * @param ossId OSS对象ID
     */
    @GetMapping("/download/{ossId}")
    @SaCheckPermission("system:oss:download")
    public void download(@PathVariable Long ossId, HttpServletResponse response) throws IOException {
        iSysOssService.download(ossId,response);
    }

    /**
     * 删除OSS对象存储
     *
     * @param ossIds OSS对象ID串
     */
    @DeleteMapping("/{ossIds}")
    @SaCheckPermission("system:oss:remove")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ossIds) {
        return toAjax(iSysOssService.deleteWithValidByIds(Arrays.asList(ossIds), true));
    }

}
