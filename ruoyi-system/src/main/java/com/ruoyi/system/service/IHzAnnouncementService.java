package com.ruoyi.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ruoyi.system.domain.HzAnnouncement;

import java.util.List;

/**
 * 公告通知Service接口
 *
 * @author ruoyi
 */
public interface IHzAnnouncementService
{
    /**
     * 根据ID查询公告
     *
     * @param announcementId 公告ID
     * @return 公告
     */
    public HzAnnouncement selectAnnouncementById(Long announcementId);

    /**
     * 查询已发布的公告列表
     *
     * @return 公告列表
     */
    public List<HzAnnouncement> selectPublishedAnnouncementList();

    /**
     * 根据类型查询已发布的公告列表
     *
     * @param announcementType 公告类型
     * @return 公告列表
     */
    public List<HzAnnouncement> selectPublishedAnnouncementListByType(String announcementType);

    /**
     * 分页查询公告列表
     *
     * @param announcement 公告
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 公告列表
     */
    public IPage<HzAnnouncement> selectAnnouncementPage(HzAnnouncement announcement, int pageNum, int pageSize);

    /**
     * 新增公告
     *
     * @param announcement 公告
     * @return 结果
     */
    public int insertAnnouncement(HzAnnouncement announcement);

    /**
     * 修改公告
     *
     * @param announcement 公告
     * @return 结果
     */
    public int updateAnnouncement(HzAnnouncement announcement);

    /**
     * 增加浏览次数
     *
     * @param announcementId 公告ID
     * @return 结果
     */
    public int increaseViewCount(Long announcementId);

    /**
     * 删除公告
     *
     * @param announcementId 公告ID
     * @return 结果
     */
    public int deleteAnnouncementById(Long announcementId);
}
