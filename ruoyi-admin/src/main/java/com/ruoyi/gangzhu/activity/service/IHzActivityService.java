package com.ruoyi.gangzhu.activity.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.gangzhu.activity.domain.HzActivity;

import java.util.List;

/**
 * 人才家园活动Service接口
 *
 * @author ruoyi
 */
public interface IHzActivityService extends IService<HzActivity> {

    /**
     * 查询人才家园活动列表
     *
     * @param activity 人才家园活动
     * @return 人才家园活动集合
     */
    List<HzActivity> selectActivityList(HzActivity activity);

    /**
     * 分页查询人才家园活动列表
     *
     * @param page 分页参数
     * @param activity 人才家园活动
     * @return 分页结果
     */
    IPage<HzActivity> selectActivityPage(Page<HzActivity> page, HzActivity activity);

    /**
     * 新增人才家园活动
     *
     * @param activity 人才家园活动
     * @return 结果
     */
    int insertActivity(HzActivity activity);

    /**
     * 修改人才家园活动
     *
     * @param activity 人才家园活动
     * @return 结果
     */
    int updateActivity(HzActivity activity);

    /**
     * 批量删除人才家园活动
     *
     * @param activityIds 需要删除的人才家园活动主键集合
     * @return 结果
     */
    int deleteActivityByIds(Long[] activityIds);

    /**
     * 删除人才家园活动信息
     *
     * @param activityId 人才家园活动主键
     * @return 结果
     */
    int deleteActivityById(Long activityId);

    /**
     * 增加浏览次数
     *
     * @param activityId 人才家园活动主键
     * @return 结果
     */
    int increaseViewCount(Long activityId);
}
