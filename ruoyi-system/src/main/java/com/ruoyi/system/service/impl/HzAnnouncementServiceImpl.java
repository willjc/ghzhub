package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.HzAnnouncement;
import com.ruoyi.system.mapper.HzAnnouncementMapper;
import com.ruoyi.system.service.IHzAnnouncementService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 公告通知Service业务层处理
 *
 * @author ruoyi
 */
@Service
public class HzAnnouncementServiceImpl extends ServiceImpl<HzAnnouncementMapper, HzAnnouncement> implements IHzAnnouncementService
{
    @Override
    public HzAnnouncement selectAnnouncementById(Long announcementId)
    {
        LambdaQueryWrapper<HzAnnouncement> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HzAnnouncement::getAnnouncementId, announcementId)
               .eq(HzAnnouncement::getDelFlag, "0");
        return this.getOne(wrapper);
    }

    @Override
    public List<HzAnnouncement> selectPublishedAnnouncementList()
    {
        LambdaQueryWrapper<HzAnnouncement> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HzAnnouncement::getStatus, "1") // 已发布
               .eq(HzAnnouncement::getDelFlag, "0")
               .orderByDesc(HzAnnouncement::getIsTop) // 置顶优先
               .orderByDesc(HzAnnouncement::getPublishTime);
        return this.list(wrapper);
    }

    @Override
    public List<HzAnnouncement> selectPublishedAnnouncementListByType(String announcementType)
    {
        LambdaQueryWrapper<HzAnnouncement> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HzAnnouncement::getAnnouncementType, announcementType)
               .eq(HzAnnouncement::getStatus, "1") // 已发布
               .eq(HzAnnouncement::getDelFlag, "0")
               .orderByDesc(HzAnnouncement::getIsTop)
               .orderByDesc(HzAnnouncement::getPublishTime);
        return this.list(wrapper);
    }

    @Override
    public IPage<HzAnnouncement> selectAnnouncementPage(HzAnnouncement announcement, int pageNum, int pageSize)
    {
        LambdaQueryWrapper<HzAnnouncement> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotEmpty(announcement.getAnnouncementTitle()),
                    HzAnnouncement::getAnnouncementTitle, announcement.getAnnouncementTitle())
               .eq(StringUtils.isNotEmpty(announcement.getAnnouncementType()),
                   HzAnnouncement::getAnnouncementType, announcement.getAnnouncementType())
               .eq(StringUtils.isNotEmpty(announcement.getStatus()),
                   HzAnnouncement::getStatus, announcement.getStatus())
               .eq(HzAnnouncement::getDelFlag, "0")
               .orderByDesc(HzAnnouncement::getIsTop)
               .orderByDesc(HzAnnouncement::getPublishTime);

        Page<HzAnnouncement> page = new Page<>(pageNum, pageSize);
        return this.page(page, wrapper);
    }

    @Override
    public int insertAnnouncement(HzAnnouncement announcement)
    {
        announcement.setDelFlag("0");
        announcement.setViewCount(0L);
        announcement.setCreateTime(DateUtils.getNowDate());
        return this.save(announcement) ? 1 : 0;
    }

    @Override
    public int updateAnnouncement(HzAnnouncement announcement)
    {
        announcement.setUpdateTime(DateUtils.getNowDate());
        return this.updateById(announcement) ? 1 : 0;
    }

    @Override
    public int increaseViewCount(Long announcementId)
    {
        HzAnnouncement announcement = this.selectAnnouncementById(announcementId);
        if (announcement != null)
        {
            announcement.setViewCount(announcement.getViewCount() + 1);
            announcement.setUpdateTime(DateUtils.getNowDate());
            return this.updateById(announcement) ? 1 : 0;
        }
        return 0;
    }

    @Override
    public int deleteAnnouncementById(Long announcementId)
    {
        LambdaUpdateWrapper<HzAnnouncement> wrapper = new LambdaUpdateWrapper<>();
        wrapper.set(HzAnnouncement::getDelFlag, "2")
               .set(HzAnnouncement::getUpdateTime, DateUtils.getNowDate())
               .eq(HzAnnouncement::getAnnouncementId, announcementId)
               .eq(HzAnnouncement::getDelFlag, "0");
        return this.update(wrapper) ? 1 : 0;
    }
}
