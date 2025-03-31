package com.karrecy.common.core.service;

/**
 * 通用 OSS服务
 */
public interface OssCommonService {

    /**
     * 通过ossId查询对应的url
     *
     * @param ossIds ossId串逗号分隔
     * @return url串逗号分隔
     */
    String selectUrlByIds(String ossIds);

}
