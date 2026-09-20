package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.HzRepair;
import com.ruoyi.system.domain.HzUser;
import com.ruoyi.system.mapper.HzRepairMapper;
import com.ruoyi.system.mapper.HzUserMapper;
import com.ruoyi.system.service.IHzRepairService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 物业报修Service业务层处理
 *
 * @author ruoyi
 */
@Service
public class HzRepairServiceImpl extends ServiceImpl<HzRepairMapper, HzRepair> implements IHzRepairService
{
    @Autowired
    private HzUserMapper userMapper;

    @Override
    public IPage<HzRepair> selectRepairPage(HzRepair repair, int pageNum, int pageSize)
    {
        LambdaQueryWrapper<HzRepair> wrapper = buildQueryWrapper(repair);
        Page<HzRepair> page = new Page<>(pageNum, pageSize);
        IPage<HzRepair> result = this.page(page, wrapper);
        fillApplicantNames(result.getRecords());
        return result;
    }

    @Override
    public List<HzRepair> selectRepairList(HzRepair repair)
    {
        LambdaQueryWrapper<HzRepair> wrapper = buildQueryWrapper(repair);
        List<HzRepair> repairs = this.list(wrapper);
        fillApplicantNames(repairs);
        return repairs;
    }

    @Override
    public List<HzRepair> selectRepairListByUserId(Long userId)
    {
        LambdaQueryWrapper<HzRepair> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HzRepair::getUserId, userId)
               .eq(HzRepair::getDelFlag, "0")
               .orderByDesc(HzRepair::getCreateTime);
        List<HzRepair> repairs = this.list(wrapper);
        fillApplicantNames(repairs);
        return repairs;
    }

    @Override
    public HzRepair selectRepairById(Long repairId)
    {
        LambdaQueryWrapper<HzRepair> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HzRepair::getRepairId, repairId)
               .eq(HzRepair::getDelFlag, "0");
        HzRepair repair = this.getOne(wrapper);
        fillApplicantNames(repair == null ? Collections.emptyList() : Collections.singletonList(repair));
        return repair;
    }

    @Override
    @Transactional
    public int insertRepair(HzRepair repair)
    {
        // 生成报修编号
        String repairNo = generateRepairNo();
        repair.setRepairNo(repairNo);

        repair.setDelFlag("0");
        repair.setStatus("0"); // 默认待处理
        repair.setCreateTime(DateUtils.getNowDate());
        return this.save(repair) ? 1 : 0;
    }

    @Override
    public int updateRepair(HzRepair repair)
    {
        repair.setUpdateTime(DateUtils.getNowDate());
        return this.updateById(repair) ? 1 : 0;
    }

    @Override
    public int handleRepair(HzRepair repair)
    {
        repair.setStatus("1"); // 已完成
        repair.setHandleTime(DateUtils.getTime());
        repair.setUpdateTime(DateUtils.getNowDate());
        return this.updateById(repair) ? 1 : 0;
    }

    @Override
    public int cancelRepair(Long repairId, Long userId)
    {
        HzRepair existRepair = selectRepairById(repairId);
        if (existRepair == null)
        {
            return 0;
        }

        // 验证是否为本人提交
        if (!userId.equals(existRepair.getUserId()))
        {
            return 0;
        }

        // 只有待处理的报修可以取消
        if (!"0".equals(existRepair.getStatus()))
        {
            return 0;
        }

        HzRepair repair = new HzRepair();
        repair.setRepairId(repairId);
        repair.setStatus("2"); // 已取消
        repair.setUpdateTime(DateUtils.getNowDate());
        return this.updateById(repair) ? 1 : 0;
    }

    @Override
    public int deleteRepairById(Long repairId)
    {
        return this.removeById(repairId) ? 1 : 0;
    }

    @Override
    public String generateRepairNo()
    {
        // 生成格式: BX + yyyyMMdd + 4位序号
        String dateStr = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String prefix = "BX" + dateStr;

        // 查询当天已有的报修数量
        LambdaQueryWrapper<HzRepair> wrapper = new LambdaQueryWrapper<>();
        wrapper.likeRight(HzRepair::getRepairNo, prefix);
        long count = this.count(wrapper);

        // 生成4位序号
        String sequence = String.format("%04d", count + 1);
        return prefix + sequence;
    }

    /**
     * 构建查询条件
     */
    private LambdaQueryWrapper<HzRepair> buildQueryWrapper(HzRepair repair)
    {
        LambdaQueryWrapper<HzRepair> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(repair.getUserId() != null, HzRepair::getUserId, repair.getUserId())
               .eq(StringUtils.isNotEmpty(repair.getStatus()), HzRepair::getStatus, repair.getStatus())
               .eq(StringUtils.isNotEmpty(repair.getRepairNo()), HzRepair::getRepairNo, repair.getRepairNo())
               .like(StringUtils.isNotEmpty(repair.getLocation()), HzRepair::getLocation, repair.getLocation())
               .like(StringUtils.isNotEmpty(repair.getPhone()), HzRepair::getPhone, repair.getPhone())
               .eq(HzRepair::getDelFlag, "0")
               .orderByDesc(HzRepair::getCreateTime);

        // 申请人姓名：hz_repair 无姓名冗余，经 hz_user.real_name 解析
        if (StringUtils.isNotEmpty(repair.getApplicantName())) {
            wrapper.exists("SELECT 1 FROM hz_user u WHERE u.user_id = hz_repair.user_id "
                    + "AND u.real_name LIKE CONCAT('%', {0}, '%')", repair.getApplicantName());
        }

        // 申请时间范围（前端 params.beginCreateTime / endCreateTime）
        if (repair.getParams() != null) {
            Object begin = repair.getParams().get("beginCreateTime");
            Object end = repair.getParams().get("endCreateTime");
            if (begin != null && StringUtils.isNotEmpty(begin.toString())) {
                wrapper.ge(HzRepair::getCreateTime, begin.toString() + " 00:00:00");
            }
            if (end != null && StringUtils.isNotEmpty(end.toString())) {
                wrapper.le(HzRepair::getCreateTime, end.toString() + " 23:59:59");
            }
        }
        return wrapper;
    }

    private void fillApplicantNames(List<HzRepair> repairs)
    {
        repairs.forEach(repair -> repair.setApplyTime(repair.getCreateTime()));
        List<Long> userIds = repairs.stream()
                .map(HzRepair::getUserId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        if (userIds.isEmpty()) {
            return;
        }
        Map<Long, HzUser> users = new HashMap<>();
        for (int start = 0; start < userIds.size(); start += 1000) {
            int end = Math.min(start + 1000, userIds.size());
            userMapper.selectBatchIds(userIds.subList(start, end))
                    .forEach(user -> users.putIfAbsent(user.getUserId(), user));
        }
        repairs.forEach(repair -> {
            HzUser user = users.get(repair.getUserId());
            repair.setApplicantName(user == null ? null : user.getRealName());
        });
    }
}
