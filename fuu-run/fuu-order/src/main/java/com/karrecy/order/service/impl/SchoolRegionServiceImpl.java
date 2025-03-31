package com.karrecy.order.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.karrecy.order.domain.po.SchoolRegion;
import com.karrecy.order.domain.vo.RegionVO;
import com.karrecy.order.mapper.SchoolRegionMapper;
import com.karrecy.order.service.ISchoolRegionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 学校楼栋管理表 服务实现类
 * </p>
 */
@Service
@RequiredArgsConstructor
public class SchoolRegionServiceImpl extends ServiceImpl<SchoolRegionMapper, SchoolRegion> implements ISchoolRegionService {

    private final SchoolRegionMapper schoolRegionMapper;

    /**
     * 校区区域分页查询
     * @return
     */
    @Override
    public List<RegionVO> selectSchoolRegionListAll(Long schoolId) {
        LambdaQueryWrapper<SchoolRegion> lqwParents = new LambdaQueryWrapper<>();
        lqwParents.eq(ObjectUtil.isNotNull(schoolId), SchoolRegion::getSchoolId, schoolId)
                .eq(SchoolRegion::getType, 0)
                .orderByDesc(SchoolRegion::getCreateTime);

        LambdaQueryWrapper<SchoolRegion> lqwChildrens = new LambdaQueryWrapper<>();

        List<RegionVO> regionVOS = new ArrayList<>();
        List<SchoolRegion> schoolRegions = schoolRegionMapper.selectList(lqwParents);
        for (SchoolRegion schoolRegion : schoolRegions) {
            RegionVO regionVO = new RegionVO();
            regionVO.setId(schoolRegion.getId());
            regionVO.setSchoolId(schoolRegion.getSchoolId());
            regionVO.setType(schoolRegion.getType());
            regionVO.setName(schoolRegion.getName());
            regionVO.setRemark(schoolRegion.getRemark());
            regionVO.setCreateTime(schoolRegion.getCreateTime());
            regionVO.setUpdateTime(schoolRegion.getUpdateTime());
            regionVO.setCreateId(schoolRegion.getCreateId());
            regionVO.setUpdateId(schoolRegion.getUpdateId());
            lqwChildrens.eq(SchoolRegion::getParentId, schoolRegion.getId())
                    .orderByDesc(SchoolRegion::getCreateTime);
            List<SchoolRegion> schoolRegionChildrens = schoolRegionMapper.selectList(lqwChildrens);
            regionVO.setChildrens(schoolRegionChildrens);
            regionVOS.add(regionVO);
            lqwChildrens.clear();

        }
        return regionVOS;
    }

    /**
     * 校区区域列表查询
     * @param schoolRegion
     * @return
     */
    @Override
    public List<SchoolRegion> selectSchoolRegionList(SchoolRegion schoolRegion) {
        LambdaQueryWrapper<SchoolRegion> lqw = new LambdaQueryWrapper<SchoolRegion>()
                .eq(ObjectUtil.isNotNull(schoolRegion.getId()),SchoolRegion::getId,schoolRegion.getId())
                .eq(SchoolRegion::getSchoolId,schoolRegion.getSchoolId())
                .eq(ObjectUtil.isNotNull(schoolRegion.getType()),SchoolRegion::getType,schoolRegion.getType())
                .eq(ObjectUtil.isNotNull(schoolRegion.getParentId()),SchoolRegion::getParentId,schoolRegion.getParentId())
                .isNull(ObjectUtil.isNull(schoolRegion.getParentId()),SchoolRegion::getParentId)
                ;
        return schoolRegionMapper.selectList(lqw);
    }
}
