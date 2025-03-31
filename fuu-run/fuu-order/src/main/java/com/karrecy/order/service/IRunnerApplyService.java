package com.karrecy.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.karrecy.common.core.domain.PageQuery;
import com.karrecy.common.core.page.TableDataInfo;
import com.karrecy.order.domain.po.RunnerApply;

/**
 * <p>
 * 跑腿申请表
 服务类
 * </p>
 */
public interface IRunnerApplyService extends IService<RunnerApply> {

    /**
     * 跑腿申请分页查询
     * @param runnerApply
     * @param pageQuery
     * @return
     */
    TableDataInfo<RunnerApply> selectPageRunnerApplyList(RunnerApply runnerApply, PageQuery pageQuery);

    /**
     * 跑腿申请
     * @param runnerApply
     */
    void apply(RunnerApply runnerApply);

    /**
     * 处理申请
     * @param runnerApply
     */
    void handle(RunnerApply runnerApply);
}
