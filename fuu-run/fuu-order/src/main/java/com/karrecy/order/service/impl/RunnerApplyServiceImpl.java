package com.karrecy.order.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.karrecy.common.core.domain.PageQuery;
import com.karrecy.common.core.domain.entity.User;
import com.karrecy.common.core.domain.entity.UserPc;
import com.karrecy.common.core.domain.entity.UserWx;
import com.karrecy.common.core.page.TableDataInfo;
import com.karrecy.common.core.service.EmailService;
import com.karrecy.common.enums.Status;
import com.karrecy.common.enums.UserType;
import com.karrecy.common.exception.OrderException;
import com.karrecy.common.helper.LoginHelper;
import com.karrecy.common.utils.StringUtils;
import com.karrecy.order.domain.po.RunnerApply;
import com.karrecy.order.domain.po.School;
import com.karrecy.order.mapper.RunnerApplyMapper;
import com.karrecy.order.mapper.SchoolMapper;
import com.karrecy.order.service.IRunnerApplyService;
import com.karrecy.system.mapper.UserMapper;
import com.karrecy.system.mapper.UserPcMapper;
import com.karrecy.system.mapper.UserWxMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * <p>
 * 跑腿申请表
 服务实现类
 * </p>
 */
@Service
@RequiredArgsConstructor
public class RunnerApplyServiceImpl extends ServiceImpl<RunnerApplyMapper, RunnerApply> implements IRunnerApplyService {

    private final RunnerApplyMapper runnerApplyMapper;
    private final UserMapper userMapper;
    private final UserWxMapper userWxMapper;
    private final SchoolMapper schoolMapper;
    private final UserPcMapper userPcMapper;

    private final EmailService emailService;
    /**
     * 跑腿申请分页查询
     * @param runnerApply
     * @param pageQuery
     * @return
     */
    @Override
    public TableDataInfo<RunnerApply> selectPageRunnerApplyList(RunnerApply runnerApply, PageQuery pageQuery) {
        LambdaQueryWrapper<RunnerApply> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ObjectUtil.isNotNull(runnerApply.getUid()), RunnerApply::getUid, runnerApply.getUid())
                .eq(ObjectUtil.isNotNull(runnerApply.getId()), RunnerApply::getId, runnerApply.getId())
                .like(StringUtils.isNotBlank(runnerApply.getRealname()), RunnerApply::getRealname, runnerApply.getRealname())
                .eq(ObjectUtil.isNotNull(runnerApply.getSchoolId()), RunnerApply::getSchoolId, runnerApply.getSchoolId())
                .eq(ObjectUtil.isNotNull(runnerApply.getStatus()), RunnerApply::getStatus, runnerApply.getStatus())
                .orderByDesc(RunnerApply::getCreateTime);
        ;
        Map<String, Object> params = runnerApply.getParams();
        if (params.get("createTimeBegin") != null) {
            lqw.between(RunnerApply::getCreateTime, params.get("createTimeBegin"), params.get("createTimeEnd"));
        }
        if (params.get("updateTimeBegin") != null) {
            lqw.between(RunnerApply::getUpdateTime, params.get("updateTimeBegin"), params.get("updateTimeEnd"));
        }

        if (LoginHelper.isSchoolAdmin()) {
            School one = schoolMapper.selectOne(new LambdaQueryWrapper<School>().eq(School::getBelongUid, LoginHelper.getUserId()));
            if (ObjectUtil.isNull(one)) throw new OrderException("当前无代理校区，没有权限");
            lqw.eq(RunnerApply::getSchoolId, one.getId());
        }
        Page<RunnerApply> schoolPage = runnerApplyMapper.selectPage(pageQuery.build(), lqw);
        return TableDataInfo.build(schoolPage);
    }

    /**
     * 跑腿申请
     * @param runnerApply
     */
    @Override
    public void apply(RunnerApply runnerApply) {
        Long uid = LoginHelper.getUserId();
        //判断校区状态
        School schoolDB = schoolMapper.selectById(runnerApply.getSchoolId());
        if (ObjectUtil.isNull(schoolDB)) throw new OrderException("校区不存在");
        if (!ObjectUtil.equals(schoolDB.getStatus(),Status.OK.getCode())) {
            throw new OrderException("校区已关闭");
        }
        //判断是不是普通用户
        UserWx userWxDB = userWxMapper.selectOne(new LambdaQueryWrapper<UserWx>().eq(UserWx::getUid, uid));
        if (ObjectUtil.equals(userWxDB.getIsRunner(),Status.OK.getCode())) {
            throw new OrderException("已是跑腿员，不可申请");
        }
        if (ObjectUtil.isNull(userWxDB.getPhone())) {
            throw new OrderException("请先绑定手机号");
        }
        //判断是否已有申请(审核中)
        RunnerApply runnerApplyDB = runnerApplyMapper.selectOne(new LambdaQueryWrapper<RunnerApply>().
                eq(RunnerApply::getUid, uid)
                .eq(RunnerApply::getStatus, Status.PENDING.getCode()));
        if (ObjectUtil.isNotNull(runnerApplyDB)) throw new OrderException("审核中，不可再次申请");

        runnerApply.setSchoolName(schoolDB.getName());
        runnerApply.setCreateTime(LocalDateTime.now());
        runnerApply.setUid(uid);
        runnerApply.setUpdateTime(LocalDateTime.now());
        runnerApply.setUpdateId(uid);
        runnerApply.setRemarks("");
        runnerApply.setStatus(Status.PENDING.getCode());
        runnerApplyMapper.insert(runnerApply);

        //通知校区代理
        Long belongUid = schoolDB.getBelongUid();
        UserPc agent = userPcMapper.selectOne(new LambdaQueryWrapper<UserPc>().eq(UserPc::getUid, belongUid));
        if (agent.getEmailEnable().equals(Status.OK.getCode())) {
            //发送邮件
            emailService.sendHtml(
                    agent.getEmail(),
                    "跑腿申请通知",
                    "您所管理校区有新的跑腿申请，请前往查看"
            );
        }
    }

    /**
     * 处理申请
     * @param runnerApply
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handle(RunnerApply runnerApply) {
        RunnerApply runnerApplyDB = runnerApplyMapper.selectById(runnerApply.getId());
        Long uid = LoginHelper.getUserId();

        if (ObjectUtil.equals(runnerApplyDB.getStatus(), Status.DISABLE.getCode()) && StringUtils.isBlank(runnerApply.getRemarks())) {
            throw new OrderException("备注不可为空");
        }
        if (ObjectUtil.equals(runnerApply.getStatus(),Status.OK.getCode())) {
            //审核通过
            School schoolDB = schoolMapper.selectById(runnerApplyDB.getSchoolId());
            if (ObjectUtil.isNull(schoolDB)) {
                throw new OrderException("申请校区不存在");
            }
            UserWx userWxDB = userWxMapper.selectOne(new LambdaQueryWrapper<UserWx>()
                    .eq(UserWx::getUid, runnerApplyDB.getUid()));
            userWxDB.setIsRunner(Status.OK.getCode());
            userWxDB.setCanTake(Status.OK.getCode());
            userWxDB.setSchoolId(runnerApplyDB.getSchoolId());
            userWxDB.setRealname(runnerApplyDB.getRealname());
            userWxDB.setGender(runnerApplyDB.getGender());
            userWxMapper.updateById(userWxDB);
            User userDB = userMapper.selectById(userWxDB.getUid());
            userDB.setUserType(UserType.RUNNER_USER.getCode());
            userMapper.updateById(userDB);
        }
        runnerApplyDB.setStatus(runnerApply.getStatus());
        runnerApplyDB.setRemarks(runnerApply.getRemarks());
        runnerApplyDB.setUpdateId(uid);
        runnerApplyDB.setUpdateTime(LocalDateTime.now());
        runnerApplyMapper.updateById(runnerApply);
        // TODO 微信通知
    }
}
