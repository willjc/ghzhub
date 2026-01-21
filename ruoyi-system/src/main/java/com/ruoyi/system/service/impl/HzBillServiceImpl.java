package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.HzBill;
import com.ruoyi.system.domain.HzBillVO;
import com.ruoyi.system.mapper.HzBillMapper;
import com.ruoyi.system.service.IHzBillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * 账单Service业务层处理
 *
 * @author ruoyi
 */
@Service
public class HzBillServiceImpl extends ServiceImpl<HzBillMapper, HzBill> implements IHzBillService {

    @Autowired
    private HzBillMapper billMapper;

    @Override
    public HzBill selectBillById(Long billId) {
        return this.getById(billId);
    }

    @Override
    public HzBill selectBillByBillNo(String billNo) {
        LambdaQueryWrapper<HzBill> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HzBill::getBillNo, billNo)
               .eq(HzBill::getDelFlag, "0")
               .last("LIMIT 1");
        return this.getOne(wrapper);
    }

    @Override
    public List<HzBill> selectBillListByTenantId(Long tenantId) {
        LambdaQueryWrapper<HzBill> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HzBill::getTenantId, tenantId)
               .eq(HzBill::getDelFlag, "0")
               .orderByDesc(HzBill::getCreateTime);
        return this.list(wrapper);
    }

    @Override
    public List<HzBill> selectBillListByContractId(Long contractId) {
        LambdaQueryWrapper<HzBill> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HzBill::getContractId, contractId)
               .eq(HzBill::getDelFlag, "0")
               .orderByDesc(HzBill::getCreateTime);
        return this.list(wrapper);
    }

    @Override
    public List<HzBill> selectUnpaidBillListByTenantId(Long tenantId) {
        LambdaQueryWrapper<HzBill> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HzBill::getTenantId, tenantId)
               .in(HzBill::getBillStatus, "0", "2") // 待支付或部分支付
               .eq(HzBill::getDelFlag, "0")
               .orderByAsc(HzBill::getDueDate);
        return this.list(wrapper);
    }

    @Override
    public List<HzBill> selectOverdueBillListByTenantId(Long tenantId) {
        LambdaQueryWrapper<HzBill> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HzBill::getTenantId, tenantId)
               .eq(HzBill::getBillStatus, "3")  // 3:已逾期
               .eq(HzBill::getDelFlag, "0")
               .orderByAsc(HzBill::getDueDate);
        return this.list(wrapper);
    }

    @Override
    public List<HzBill> selectBillList(HzBill bill) {
        LambdaQueryWrapper<HzBill> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(bill.getTenantId() != null, HzBill::getTenantId, bill.getTenantId())
               .eq(bill.getContractId() != null, HzBill::getContractId, bill.getContractId())
               .eq(bill.getHouseId() != null, HzBill::getHouseId, bill.getHouseId())
               .like(StringUtils.isNotEmpty(bill.getBillNo()), HzBill::getBillNo, bill.getBillNo())
               .eq(StringUtils.isNotEmpty(bill.getBillType()), HzBill::getBillType, bill.getBillType())
               .eq(StringUtils.isNotEmpty(bill.getBillStatus()), HzBill::getBillStatus, bill.getBillStatus())
               .eq(HzBill::getDelFlag, "0")
               .orderByDesc(HzBill::getCreateTime);
        return this.list(wrapper);
    }

    @Override
    public IPage<HzBill> selectBillPage(HzBill bill, int pageNum, int pageSize) {
        Page<HzBill> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<HzBill> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(bill.getTenantId() != null, HzBill::getTenantId, bill.getTenantId())
               .eq(bill.getContractId() != null, HzBill::getContractId, bill.getContractId())
               .eq(bill.getHouseId() != null, HzBill::getHouseId, bill.getHouseId())
               .like(StringUtils.isNotEmpty(bill.getBillNo()), HzBill::getBillNo, bill.getBillNo())
               .eq(StringUtils.isNotEmpty(bill.getBillType()), HzBill::getBillType, bill.getBillType())
               .eq(StringUtils.isNotEmpty(bill.getBillStatus()), HzBill::getBillStatus, bill.getBillStatus())
               .eq(HzBill::getDelFlag, "0")
               .orderByDesc(HzBill::getCreateTime);
        return this.page(page, wrapper);
    }

    @Override
    public int insertBill(HzBill bill) {
        bill.setDelFlag("0");
        bill.setBillStatus("0"); // 默认待支付状态
        bill.setIsOverdue("0"); // 默认未逾期
        bill.setOverdueDays(0);
        if (bill.getPaidAmount() == null) {
            bill.setPaidAmount(BigDecimal.ZERO);
        }
        if (bill.getUnpaidAmount() == null && bill.getBillAmount() != null) {
            bill.setUnpaidAmount(bill.getBillAmount());
        }
        if (StringUtils.isEmpty(bill.getBillNo())) {
            bill.setBillNo(generateBillNo());
        }
        return this.save(bill) ? 1 : 0;
    }

    @Override
    public int updateBill(HzBill bill) {
        return this.updateById(bill) ? 1 : 0;
    }

    @Override
    public int deleteBillById(Long billId) {
        LambdaUpdateWrapper<HzBill> wrapper = new LambdaUpdateWrapper<>();
        wrapper.set(HzBill::getDelFlag, "2")
               .eq(HzBill::getBillId, billId)
               .eq(HzBill::getDelFlag, "0");
        return this.update(wrapper) ? 1 : 0;
    }

    @Override
    public String generateBillNo() {
        return "ZD" + DateUtils.dateTimeNow() + String.format("%04d", (int)(Math.random() * 10000));
    }

    @Override
    public int updateBillStatus(Long billId, String billStatus) {
        HzBill bill = new HzBill();
        bill.setBillId(billId);
        bill.setBillStatus(billStatus);
        if ("1".equals(billStatus)) {
            bill.setPayTime(DateUtils.getTime());
        }
        return this.updateById(bill) ? 1 : 0;
    }

    @Override
    public void checkAndUpdateOverdueBills() {
        // 查询所有未支付和部分支付的账单
        LambdaQueryWrapper<HzBill> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(HzBill::getBillStatus, "0", "2")
               .eq(HzBill::getDelFlag, "0");
        List<HzBill> bills = this.list(wrapper);

        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (HzBill bill : bills) {
            if (StringUtils.isNotEmpty(bill.getDueDate())) {
                LocalDate dueDate = LocalDate.parse(bill.getDueDate().substring(0, 10), formatter);
                if (today.isAfter(dueDate)) {
                    // 已逾期
                    long overdueDays = ChronoUnit.DAYS.between(dueDate, today);
                    bill.setIsOverdue("1");
                    bill.setOverdueDays((int) overdueDays);
                    bill.setBillStatus("3"); // 更新状态为已逾期

                    // 计算滞纳金 (简单示例: 每天按未支付金额的0.05%计算)
                    if (bill.getUnpaidAmount() != null && bill.getUnpaidAmount().compareTo(BigDecimal.ZERO) > 0) {
                        BigDecimal lateFee = bill.getUnpaidAmount()
                                .multiply(BigDecimal.valueOf(0.0005))
                                .multiply(BigDecimal.valueOf(overdueDays))
                                .setScale(2, RoundingMode.HALF_UP);
                        bill.setLateFee(lateFee);
                    }

                    this.updateById(bill);
                }
            }
        }
    }

    @Override
    public HzBillVO selectBillVOById(Long billId) {
        return billMapper.selectBillVOById(billId);
    }

    @Override
    public List<HzBillVO> selectBillVOList(HzBill bill) {
        return billMapper.selectBillVOList(bill);
    }

    @Override
    public IPage<HzBillVO> selectBillVOPage(HzBill bill) {
        Page<HzBillVO> page = com.ruoyi.common.utils.PageUtils.getPage();
        return billMapper.selectBillVOPage(page, bill);
    }
}
