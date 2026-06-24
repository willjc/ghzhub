package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.HzHouse;
import com.ruoyi.system.domain.HzHouseStatusAudit;
import com.ruoyi.system.mapper.HzHouseStatusAuditMapper;
import com.ruoyi.system.service.IHzHouseService;
import com.ruoyi.system.service.IHzHouseStatusAuditService;
import com.ruoyi.system.service.IHzRoleProjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 房源状态变更审批Service业务层处理
 *
 * @author ruoyi
 */
@Service
public class HzHouseStatusAuditServiceImpl extends ServiceImpl<HzHouseStatusAuditMapper, HzHouseStatusAudit>
        implements IHzHouseStatusAuditService {

    private static final Logger logger = LoggerFactory.getLogger(HzHouseStatusAuditServiceImpl.class);

    @Autowired
    @Lazy
    private IHzHouseService houseService;

    @Autowired
    private IHzRoleProjectService roleProjectService;

    @Override
    public int submitStatusChange(Long houseId, String targetStatus) {
        HzHouse house = houseService.selectHouseById(houseId);
        if (house == null) {
            throw new ServiceException("房源不存在");
        }

        // 检查是否有待审批的记录
        long pending = this.count(new LambdaQueryWrapper<HzHouseStatusAudit>()
                .eq(HzHouseStatusAudit::getHouseId, houseId)
                .eq(HzHouseStatusAudit::getApproveStatus, "0")
                .eq(HzHouseStatusAudit::getDelFlag, "0"));
        if (pending > 0) {
            throw new ServiceException("该房源有待审批的状态变更申请，请等待审批完成");
        }

        HzHouseStatusAudit audit = new HzHouseStatusAudit();
        audit.setHouseId(houseId);
        audit.setHouseCode(house.getHouseCode());
        audit.setProjectId(house.getProjectId());
        audit.setCurrentStatus(house.getHouseStatus());
        audit.setTargetStatus(targetStatus);
        audit.setApplyBy(SecurityUtils.getUsername());
        audit.setApplyTime(DateUtils.getTime());
        audit.setApproveStatus("0"); // 待审批
        audit.setDelFlag("0");

        return this.save(audit) ? 1 : 0;
    }

    @Override
    @Transactional
    public int batchSubmitStatusChange(List<Long> houseIds, String targetStatus) {
        int count = 0;
        for (Long houseId : houseIds) {
            try {
                count += submitStatusChange(houseId, targetStatus);
            } catch (ServiceException e) {
                logger.warn("房源{}提交状态变更申请失败: {}", houseId, e.getMessage());
            }
        }
        return count;
    }

    @Override
    @Transactional
    public int approveStatusChange(Long auditId, String approveStatus, String opinion) {
        HzHouseStatusAudit audit = this.getById(auditId);
        if (audit == null) {
            throw new ServiceException("审批记录不存在");
        }
        if (!"0".equals(audit.getApproveStatus())) {
            throw new ServiceException("该申请已处理，请勿重复操作");
        }

        // 更新审批信息
        audit.setApproveStatus(approveStatus);
        audit.setApproveBy(SecurityUtils.getUsername());
        audit.setApproveTime(DateUtils.getTime());
        audit.setApproveOpinion(opinion);
        this.updateById(audit);

        // 审批通过：直接更新房源状态（绕过updateHouse的审批判断，避免死循环）
        if ("1".equals(approveStatus)) {
            HzHouse updateHouse = new HzHouse();
            updateHouse.setHouseId(audit.getHouseId());
            updateHouse.setHouseStatus(audit.getTargetStatus());
            houseService.updateById(updateHouse);
            logger.info("房源状态审批通过，houseId={}, {}→{}", audit.getHouseId(), audit.getCurrentStatus(), audit.getTargetStatus());
        }

        return 1;
    }

    @Override
    public IPage<HzHouseStatusAudit> selectAuditPage(HzHouseStatusAudit audit, int pageNum, int pageSize) {
        Page<HzHouseStatusAudit> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<HzHouseStatusAudit> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HzHouseStatusAudit::getDelFlag, "0")
               .eq(StringUtils.isNotEmpty(audit.getApproveStatus()), HzHouseStatusAudit::getApproveStatus, audit.getApproveStatus())
               .eq(audit.getProjectId() != null, HzHouseStatusAudit::getProjectId, audit.getProjectId())
               .like(StringUtils.isNotEmpty(audit.getHouseCode()), HzHouseStatusAudit::getHouseCode, audit.getHouseCode())
               .orderByDesc(HzHouseStatusAudit::getApplyTime);

        // 项目权限过滤
        List<Long> projectIds = roleProjectService.getCurrentUserProjectIds();
        if (projectIds != null && !projectIds.isEmpty()) {
            wrapper.in(HzHouseStatusAudit::getProjectId, projectIds);
        } else if (projectIds != null && projectIds.isEmpty()) {
            return new Page<>();
        }

        return this.page(page, wrapper);
    }

    @Override
    public HzHouseStatusAudit selectAuditById(Long auditId) {
        return this.getById(auditId);
    }
}
