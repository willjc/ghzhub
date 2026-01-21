package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.HzRepair;
import com.ruoyi.system.mapper.HzRepairMapper;
import com.ruoyi.system.service.IHzRepairService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 物业报修Service业务层处理
 *
 * @author ruoyi
 */
@Service
public class HzRepairServiceImpl extends ServiceImpl<HzRepairMapper, HzRepair> implements IHzRepairService
{
    @Override
    public IPage<HzRepair> selectRepairPage(HzRepair repair, int pageNum, int pageSize)
    {
        LambdaQueryWrapper<HzRepair> wrapper = buildQueryWrapper(repair);
        Page<HzRepair> page = new Page<>(pageNum, pageSize);
        return this.page(page, wrapper);
    }

    @Override
    public List<HzRepair> selectRepairList(HzRepair repair)
    {
        LambdaQueryWrapper<HzRepair> wrapper = buildQueryWrapper(repair);
        return this.list(wrapper);
    }

    @Override
    public List<HzRepair> selectRepairListByUserId(Long userId)
    {
        LambdaQueryWrapper<HzRepair> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HzRepair::getUserId, userId)
               .eq(HzRepair::getDelFlag, "0")
               .orderByDesc(HzRepair::getCreateTime);
        return this.list(wrapper);
    }

    @Override
    public HzRepair selectRepairById(Long repairId)
    {
        LambdaQueryWrapper<HzRepair> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HzRepair::getRepairId, repairId)
               .eq(HzRepair::getDelFlag, "0");
        return this.getOne(wrapper);
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
        HzRepair repair = new HzRepair();
        repair.setRepairId(repairId);
        repair.setDelFlag("2");
        repair.setUpdateTime(DateUtils.getNowDate());
        return this.updateById(repair) ? 1 : 0;
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
               .eq(HzRepair::getDelFlag, "0")
               .orderByDesc(HzRepair::getCreateTime);
        return wrapper;
    }
}
