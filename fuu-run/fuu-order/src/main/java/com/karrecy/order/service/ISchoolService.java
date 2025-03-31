package com.karrecy.order.service;

import com.karrecy.common.core.domain.PageQuery;
import com.karrecy.common.core.page.TableDataInfo;
import com.karrecy.order.domain.po.School;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 */
public interface ISchoolService extends IService<School> {

    /**
     * 校区分页查询
     * @param school
     * @param pageQuery
     * @return
     */
    TableDataInfo<School> selectPageSchoolList(School school, PageQuery pageQuery);
}
