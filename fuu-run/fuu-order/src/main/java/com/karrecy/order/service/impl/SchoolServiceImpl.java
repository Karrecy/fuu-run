package com.karrecy.order.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.karrecy.common.core.domain.PageQuery;
import com.karrecy.common.core.page.TableDataInfo;
import com.karrecy.common.utils.StringUtils;
import com.karrecy.order.domain.po.School;
import com.karrecy.order.mapper.SchoolMapper;
import com.karrecy.order.service.ISchoolService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 */
@RequiredArgsConstructor
@Service
public class SchoolServiceImpl extends ServiceImpl<SchoolMapper, School> implements ISchoolService {

    private final SchoolMapper schoolMapper;

    /**
     * 校区分页查询
     * @param school
     * @param pageQuery
     * @return
     */
    @Override
    public TableDataInfo<School> selectPageSchoolList(School school, PageQuery pageQuery) {
        LambdaQueryWrapper<School> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ObjectUtil.isNotNull(school.getBelongUid()), School::getBelongUid, school.getBelongUid())
                .eq(ObjectUtil.isNotNull(school.getId()), School::getId, school.getId())
                .like(StringUtils.isNotBlank(school.getName()), School::getName, school.getName())
                .eq(ObjectUtil.isNotNull(school.getStatus()), School::getStatus, school.getStatus())
                ;
        Page<School> schoolPage = schoolMapper.selectPage(pageQuery.build(), lqw);
        return TableDataInfo.build(schoolPage);
    }
}
