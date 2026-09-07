package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.HzComplaint;
import com.ruoyi.system.domain.HzContract;
import com.ruoyi.system.domain.HzHouse;
import com.ruoyi.system.domain.HzProject;
import com.ruoyi.system.domain.HzUser;
import com.ruoyi.system.mapper.HzComplaintMapper;
import com.ruoyi.system.mapper.HzContractMapper;
import com.ruoyi.system.mapper.HzHouseMapper;
import com.ruoyi.system.mapper.HzProjectMapper;
import com.ruoyi.system.mapper.HzUserMapper;
import com.ruoyi.system.service.IHzComplaintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 投诉建议Service业务层处理
 *
 * @author ruoyi
 */
@Service
public class HzComplaintServiceImpl extends ServiceImpl<HzComplaintMapper, HzComplaint> implements IHzComplaintService
{
    @Autowired
    private HzUserMapper userMapper;

    @Autowired
    private HzContractMapper contractMapper;

    @Autowired
    private HzProjectMapper projectMapper;

    @Autowired
    private HzHouseMapper houseMapper;

    @Override
    public IPage<HzComplaint> selectComplaintPage(HzComplaint complaint, int pageNum, int pageSize)
    {
        LambdaQueryWrapper<HzComplaint> wrapper = buildQueryWrapper(complaint);
        Page<HzComplaint> page = new Page<>(pageNum, pageSize);
        IPage<HzComplaint> result = this.page(page, wrapper);
        backfillUserAndHousingInfo(result.getRecords());
        return result;
    }

    @Override
    public List<HzComplaint> selectComplaintList(HzComplaint complaint)
    {
        LambdaQueryWrapper<HzComplaint> wrapper = buildQueryWrapper(complaint);
        List<HzComplaint> result = this.list(wrapper);
        backfillUserAndHousingInfo(result);
        return result;
    }

    @Override
    public List<HzComplaint> selectComplaintListByUserId(Long userId)
    {
        LambdaQueryWrapper<HzComplaint> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HzComplaint::getUserId, userId)
               .eq(HzComplaint::getDelFlag, "0")
               .orderByDesc(HzComplaint::getCreateTime);
        List<HzComplaint> result = this.list(wrapper);
        backfillUserAndHousingInfo(result);
        return result;
    }

    @Override
    public HzComplaint selectComplaintById(Long complaintId)
    {
        LambdaQueryWrapper<HzComplaint> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HzComplaint::getComplaintId, complaintId)
               .eq(HzComplaint::getDelFlag, "0");
        HzComplaint result = this.getOne(wrapper);
        backfillUserAndHousingInfo(result == null ? List.of() : List.of(result));
        return result;
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

        return this.removeById(complaintId) ? 1 : 0;
    }

    @Override
    public int deleteComplaintById(Long complaintId)
    {
        return this.removeById(complaintId) ? 1 : 0;
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

    /**
     * 回填投诉人姓名、所在项目、房间号（列表展示用）
     * 姓名来自 hz_user（real_name 优先，空则 nickname）；
     * 项目/房间取自该用户最近一条有效合同（含已到期/已解约等历史合同，排除软删），
     * 无任何合同记录的用户项目/房间留空。
     */
    private void backfillUserAndHousingInfo(List<HzComplaint> complaints)
    {
        if (complaints == null || complaints.isEmpty()) {
            return;
        }
        Set<Long> userIds = complaints.stream()
                .map(HzComplaint::getUserId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        if (userIds.isEmpty()) {
            return;
        }

        // 1. 批量查用户姓名
        Map<Long, HzUser> userMap = userMapper.selectList(
                        new LambdaQueryWrapper<HzUser>().in(HzUser::getUserId, userIds))
                .stream().collect(Collectors.toMap(HzUser::getUserId, u -> u, (a, b) -> a));

        // 2. 批量查用户最近一条合同（contract_id 倒序，取最新）
        Map<Long, HzContract> contractMap = new HashMap<>();
        List<HzContract> contracts = contractMapper.selectList(
                new LambdaQueryWrapper<HzContract>()
                        .in(HzContract::getTenantId, userIds)
                        .eq(HzContract::getDelFlag, "0")
                        .orderByDesc(HzContract::getContractId));
        for (HzContract c : contracts) {
            contractMap.putIfAbsent(c.getTenantId(), c);
        }

        // 3. 批量查项目名、房间号
        Set<Long> projectIds = contracts.stream()
                .map(HzContract::getProjectId).filter(Objects::nonNull).collect(Collectors.toSet());
        Map<Long, String> projectNameMap = projectIds.isEmpty() ? Map.of() :
                projectMapper.selectList(new LambdaQueryWrapper<HzProject>().in(HzProject::getProjectId, projectIds))
                        .stream().collect(Collectors.toMap(HzProject::getProjectId, HzProject::getProjectName, (a, b) -> a));
        Set<Long> houseIds = contracts.stream()
                .map(HzContract::getHouseId).filter(Objects::nonNull).collect(Collectors.toSet());
        Map<Long, String> houseNoMap = houseIds.isEmpty() ? Map.of() :
                houseMapper.selectList(new LambdaQueryWrapper<HzHouse>().in(HzHouse::getHouseId, houseIds))
                        .stream().collect(Collectors.toMap(HzHouse::getHouseId, HzHouse::getHouseNo, (a, b) -> a));

        // 4. 回填
        for (HzComplaint c : complaints) {
            HzUser u = userMap.get(c.getUserId());
            if (u != null) {
                String name = StringUtils.isNotEmpty(u.getRealName()) ? u.getRealName() : u.getNickname();
                c.setUserName(name);
            }
            HzContract ct = contractMap.get(c.getUserId());
            if (ct != null) {
                c.setProjectName(projectNameMap.get(ct.getProjectId()));
                c.setHouseNo(houseNoMap.get(ct.getHouseId()));
            }
        }
    }
}
