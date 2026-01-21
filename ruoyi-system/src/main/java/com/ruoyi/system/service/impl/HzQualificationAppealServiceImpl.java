package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.HzQualificationAppeal;
import com.ruoyi.system.domain.HzQualificationAppealVO;
import com.ruoyi.system.domain.HzUser;
import com.ruoyi.system.mapper.HzQualificationAppealMapper;
import com.ruoyi.system.mapper.HzUserMapper;
import com.ruoyi.system.service.IHzQualificationAppealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 资格申诉Service业务层处理
 *
 * @author ruoyi
 */
@Service
public class HzQualificationAppealServiceImpl extends ServiceImpl<HzQualificationAppealMapper, HzQualificationAppeal> implements IHzQualificationAppealService {

    @Autowired
    private HzUserMapper userMapper;

    @Override
    public HzQualificationAppeal selectAppealById(Long appealId) {
        return this.getById(appealId);
    }

    @Override
    public HzQualificationAppealVO selectAppealVOById(Long appealId) {
        return this.baseMapper.selectAppealVOById(appealId);
    }

    @Override
    public List<HzQualificationAppealVO> selectAppealVOListByUserId(Long userId) {
        return this.baseMapper.selectAppealVOListByUserId(userId);
    }

    @Override
    public List<HzQualificationAppeal> selectAppealListByTenantId(Long tenantId) {
        LambdaQueryWrapper<HzQualificationAppeal> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HzQualificationAppeal::getTenantId, tenantId)
               .eq(HzQualificationAppeal::getDelFlag, "0")
               .orderByDesc(HzQualificationAppeal::getCreateTime);
        return this.list(wrapper);
    }

    @Override
    public IPage<HzQualificationAppealVO> selectAppealVOPage(HzQualificationAppeal appeal, int pageNum, int pageSize) {
        Page<HzQualificationAppealVO> page = new Page<>(pageNum, pageSize);
        QueryWrapper<HzQualificationAppeal> wrapper = new QueryWrapper<>();

        // 构建查询条件
        if (appeal.getTenantId() != null) {
            wrapper.eq("a.tenant_id", appeal.getTenantId());
        }
        if (StringUtils.isNotEmpty(appeal.getHandleResult())) {
            wrapper.eq("a.handle_result", appeal.getHandleResult());
        }
        if (StringUtils.isNotEmpty(appeal.getAppealReason())) {
            wrapper.like("a.appeal_reason", appeal.getAppealReason());
        }

        wrapper.eq("a.del_flag", "0");
        wrapper.orderByDesc("a.create_time");

        return this.baseMapper.selectAppealVOPage(page, wrapper);
    }

    @Override
    public IPage<HzQualificationAppeal> selectAppealPage(HzQualificationAppeal appeal, int pageNum, int pageSize) {
        Page<HzQualificationAppeal> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<HzQualificationAppeal> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(appeal.getTenantId() != null, HzQualificationAppeal::getTenantId, appeal.getTenantId())
               .eq(appeal.getQualificationId() != null, HzQualificationAppeal::getQualificationId, appeal.getQualificationId())
               .eq(StringUtils.isNotEmpty(appeal.getHandleResult()), HzQualificationAppeal::getHandleResult, appeal.getHandleResult())
               .eq(HzQualificationAppeal::getDelFlag, "0")
               .orderByDesc(HzQualificationAppeal::getCreateTime);
        return this.page(page, wrapper);
    }

    @Override
    public int insertAppeal(HzQualificationAppeal appeal) {
        appeal.setDelFlag("0");
        appeal.setHandleResult("0"); // 默认待处理
        appeal.setStatus("0");
        // 设置申诉时间
        appeal.setAppealTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        return this.save(appeal) ? 1 : 0;
    }

    @Override
    public int updateAppeal(HzQualificationAppeal appeal) {
        return this.updateById(appeal) ? 1 : 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int handleAppeal(Long appealId, String handleResult, String handleOpinion, String newEducation) {
        // 1. 查询申诉记录
        HzQualificationAppeal appeal = this.getById(appealId);
        if (appeal == null) {
            throw new RuntimeException("申诉记录不存在");
        }

        // 2. 更新申诉记录
        appeal.setHandleResult(handleResult);
        appeal.setHandleOpinion(handleOpinion);
        appeal.setHandleTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        boolean updateResult = this.updateById(appeal);
        if (!updateResult) {
            throw new RuntimeException("更新申诉记录失败");
        }

        // 3. 如果审核通过，更新用户学历
        if ("1".equals(handleResult) && StringUtils.isNotEmpty(newEducation)) {
            HzUser user = new HzUser();
            user.setUserId(appeal.getTenantId());
            user.setEducation(newEducation);

            int userUpdateResult = userMapper.updateById(user);
            if (userUpdateResult <= 0) {
                throw new RuntimeException("更新用户学历失败");
            }
        }

        return 1;
    }

    @Override
    public int deleteAppealById(Long appealId) {
        HzQualificationAppeal appeal = new HzQualificationAppeal();
        appeal.setAppealId(appealId);
        appeal.setDelFlag("2");
        return this.updateById(appeal) ? 1 : 0;
    }
}
