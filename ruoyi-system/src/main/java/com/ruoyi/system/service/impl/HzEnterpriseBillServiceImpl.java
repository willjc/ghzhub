package com.ruoyi.system.service.impl;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.system.domain.HzEnterpriseBatch;
import com.ruoyi.system.domain.HzEnterpriseBill;
import com.ruoyi.system.mapper.HzEnterpriseBillMapper;
import com.ruoyi.system.service.IHzEnterpriseBatchService;
import com.ruoyi.system.service.IHzEnterpriseBillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

/**
 * 企业账单Service业务层处理
 *
 * @author ruoyi
 */
@Service
public class HzEnterpriseBillServiceImpl implements IHzEnterpriseBillService {

    @Autowired
    private HzEnterpriseBillMapper enterpriseBillMapper;

    @Autowired
    private IHzEnterpriseBatchService enterpriseBatchService;

    /**
     * 查询企业账单列表
     *
     * @param enterpriseBill 企业账单
     * @return 企业账单
     */
    @Override
    public List<HzEnterpriseBill> selectEnterpriseBillList(HzEnterpriseBill enterpriseBill) {
        return enterpriseBillMapper.selectEnterpriseBillList(enterpriseBill);
    }

    /**
     * 查询企业账单详情
     *
     * @param billId 账单ID
     * @return 企业账单
     */
    @Override
    public HzEnterpriseBill selectEnterpriseBillById(Long billId) {
        return enterpriseBillMapper.selectEnterpriseBillById(billId);
    }

    /**
     * 新增企业账单
     *
     * @param enterpriseBill 企业账单
     * @return 结果
     */
    @Override
    public int insertEnterpriseBill(HzEnterpriseBill enterpriseBill) {
        return enterpriseBillMapper.insertEnterpriseBill(enterpriseBill);
    }

    /**
     * 修改企业账单
     *
     * @param enterpriseBill 企业账单
     * @return 结果
     */
    @Override
    public int updateEnterpriseBill(HzEnterpriseBill enterpriseBill) {
        return enterpriseBillMapper.updateEnterpriseBill(enterpriseBill);
    }

    /**
     * 批量删除企业账单
     *
     * @param billIds 需要删除的账单ID数组
     * @return 结果
     */
    @Override
    public int deleteEnterpriseBillByIds(Long[] billIds) {
        return enterpriseBillMapper.deleteEnterpriseBillByIds(billIds);
    }

    /**
     * 删除企业账单信息
     *
     * @param billId 账单ID
     * @return 结果
     */
    @Override
    public int deleteEnterpriseBillById(Long billId) {
        return enterpriseBillMapper.deleteEnterpriseBillById(billId);
    }

    /**
     * 根据联系方式查询账单列表
     *
     * @param contactPhone 联系方式
     * @return 企业账单集合
     */
    @Override
    public List<HzEnterpriseBill> selectBillsByContactPhone(String contactPhone) {
        return enterpriseBillMapper.selectBillsByContactPhone(contactPhone);
    }

    /**
     * 根据批次ID查询账单
     *
     * @param batchId 批次ID
     * @return 企业账单
     */
    @Override
    public HzEnterpriseBill selectBillByBatchId(Long batchId) {
        return enterpriseBillMapper.selectBillByBatchId(batchId);
    }

    /**
     * 计算月数（不满一个月算一个月）
     *
     * @param checkInDate  入驻日期
     * @param checkOutDate 截止日期
     * @return 月数
     */
    @Override
    public int calculateMonths(Date checkInDate, Date checkOutDate) {
        if (checkInDate == null || checkOutDate == null) {
            return 0;
        }

        LocalDate start = checkInDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate end = checkOutDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        if (end.isBefore(start)) {
            return 0;
        }

        // 计算年月差异
        int years = end.getYear() - start.getYear();
        int months = end.getMonthValue() - start.getMonthValue();
        int totalMonths = years * 12 + months;

        // 检查日期，如果截止日期的日数 >= 入驻日期的日数，则算满一个月
        // 否则需要额外加一个月（不满一个月算一个月）
        if (end.getDayOfMonth() >= start.getDayOfMonth()) {
            totalMonths += 1;
        } else {
            totalMonths += 1;
        }

        return totalMonths > 0 ? totalMonths : 0;
    }

    /**
     * 生成账单编号
     *
     * @return 账单编号 (格式: QYB + yyyyMMdd + 6位序号)
     */
    @Override
    public String generateBillNo() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String dateStr = sdf.format(new Date());
        // 简单实现：使用时间戳后6位
        String timeStr = String.valueOf(System.currentTimeMillis());
        String seqNo = timeStr.substring(timeStr.length() - 6);
        return "QYB" + dateStr + seqNo;
    }

    /**
     * 审核账单
     *
     * @param billId   账单ID
     * @param approved 是否通过
     * @param approveBy 审核人
     * @return 结果
     */
    @Override
    @Transactional
    public int approveBill(Long billId, boolean approved, String approveBy) {
        HzEnterpriseBill bill = enterpriseBillMapper.selectEnterpriseBillById(billId);
        if (bill == null) {
            throw new ServiceException("账单不存在");
        }

        // 更新账单状态
        bill.setApproveStatus(approved ? "1" : "2");
        bill.setApproveBy(approveBy);
        bill.setApproveTime(new Date());
        if (approved) {
            bill.setBillStatus("1"); // 已审核
        }
        bill.setUpdateTime(new Date());
        int result = enterpriseBillMapper.updateEnterpriseBill(bill);

        // 同步更新批次状态
        HzEnterpriseBatch batch = enterpriseBatchService.selectEnterpriseBatchById(bill.getBatchId());
        if (batch != null) {
            if (approved) {
                batch.setBatchStatus("1"); // 已完成
                batch.setApproveStatus("1");
                batch.setApproveBy(approveBy);
                batch.setApproveTime(new Date());
            } else {
                batch.setApproveStatus("2");
            }
            batch.setUpdateTime(new Date());
            enterpriseBatchService.updateEnterpriseBatch(batch);
        }

        return result;
    }

    /**
     * 支付账单
     *
     * @param billId        账单ID
     * @param payMethod     支付方式
     * @param transactionNo 交易流水号
     * @return 结果
     */
    @Override
    public int payBill(Long billId, String payMethod, String transactionNo) {
        HzEnterpriseBill bill = enterpriseBillMapper.selectEnterpriseBillById(billId);
        if (bill == null) {
            throw new ServiceException("账单不存在");
        }

        if (!"1".equals(bill.getBillStatus())) {
            throw new ServiceException("账单状态不正确，无法支付");
        }

        bill.setBillStatus("2"); // 已支付
        bill.setPayTime(new Date());
        bill.setPayMethod(payMethod);
        bill.setTransactionNo(transactionNo);
        bill.setUpdateTime(new Date());

        return enterpriseBillMapper.updateEnterpriseBill(bill);
    }

    /**
     * 管理端线下支付账单
     *
     * @param billId     账单ID
     * @param payMethod  支付方式
     * @param payVoucher 支付凭证文件路径
     * @return 结果
     */
    @Override
    @Transactional
    public int adminPayBill(Long billId, String payMethod, String payVoucher) {
        HzEnterpriseBill bill = enterpriseBillMapper.selectEnterpriseBillById(billId);
        if (bill == null) {
            throw new ServiceException("账单不存在");
        }

        // 只有已审核状态可以支付
        if (!"1".equals(bill.getBillStatus())) {
            throw new ServiceException("账单状态不正确，只有已审核状态的账单可以支付");
        }

        bill.setBillStatus("2"); // 已支付
        bill.setPayTime(new Date());
        bill.setPayMethod(payMethod);
        bill.setPayVoucher(payVoucher);
        bill.setUpdateTime(new Date());

        return enterpriseBillMapper.updateEnterpriseBill(bill);
    }

    /**
     * 提交入住办理（上传人员名单）
     *
     * @param billId        账单ID
     * @param personnelFile 人员名单文件路径
     * @return 结果
     */
    @Override
    public int submitCheckin(Long billId, String personnelFile) {
        HzEnterpriseBill bill = enterpriseBillMapper.selectEnterpriseBillById(billId);
        if (bill == null) {
            throw new ServiceException("账单不存在");
        }

        // 只有已支付状态可以办理入住
        if (!"2".equals(bill.getBillStatus())) {
            throw new ServiceException("账单状态不正确，只有已支付状态的账单可以办理入住");
        }

        bill.setPersonnelFile(personnelFile);
        bill.setUpdateTime(new Date());

        return enterpriseBillMapper.updateEnterpriseBill(bill);
    }
}
