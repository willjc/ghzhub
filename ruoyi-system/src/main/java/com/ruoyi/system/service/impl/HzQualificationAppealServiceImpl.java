package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.utils.SecurityUtils;
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
            // “done” → 已处理（1 或 2）；其他值走精确匹配
            if ("done".equalsIgnoreCase(appeal.getHandleResult())) {
                wrapper.in("a.handle_result", "1", "2");
            } else {
                wrapper.eq("a.handle_result", appeal.getHandleResult());
            }
        }
        if (StringUtils.isNotEmpty(appeal.getAppealReason())) {
            wrapper.like("a.appeal_reason", appeal.getAppealReason());
        }
        // 用户昵称 / 手机号模糊搜索（通过 VO 承载）
        if (appeal instanceof HzQualificationAppealVO) {
            HzQualificationAppealVO vo = (HzQualificationAppealVO) appeal;
            if (StringUtils.isNotEmpty(vo.getNickname())) {
                wrapper.like("u.nickname", vo.getNickname());
            }
            if (StringUtils.isNotEmpty(vo.getPhone())) {
                wrapper.like("u.phone", vo.getPhone());
            }
        }

        wrapper.eq("a.del_flag", "0");
        // 排序：待处理(0) 优先 → 已通过(1)/已拒绝(2) 在后；同状态按申诉时间倒序
        wrapper.orderByAsc("a.handle_result");
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
    public int handleAppealSplit(HzQualificationAppeal input, String newEducation) {
        if (input == null || input.getAppealId() == null) {
            throw new RuntimeException("入参不合法");
        }

        // 1. 查询申诉记录
        HzQualificationAppeal appeal = this.getById(input.getAppealId());
        if (appeal == null) {
            throw new RuntimeException("申诉记录不存在");
        }

        // 2. 防御性校验：只能审核「用户实际提交的材料」
        boolean hasEdu    = StringUtils.isNotEmpty(appeal.getAppealAttachments());
        boolean hasSocial = StringUtils.isNotEmpty(appeal.getSocialAttachments());

        String eduStatus    = input.getEducationAuditStatus();
        String socialStatus = input.getSocialAuditStatus();

        if (StringUtils.isNotEmpty(eduStatus) && !hasEdu) {
            throw new RuntimeException("用户未提交学历附件，不可审核学历项");
        }
        if (StringUtils.isNotEmpty(socialStatus) && !hasSocial) {
            throw new RuntimeException("用户未提交社保附件，不可审核社保项");
        }
        if (StringUtils.isEmpty(eduStatus) && StringUtils.isEmpty(socialStatus)) {
            throw new RuntimeException("学历和社保至少要审核一项");
        }

        // 3. 局部更新：仅更新本次传入的侧（null 表示不动）
        if (StringUtils.isNotEmpty(eduStatus)) {
            appeal.setEducationAuditStatus(eduStatus);
            appeal.setEducationAuditOpinion(input.getEducationAuditOpinion());
        }
        if (StringUtils.isNotEmpty(socialStatus)) {
            appeal.setSocialAuditStatus(socialStatus);
            appeal.setSocialAuditOpinion(input.getSocialAuditOpinion());
        }

        // 4. 兼容旧字段：handle_result 用「最差状态」做摘要（任一驳回 → 整体驳回；
        //    都通过 → 整体通过；其他 → 待处理），handle_opinion 拼接两侧意见
        String summaryStatus = computeSummaryStatus(appeal.getEducationAuditStatus(), appeal.getSocialAuditStatus());
        appeal.setHandleResult(summaryStatus);
        appeal.setHandleOpinion(buildSummaryOpinion(appeal.getEducationAuditOpinion(), appeal.getSocialAuditOpinion()));
        appeal.setHandleTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        // 记录处理人（当前登录后台用户 user_id）—— 详情页“处理人”依赖该字段 LEFT JOIN sys_user 取 nick_name
        try {
            Long operatorId = SecurityUtils.getUserId();
            if (operatorId != null) {
                appeal.setHandleBy(String.valueOf(operatorId));
            }
        } catch (Exception ignore) {
            // 非 Web 上下文调用时不阐出，避免影响主流程
        }
        // status：摘要状态非 0 则置为「已处理」
        if (!"0".equals(summaryStatus)) {
            appeal.setStatus("1");
        }

        boolean updateOk = this.updateById(appeal);
        if (!updateOk) {
            throw new RuntimeException("更新申诉记录失败");
        }

        // 5. 学历审核通过 → 回写用户学历字段（社保侧无需回写，校验时实时查申诉表豁免）
        if ("1".equals(eduStatus) && StringUtils.isNotEmpty(newEducation)) {
            HzUser user = new HzUser();
            user.setUserId(appeal.getTenantId());
            user.setEducation(newEducation);
            int rows = userMapper.updateById(user);
            if (rows <= 0) {
                throw new RuntimeException("更新用户学历失败");
            }
        }

        return 1;
    }

    /** 计算两侧审核的摘要状态：任一驳回 → 2；都通过 → 1；其他 → 0 */
    private String computeSummaryStatus(String edu, String soc) {
        if ("2".equals(edu) || "2".equals(soc)) return "2";
        boolean eduPassed = "1".equals(edu) || edu == null;   // null 视为「未提交」不阻断
        boolean socPassed = "1".equals(soc) || soc == null;
        if (eduPassed && socPassed && ("1".equals(edu) || "1".equals(soc))) return "1";
        return "0";
    }

    /** 拼接两侧审核意见（任意一侧空则只显示另一侧） */
    private String buildSummaryOpinion(String eduOp, String socOp) {
        boolean hasEdu = StringUtils.isNotEmpty(eduOp);
        boolean hasSoc = StringUtils.isNotEmpty(socOp);
        if (hasEdu && hasSoc) return "[学历]" + eduOp + " | [社保]" + socOp;
        if (hasEdu) return "[学历]" + eduOp;
        if (hasSoc) return "[社保]" + socOp;
        return null;
    }

    @Override
    public int deleteAppealById(Long appealId) {
        return this.removeById(appealId) ? 1 : 0;
    }

    @Override
    public boolean existsPassedEducationAppeal(Long userId) {
        if (userId == null) return false;
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<HzQualificationAppeal> qw =
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        qw.eq("tenant_id", userId)
          .eq("education_audit_status", "1")
          .eq("del_flag", "0");
        return this.count(qw) > 0;
    }

    @Override
    public boolean existsPassedSocialAppeal(Long userId) {
        if (userId == null) return false;
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<HzQualificationAppeal> qw =
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        qw.eq("tenant_id", userId)
          .eq("social_audit_status", "1")
          .eq("del_flag", "0");
        return this.count(qw) > 0;
    }
}
