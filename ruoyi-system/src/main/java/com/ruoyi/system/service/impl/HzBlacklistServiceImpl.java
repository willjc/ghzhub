package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.system.domain.HzBlacklist;
import com.ruoyi.system.mapper.HzBlacklistMapper;
import com.ruoyi.system.service.IHzBlacklistService;
import org.springframework.stereotype.Service;

/**
 * 黑名单Service业务层处理
 *
 * @author ruoyi
 */
@Service
public class HzBlacklistServiceImpl extends ServiceImpl<HzBlacklistMapper, HzBlacklist> implements IHzBlacklistService {

    @Override
    public HzBlacklist selectBlacklistById(Long blacklistId) {
        return this.getById(blacklistId);
    }

    @Override
    public HzBlacklist selectBlacklistByTenantId(Long tenantId) {
        LambdaQueryWrapper<HzBlacklist> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HzBlacklist::getTenantId, tenantId)
               .eq(HzBlacklist::getStatus, "0") // 生效中
               .eq(HzBlacklist::getDelFlag, "0")
               .orderByDesc(HzBlacklist::getBlacklistTime)
               .last("LIMIT 1");
        return this.getOne(wrapper);
    }

    @Override
    public HzBlacklist selectBlacklistByIdCard(String idCard) {
        LambdaQueryWrapper<HzBlacklist> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HzBlacklist::getIdCard, idCard)
               .eq(HzBlacklist::getStatus, "0") // 生效中
               .eq(HzBlacklist::getDelFlag, "0")
               .orderByDesc(HzBlacklist::getBlacklistTime)
               .last("LIMIT 1");
        return this.getOne(wrapper);
    }

    @Override
    public int insertBlacklist(HzBlacklist blacklist) {
        blacklist.setDelFlag("0");
        blacklist.setStatus("0"); // 默认生效中
        blacklist.setBlacklistTime(DateUtils.getTime());
        return this.save(blacklist) ? 1 : 0;
    }

    @Override
    public int updateBlacklist(HzBlacklist blacklist) {
        return this.updateById(blacklist) ? 1 : 0;
    }

    @Override
    public int removeBlacklist(Long blacklistId, String removeReason) {
        HzBlacklist blacklist = new HzBlacklist();
        blacklist.setBlacklistId(blacklistId);
        blacklist.setStatus("1"); // 已解除
        blacklist.setRemoveTime(DateUtils.getTime());
        blacklist.setRemoveReason(removeReason);
        return this.updateById(blacklist) ? 1 : 0;
    }
}
