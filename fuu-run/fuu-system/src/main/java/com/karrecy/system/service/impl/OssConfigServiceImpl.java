package com.karrecy.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.karrecy.common.constant.CacheNames;
import com.karrecy.common.core.domain.PageQuery;
import com.karrecy.common.core.page.TableDataInfo;
import com.karrecy.common.enums.Status;
import com.karrecy.common.exception.ServiceException;
import com.karrecy.common.utils.JsonUtils;
import com.karrecy.common.utils.StringUtils;
import com.karrecy.common.utils.redis.CacheUtils;
import com.karrecy.common.utils.redis.RedisUtils;
import com.karrecy.oss.constant.OssConstant;
import com.karrecy.system.domain.bo.OssConfigBo;
import com.karrecy.system.domain.po.OssConfig;
import com.karrecy.system.domain.vo.OssConfigVo;
import com.karrecy.system.mapper.OssConfigMapper;
import com.karrecy.system.service.OssConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

/**
 * 对象存储配置Service业务层处理
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class OssConfigServiceImpl implements OssConfigService {

    private final OssConfigMapper baseMapper;

    /**
     * 项目启动时，初始化参数到缓存，加载配置类
     */
    @Override
    public void init() {
        List<OssConfig> list = baseMapper.selectList();
        // 加载OSS初始化配置
        for (OssConfig config : list) {
            String configKey = config.getConfigKey();
            if (Status.OK.getCode().equals(config.getStatus())) {
                RedisUtils.setCacheObject(OssConstant.DEFAULT_CONFIG_KEY, configKey);
            }
            CacheUtils.put(CacheNames.SYS_OSS_CONFIG, config.getConfigKey(), JsonUtils.toJsonString(config));
        }
    }

    @Override
    public OssConfigVo queryById(Long ossConfigId) {
        return baseMapper.selectVoById(ossConfigId);
    }

    @Override
    public TableDataInfo<OssConfigVo> queryPageList(OssConfigBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<OssConfig> lqw = buildQueryWrapper(bo);
        Page<OssConfigVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }


    private LambdaQueryWrapper<OssConfig> buildQueryWrapper(OssConfigBo bo) {
        LambdaQueryWrapper<OssConfig> lqw = Wrappers.lambdaQuery();
        lqw.eq(StringUtils.isNotBlank(bo.getConfigKey()), OssConfig::getConfigKey, bo.getConfigKey());
        lqw.like(StringUtils.isNotBlank(bo.getBucketName()), OssConfig::getBucketName, bo.getBucketName());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), OssConfig::getStatus, bo.getStatus());
        return lqw;
    }

    @Override
    public Boolean insertByBo(OssConfigBo bo) {
        OssConfig config = BeanUtil.toBean(bo, OssConfig.class);
        validEntityBeforeSave(config);
        boolean flag = baseMapper.insert(config) > 0;
        if (flag) {
            CacheUtils.put(CacheNames.SYS_OSS_CONFIG, config.getConfigKey(), JsonUtils.toJsonString(config));
        }
        return flag;
    }

    @Override
    public Boolean updateByBo(OssConfigBo bo) {
        OssConfig config = BeanUtil.toBean(bo, OssConfig.class);
        validEntityBeforeSave(config);
        LambdaUpdateWrapper<OssConfig> luw = new LambdaUpdateWrapper<>();
        luw.set(ObjectUtil.isNull(config.getPrefix()), OssConfig::getPrefix, "");
        luw.set(ObjectUtil.isNull(config.getRegion()), OssConfig::getRegion, "");
        luw.set(ObjectUtil.isNull(config.getExt1()), OssConfig::getExt1, "");
        luw.set(ObjectUtil.isNull(config.getRemark()), OssConfig::getRemark, "");
        luw.eq(OssConfig::getOssConfigId, config.getOssConfigId());
        boolean flag = baseMapper.update(config, luw) > 0;
        if (flag) {
            CacheUtils.put(CacheNames.SYS_OSS_CONFIG, config.getConfigKey(), JsonUtils.toJsonString(config));
        }
        return flag;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(OssConfig entity) {
        if (StringUtils.isNotEmpty(entity.getConfigKey()) && !checkConfigKeyUnique(entity)) {
            throw new ServiceException("操作配置'" + entity.getConfigKey() + "'失败, 配置key已存在!");
        }
    }

    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if (isValid) {
            if (CollUtil.containsAny(ids, OssConstant.SYSTEM_DATA_IDS)) {
                throw new ServiceException("系统内置, 不可删除!");
            }
        }
        List<OssConfig> list = CollUtil.newArrayList();
        for (Long configId : ids) {
            OssConfig config = baseMapper.selectById(configId);
            list.add(config);
        }
        boolean flag = baseMapper.deleteBatchIds(ids) > 0;
        if (flag) {
            list.forEach(ossConfig ->
                CacheUtils.evict(CacheNames.SYS_OSS_CONFIG, ossConfig.getConfigKey()));
        }
        return flag;
    }

    /**
     * 判断configKey是否唯一
     */
    private boolean checkConfigKeyUnique(OssConfig ossConfig) {
        long ossConfigId = ObjectUtil.isNull(ossConfig.getOssConfigId()) ? -1L : ossConfig.getOssConfigId();
        OssConfig info = baseMapper.selectOne(new LambdaQueryWrapper<OssConfig>()
            .select(OssConfig::getOssConfigId, OssConfig::getConfigKey)
            .eq(OssConfig::getConfigKey, ossConfig.getConfigKey()));
        if (ObjectUtil.isNotNull(info) && info.getOssConfigId() != ossConfigId) {
            return false;
        }
        return true;
    }

    /**
     * 启用禁用状态
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateOssConfigStatus(OssConfigBo bo) {
        OssConfig ossConfig = BeanUtil.toBean(bo, OssConfig.class);
        int row = baseMapper.update(null, new LambdaUpdateWrapper<OssConfig>()
            .set(OssConfig::getStatus, Status.DISABLE.getCode()));
        row += baseMapper.updateById(ossConfig);
        if (row > 0) {
            RedisUtils.setCacheObject(OssConstant.DEFAULT_CONFIG_KEY, ossConfig.getConfigKey());
        }
        return row;
    }

}
