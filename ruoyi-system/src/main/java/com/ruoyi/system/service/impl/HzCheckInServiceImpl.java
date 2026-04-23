package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.HzCheckIn;
import com.ruoyi.system.mapper.HzCheckInMapper;
import com.ruoyi.system.service.IHzCheckInService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 入住申请Service业务层处理
 *
 * @author ruoyi
 */
@Service
public class HzCheckInServiceImpl extends ServiceImpl<HzCheckInMapper, HzCheckIn> implements IHzCheckInService {

    @Override
    public HzCheckIn selectCheckInById(Long recordId) {
        return baseMapper.selectCheckInByIdWithRelations(recordId);
    }

    @Override
    public List<HzCheckIn> selectCheckInListByTenantId(Long tenantId) {
        LambdaQueryWrapper<HzCheckIn> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HzCheckIn::getTenantId, tenantId)
               .eq(HzCheckIn::getDelFlag, "0")
               .orderByDesc(HzCheckIn::getCreateTime);
        return this.list(wrapper);
    }

    @Override
    public List<HzCheckIn> selectConfirmedCheckInListByTenantId(Long tenantId) {
        LambdaQueryWrapper<HzCheckIn> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HzCheckIn::getTenantId, tenantId)
               .in(HzCheckIn::getStatus, "2", "3", "4")  // 兼容老数据：2=已完成入住确认, 3=已确认资料, 4=已入住确认
               .eq(HzCheckIn::getDelFlag, "0")
               .orderByDesc(HzCheckIn::getCreateTime);
        return this.list(wrapper);
    }

    @Override
    public HzCheckIn selectCheckInByContractId(Long contractId) {
        LambdaQueryWrapper<HzCheckIn> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HzCheckIn::getContractId, contractId)
               .eq(HzCheckIn::getDelFlag, "0")
               .last("LIMIT 1");
        return this.getOne(wrapper);
    }

    @Override
    public List<HzCheckIn> selectCheckInList(HzCheckIn checkIn) {
        // 使用带关联查询的方法，返回合同编号、用户昵称、房源名称等关联信息
        return baseMapper.selectCheckInListWithRelations(checkIn);
    }

    @Override
    public IPage<HzCheckIn> selectCheckInPage(HzCheckIn checkIn, int pageNum, int pageSize) {
        Page<HzCheckIn> page = new Page<>(pageNum, pageSize);
        return baseMapper.selectCheckInPageWithRelations(page, checkIn);
    }

    @Override
    public int insertCheckIn(HzCheckIn checkIn) {
        checkIn.setDelFlag("0");
        if (StringUtils.isEmpty(checkIn.getStatus())) {
            checkIn.setStatus("0"); // 默认待办理状态
        }
        if (StringUtils.isEmpty(checkIn.getCheckinNo())) {
            checkIn.setCheckinNo(generateCheckinNo()); // 生成入住单号
        }
        // 设置申请时间（创建时间）
        if (checkIn.getCreateTime() == null) {
            checkIn.setCreateTime(DateUtils.getNowDate());
        }
        return this.save(checkIn) ? 1 : 0;
    }

    @Override
    public int updateCheckIn(HzCheckIn checkIn) {
        return this.updateById(checkIn) ? 1 : 0;
    }

    @Override
    public int deleteCheckInById(Long recordId) {
        LambdaUpdateWrapper<HzCheckIn> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(HzCheckIn::getRecordId, recordId)
               .set(HzCheckIn::getDelFlag, "2");
        return this.update(wrapper) ? 1 : 0;
    }

    @Override
    public int updateCheckInStatus(Long recordId, String status) {
        LambdaUpdateWrapper<HzCheckIn> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(HzCheckIn::getRecordId, recordId)
               .set(HzCheckIn::getStatus, status);
        return this.update(wrapper) ? 1 : 0;
    }

    @Override
    public String generateCheckinNo() {
        return "RZ" + DateUtils.dateTimeNow() + String.format("%04d", (int)(Math.random() * 10000));
    }
}
