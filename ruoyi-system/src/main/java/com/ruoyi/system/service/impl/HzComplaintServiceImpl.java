package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.HzComplaint;
import com.ruoyi.system.mapper.HzComplaintMapper;
import com.ruoyi.system.service.IHzComplaintService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 投诉建议Service业务层处理
 *
 * @author ruoyi
 */
@Service
public class HzComplaintServiceImpl extends ServiceImpl<HzComplaintMapper, HzComplaint> implements IHzComplaintService
{
    @Override
    public IPage<HzComplaint> selectComplaintPage(HzComplaint complaint, int pageNum, int pageSize)
    {
        LambdaQueryWrapper<HzComplaint> wrapper = buildQueryWrapper(complaint);
        Page<HzComplaint> page = new Page<>(pageNum, pageSize);
        return this.page(page, wrapper);
    }

    @Override
    public List<HzComplaint> selectComplaintList(HzComplaint complaint)
    {
        LambdaQueryWrapper<HzComplaint> wrapper = buildQueryWrapper(complaint);
        return this.list(wrapper);
    }

    @Override
    public List<HzComplaint> selectComplaintListByUserId(Long userId)
    {
        LambdaQueryWrapper<HzComplaint> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HzComplaint::getUserId, userId)
               .eq(HzComplaint::getDelFlag, "0")
               .orderByDesc(HzComplaint::getCreateTime);
        return this.list(wrapper);
    }

    @Override
    public HzComplaint selectComplaintById(Long complaintId)
    {
        LambdaQueryWrapper<HzComplaint> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HzComplaint::getComplaintId, complaintId)
               .eq(HzComplaint::getDelFlag, "0");
        return this.getOne(wrapper);
    }

    @Override
    public int insertComplaint(HzComplaint complaint)
    {
        complaint.setDelFlag("0");
        complaint.setStatus("0"); // 默认待处理
        complaint.setIsUrged("0"); // 默认未催办
        complaint.setUrgeCount(0);
        complaint.setCreateTime(DateUtils.getNowDate());
        return this.save(complaint) ? 1 : 0;
    }

    @Override
    public int updateComplaint(HzComplaint complaint)
    {
        complaint.setUpdateTime(DateUtils.getNowDate());
        return this.updateById(complaint) ? 1 : 0;
    }

    @Override
    public int urgeComplaint(Long complaintId)
    {
        HzComplaint existComplaint = selectComplaintById(complaintId);
        if (existComplaint == null)
        {
            return 0;
        }

        // 只有待处理的投诉可以催办
        if (!"0".equals(existComplaint.getStatus()))
        {
            return 0;
        }

        HzComplaint complaint = new HzComplaint();
        complaint.setComplaintId(complaintId);
        complaint.setIsUrged("1");
        complaint.setUrgeCount((existComplaint.getUrgeCount() == null ? 0 : existComplaint.getUrgeCount()) + 1);
        complaint.setUrgeTime(DateUtils.getTime());
        complaint.setUpdateTime(DateUtils.getNowDate());
        return this.updateById(complaint) ? 1 : 0;
    }

    @Override
    public int handleComplaint(HzComplaint complaint)
    {
        complaint.setStatus("1"); // 已处理
        complaint.setHandleTime(DateUtils.getTime());
        complaint.setUpdateTime(DateUtils.getNowDate());
        return this.updateById(complaint) ? 1 : 0;
    }

    @Override
    public int cancelComplaint(Long complaintId, Long userId)
    {
        HzComplaint existComplaint = selectComplaintById(complaintId);
        if (existComplaint == null)
        {
            return 0;
        }

        // 验证是否为本人提交
        if (!userId.equals(existComplaint.getUserId()))
        {
            return 0;
        }

        // 只有待处理的投诉可以取消
        if (!"0".equals(existComplaint.getStatus()))
        {
            return 0;
        }

        HzComplaint complaint = new HzComplaint();
        complaint.setComplaintId(complaintId);
        complaint.setDelFlag("2"); // 逻辑删除
        complaint.setUpdateTime(DateUtils.getNowDate());
        return this.updateById(complaint) ? 1 : 0;
    }

    @Override
    public int deleteComplaintById(Long complaintId)
    {
        HzComplaint complaint = new HzComplaint();
        complaint.setComplaintId(complaintId);
        complaint.setDelFlag("2");
        complaint.setUpdateTime(DateUtils.getNowDate());
        return this.updateById(complaint) ? 1 : 0;
    }

    /**
     * 构建查询条件
     */
    private LambdaQueryWrapper<HzComplaint> buildQueryWrapper(HzComplaint complaint)
    {
        LambdaQueryWrapper<HzComplaint> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(complaint.getUserId() != null, HzComplaint::getUserId, complaint.getUserId())
               .eq(StringUtils.isNotEmpty(complaint.getStatus()), HzComplaint::getStatus, complaint.getStatus())
               .eq(StringUtils.isNotEmpty(complaint.getIsUrged()), HzComplaint::getIsUrged, complaint.getIsUrged())
               .eq(StringUtils.isNotEmpty(complaint.getTitle()), HzComplaint::getTitle, complaint.getTitle())
               .eq(HzComplaint::getDelFlag, "0")
               .orderByDesc(HzComplaint::getCreateTime);
        return wrapper;
    }
}
