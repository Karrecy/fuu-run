package com.karrecy.system.service;


import com.karrecy.common.core.domain.PageQuery;
import com.karrecy.common.core.page.TableDataInfo;
import com.karrecy.system.domain.bo.OssConfigBo;
import com.karrecy.system.domain.vo.OssConfigVo;

import java.util.Collection;

/**
 * 对象存储配置Service接口
 */
public interface OssConfigService {

    /**
     * 初始化OSS配置
     */
    void init();

    /**
     * 查询单个
     */
    OssConfigVo queryById(Long ossConfigId);

    /**
     * 查询列表
     */
    TableDataInfo<OssConfigVo> queryPageList(OssConfigBo bo, PageQuery pageQuery);


    /**
     * 根据新增业务对象插入对象存储配置
     *
     * @param bo 对象存储配置新增业务对象
     * @return
     */
    Boolean insertByBo(OssConfigBo bo);

    /**
     * 根据编辑业务对象修改对象存储配置
     *
     * @param bo 对象存储配置编辑业务对象
     * @return
     */
    Boolean updateByBo(OssConfigBo bo);

    /**
     * 校验并删除数据
     *
     * @param ids     主键集合
     * @param isValid 是否校验,true-删除前校验,false-不校验
     * @return
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);

    /**
     * 启用停用状态
     */
    int updateOssConfigStatus(OssConfigBo bo);

}
