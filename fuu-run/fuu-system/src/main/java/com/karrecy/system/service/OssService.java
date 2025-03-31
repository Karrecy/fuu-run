package com.karrecy.system.service;


import com.karrecy.common.core.domain.PageQuery;
import com.karrecy.common.core.page.TableDataInfo;
import com.karrecy.system.domain.bo.OssBo;
import com.karrecy.system.domain.vo.OssVo;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

/**
 * 文件上传 服务层
 */
public interface OssService {

    TableDataInfo<OssVo> queryPageList(OssBo sysOss, PageQuery pageQuery);

    List<OssVo> listByIds(Collection<Long> ossIds);

    OssVo getById(Long ossId);

    OssVo upload(MultipartFile file, Integer type, String name);

    OssVo upload(File file);

    void download(Long ossId, HttpServletResponse response) throws IOException;

    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);

}
