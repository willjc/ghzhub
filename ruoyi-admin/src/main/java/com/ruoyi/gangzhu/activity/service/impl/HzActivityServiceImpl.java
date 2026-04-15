package com.ruoyi.gangzhu.activity.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.gangzhu.activity.domain.HzActivity;
import com.ruoyi.gangzhu.activity.mapper.HzActivityMapper;
import com.ruoyi.gangzhu.activity.service.IHzActivityService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * 人才家园活动Service业务层处理
 *
 * @author ruoyi
 */
@Service
public class HzActivityServiceImpl extends ServiceImpl<HzActivityMapper, HzActivity> implements IHzActivityService {

    /**
     * 查询人才家园活动列表
     *
     * @param activity 人才家园活动
     * @return 人才家园活动
     */
    @Override
    public List<HzActivity> selectActivityList(HzActivity activity) {
        LambdaQueryWrapper<HzActivity> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotEmpty(activity.getActivityTitle()), HzActivity::getActivityTitle, activity.getActivityTitle())
                .eq(StringUtils.isNotEmpty(activity.getActivityType()), HzActivity::getActivityType, activity.getActivityType())
                .like(StringUtils.isNotEmpty(activity.getOrganizer()), HzActivity::getOrganizer, activity.getOrganizer())
                .eq(StringUtils.isNotEmpty(activity.getStatus()), HzActivity::getStatus, activity.getStatus())
                .orderByDesc(HzActivity::getCreateTime);
        return this.list(wrapper);
    }

    /**
     * 分页查询人才家园活动列表
     *
     * @param page 分页参数
     * @param activity 人才家园活动
     * @return 分页结果
     */
    @Override
    public IPage<HzActivity> selectActivityPage(Page<HzActivity> page, HzActivity activity) {
        LambdaQueryWrapper<HzActivity> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotEmpty(activity.getActivityTitle()), HzActivity::getActivityTitle, activity.getActivityTitle())
                .eq(StringUtils.isNotEmpty(activity.getActivityType()), HzActivity::getActivityType, activity.getActivityType())
                .like(StringUtils.isNotEmpty(activity.getOrganizer()), HzActivity::getOrganizer, activity.getOrganizer())
                .eq(StringUtils.isNotEmpty(activity.getStatus()), HzActivity::getStatus, activity.getStatus())
                .orderByDesc(HzActivity::getCreateTime);
        return this.page(page, wrapper);
    }

    /**
     * 新增人才家园活动
     *
     * @param activity 人才家园活动
     * @return 结果
     */
    @Override
    public int insertActivity(HzActivity activity) {
        return this.save(activity) ? 1 : 0;
    }

    /**
     * 修改人才家园活动
     *
     * @param activity 人才家园活动
     * @return 结果
     */
    @Override
    public int updateActivity(HzActivity activity) {
        return this.updateById(activity) ? 1 : 0;
    }

    /**
     * 批量删除人才家园活动
     *
     * @param activityIds 需要删除的人才家园活动主键
     * @return 结果
     */
    @Override
    public int deleteActivityByIds(Long[] activityIds) {
        return this.removeByIds(Arrays.asList(activityIds)) ? 1 : 0;
    }

    /**
     * 删除人才家园活动信息
     *
     * @param activityId 人才家园活动主键
     * @return 结果
     */
    @Override
    public int deleteActivityById(Long activityId) {
        return this.removeById(activityId) ? 1 : 0;
    }

    /**
     * 增加浏览次数
     *
     * @param activityId 人才家园活动主键
     * @return 结果
     */
    @Override
    public int increaseViewCount(Long activityId) {
        LambdaUpdateWrapper<HzActivity> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(HzActivity::getActivityId, activityId)
                .setSql("view_count = view_count + 1");
        return this.update(wrapper) ? 1 : 0;
    }
}
