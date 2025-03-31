package com.karrecy.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.karrecy.common.constant.CacheNames;
import com.karrecy.common.core.domain.PageQuery;
import com.karrecy.common.core.page.TableDataInfo;
import com.karrecy.common.core.service.OssCommonService;
import com.karrecy.common.exception.ServiceException;
import com.karrecy.common.helper.LoginHelper;
import com.karrecy.common.utils.SpringUtils;
import com.karrecy.common.utils.StringUtils;
import com.karrecy.common.utils.file.FileUtils;
import com.karrecy.oss.core.OssClient;
import com.karrecy.oss.entity.UploadResult;
import com.karrecy.oss.enumd.AccessPolicyType;
import com.karrecy.oss.factory.OssFactory;
import com.karrecy.system.domain.bo.OssBo;
import com.karrecy.system.domain.po.Oss;
import com.karrecy.system.domain.vo.OssVo;
import com.karrecy.system.mapper.OssMapper;
import com.karrecy.system.service.OssService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 文件上传 服务层实现
 */
@RequiredArgsConstructor
@Service
public class OssServiceImpl implements OssService, OssCommonService {

    private final OssMapper baseMapper;

    @Override
    public TableDataInfo<OssVo> queryPageList(OssBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<Oss> lqw = buildQueryWrapper(bo);
        Page<OssVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        List<OssVo> filterResult = result.getRecords().stream().map(this::matchingUrl).collect(Collectors.toList());
        result.setRecords(filterResult);
        return TableDataInfo.build(result);
    }

    @Override
    public List<OssVo> listByIds(Collection<Long> ossIds) {
        List<OssVo> list = new ArrayList<>();
        for (Long id : ossIds) {
            OssVo vo = SpringUtils.getAopProxy(this).getById(id);
            if (ObjectUtil.isNotNull(vo)) {
                try {
                    list.add(this.matchingUrl(vo));
                } catch (Exception ignored) {
                    // 如果oss异常无法连接则将数据直接返回
                    list.add(vo);
                }            }
        }
        return list;
    }

    @Override
    public String selectUrlByIds(String ossIds) {
        List<String> list = new ArrayList<>();
        for (Long id : StringUtils.splitTo(ossIds, Convert::toLong)) {
            OssVo vo = SpringUtils.getAopProxy(this).getById(id);
            if (ObjectUtil.isNotNull(vo)) {
                try {
                    list.add(this.matchingUrl(vo).getUrl());
                } catch (Exception ignored) {
                    // 如果oss异常无法连接则将数据直接返回
                    list.add(vo.getUrl());
                }
            }
        }
        return String.join(StringUtils.SEPARATOR, list);
    }

    private LambdaQueryWrapper<Oss> buildQueryWrapper(OssBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<Oss> lqw = Wrappers.lambdaQuery();
        lqw.like(StringUtils.isNotBlank(bo.getFileName()), Oss::getFileName, bo.getFileName());
        lqw.like(StringUtils.isNotBlank(bo.getOriginalName()), Oss::getOriginalName, bo.getOriginalName());
        lqw.eq(StringUtils.isNotBlank(bo.getFileSuffix()), Oss::getFileSuffix, bo.getFileSuffix());
        lqw.eq(StringUtils.isNotBlank(bo.getUrl()), Oss::getUrl, bo.getUrl());
        lqw.between(params.get("beginCreateTime") != null && params.get("endCreateTime") != null,
            Oss::getCreateTime, params.get("beginCreateTime"), params.get("endCreateTime"));
        lqw.eq(StringUtils.isNotBlank(bo.getCreateId()), Oss::getCreateId, bo.getCreateId());
        lqw.eq(StringUtils.isNotBlank(bo.getService()), Oss::getService, bo.getService());
        return lqw;
    }

    @Cacheable(cacheNames = CacheNames.SYS_OSS, key = "#ossId")
    @Override
    public OssVo getById(Long ossId) {
        return baseMapper.selectVoById(ossId);
    }

    @Override
    public void download(Long ossId, HttpServletResponse response) throws IOException {
        OssVo sysOss = getById(ossId);
        if (ObjectUtil.isNull(sysOss)) {
            throw new ServiceException("文件数据不存在!");
        }
        FileUtils.setAttachmentResponseHeader(response, sysOss.getOriginalName());
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE + "; charset=UTF-8");
        OssClient storage = OssFactory.instance(sysOss.getService());
        try(InputStream inputStream = storage.getObjectContent(sysOss.getUrl())) {
            int available = inputStream.available();
            IoUtil.copy(inputStream, response.getOutputStream(), available);
            response.setContentLength(available);
        } catch (Exception e) {
            throw new ServiceException(e.getMessage());
        }
    }

    @Override
    public OssVo upload(MultipartFile file, Integer type, String name) {
        String originalfileName = file.getOriginalFilename();
        String suffix = StringUtils.substring(originalfileName, originalfileName.lastIndexOf(".")+1, originalfileName.length());
        OssClient storage = OssFactory.instance();
        UploadResult uploadResult;
        try {
            uploadResult = storage.uploadSuffix(file.getBytes(), suffix, file.getContentType());
        } catch (IOException e) {
            throw new ServiceException(e.getMessage());
        }
        // 保存文件信息
        return buildResultEntity(originalfileName, suffix, storage.getConfigKey(), uploadResult, file.getSize(),type,name);
    }

    @Override
    public OssVo upload(File file) {
//        String originalfileName = file.getName();
//        String suffix = StringUtils.substring(originalfileName, originalfileName.lastIndexOf("."), originalfileName.length());
//        OssClient storage = OssFactory.instance();
//        UploadResult uploadResult = storage.uploadSuffix(file, suffix);
//        // 保存文件信息
//        return buildResultEntity(originalfileName, suffix, storage.getConfigKey(), uploadResult, file.length(), type);
        return null;
    }

    private OssVo buildResultEntity(String originalfileName, String suffix, String configKey, UploadResult uploadResult, long size, Integer type, String name) {
        Oss oss = new Oss();
        oss.setFileSize(size);
        oss.setType(type);
        oss.setUrl(uploadResult.getUrl());
        oss.setFileSuffix(suffix);
        oss.setFileName(uploadResult.getFilename());
        if (StringUtils.isNotBlank(name)) {
            oss.setOriginalName(name);
        }else {
            oss.setOriginalName(originalfileName);
        }
        oss.setService(configKey);
        Long uid = LoginHelper.getUserId();
        oss.setCreateId(uid);
        oss.setCreateTime(LocalDateTime.now());
        oss.setUpdateTime(LocalDateTime.now());
        oss.setUpdateId(uid);
        baseMapper.insert(oss);
        OssVo ossVo = BeanUtil.toBean(oss, OssVo.class);
        return this.matchingUrl(ossVo);
    }

    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if (isValid) {
            // 做一些业务上的校验,判断是否需要校验
        }
        List<Oss> list = baseMapper.selectBatchIds(ids);
        for (Oss oss : list) {
            OssClient storage = OssFactory.instance(oss.getService());
            storage.delete(oss.getUrl());
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }

    /**
     * 匹配Url
     *
     * @param oss OSS对象
     * @return oss 匹配Url的OSS对象
     */
    private OssVo matchingUrl(OssVo oss) {
        OssClient storage = OssFactory.instance(oss.getService());
        // 仅修改桶类型为 private 的URL，临时URL时长为120s
        if (AccessPolicyType.PRIVATE == storage.getAccessPolicy()) {
            oss.setUrl(storage.getPrivateUrl(oss.getFileName(), 120));
        }
        return oss;
    }
}
