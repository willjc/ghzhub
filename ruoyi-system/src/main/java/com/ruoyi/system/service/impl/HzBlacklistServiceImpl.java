package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.HzBlacklist;
import com.ruoyi.system.domain.HzUser;
import com.ruoyi.system.mapper.HzBlacklistMapper;
import com.ruoyi.system.mapper.HzUserMapper;
import com.ruoyi.system.service.IHzBlacklistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 黑名单Service业务层处理
 *
 * 状态说明（与数据库表注释和存量数据一致）：1=生效中，0=已解除
 *
 * @author ruoyi
 */
@Service
public class HzBlacklistServiceImpl extends ServiceImpl<HzBlacklistMapper, HzBlacklist> implements IHzBlacklistService {

    @Autowired
    private HzUserMapper userMapper;

    @Override
    public HzBlacklist selectBlacklistById(Long blacklistId) {
        return this.getById(blacklistId);
    }

    @Override
    public HzBlacklist selectBlacklistByTenantId(Long tenantId) {
        LambdaQueryWrapper<HzBlacklist> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HzBlacklist::getTenantId, tenantId)
               .eq(HzBlacklist::getStatus, "1") // 生效中
               .eq(HzBlacklist::getDelFlag, "0")
               .orderByDesc(HzBlacklist::getBlacklistTime)
               .last("LIMIT 1");
        return this.getOne(wrapper);
    }

    @Override
    public HzBlacklist selectBlacklistByIdCard(String idCard) {
        LambdaQueryWrapper<HzBlacklist> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HzBlacklist::getIdCard, idCard)
               .eq(HzBlacklist::getStatus, "1") // 生效中
               .eq(HzBlacklist::getDelFlag, "0")
               .orderByDesc(HzBlacklist::getBlacklistTime)
               .last("LIMIT 1");
        return this.getOne(wrapper);
    }

    @Override
    public IPage<HzBlacklist> selectBlacklistPage(HzBlacklist query, int pageNum, int pageSize) {
        Page<HzBlacklist> page = new Page<>(pageNum, pageSize);
        IPage<HzBlacklist> result = this.page(page, buildQueryWrapper(query));
        backfillPhone(result.getRecords());
        return result;
    }

    @Override
    public List<HzBlacklist> selectBlacklistList(HzBlacklist query) {
        List<HzBlacklist> list = this.list(buildQueryWrapper(query));
        backfillPhone(list);
        return list;
    }

    @Override
    public int insertBlacklist(HzBlacklist blacklist) {
        blacklist.setDelFlag("0");
        blacklist.setStatus("1"); // 默认生效中
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
        blacklist.setStatus("0"); // 已解除
        blacklist.setRemoveTime(DateUtils.getTime());
        blacklist.setRemoveReason(removeReason);
        return this.updateById(blacklist) ? 1 : 0;
    }

    /**
     * 构建查询条件：姓名模糊、手机号（转用户ID集合）、状态、身份证
     */
    private LambdaQueryWrapper<HzBlacklist> buildQueryWrapper(HzBlacklist query) {
        LambdaQueryWrapper<HzBlacklist> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HzBlacklist::getDelFlag, "0");
        if (query == null) {
            wrapper.orderByDesc(HzBlacklist::getBlacklistTime);
            return wrapper;
        }
        if (StringUtils.isNotEmpty(query.getTenantName())) {
            wrapper.like(HzBlacklist::getTenantName, query.getTenantName());
        }
        if (StringUtils.isNotEmpty(query.getIdCard())) {
            wrapper.like(HzBlacklist::getIdCard, query.getIdCard());
        }
        if (StringUtils.isNotEmpty(query.getStatus())) {
            wrapper.eq(HzBlacklist::getStatus, query.getStatus());
        }
        // 手机号过滤：先按手机号查 hz_user，再按 tenant_id（=user_id）过滤
        if (StringUtils.isNotEmpty(query.getPhone())) {
            List<Long> userIds = userMapper.selectMaps(new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<HzUser>()
                            .select("user_id")
                            .like("phone", query.getPhone()))
                    .stream()
                    .map(m -> m.get("user_id"))
                    .filter(Objects::nonNull)
                    .map(v -> Long.valueOf(String.valueOf(v)))
                    .collect(Collectors.toList());
            if (userIds.isEmpty()) {
                // 没有匹配用户：返回空结果
                wrapper.eq(HzBlacklist::getBlacklistId, -1L);
            } else {
                wrapper.in(HzBlacklist::getTenantId, userIds);
            }
        }
        wrapper.orderByDesc(HzBlacklist::getBlacklistTime);
        return wrapper;
    }

    /** 回填手机号（来自 hz_user.phone，展示用） */
    private void backfillPhone(List<HzBlacklist> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        Set<Long> userIds = list.stream().map(HzBlacklist::getTenantId)
                .filter(Objects::nonNull).collect(Collectors.toSet());
        if (userIds.isEmpty()) {
            return;
        }
        Map<Long, String> phoneMap = new java.util.HashMap<>();
        List<Map<String, Object>> rows = userMapper.selectMaps(new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<HzUser>()
                .select("user_id, phone")
                .in("user_id", userIds));
        for (Map<String, Object> row : rows) {
            Object uid = row.get("user_id");
            Object ph = row.get("phone");
            if (uid != null) {
                phoneMap.put(Long.valueOf(String.valueOf(uid)), ph == null ? "" : String.valueOf(ph));
            }
        }
        for (HzBlacklist b : list) {
            if (b.getTenantId() != null) {
                b.setPhone(phoneMap.getOrDefault(b.getTenantId(), ""));
            }
        }
    }
}
