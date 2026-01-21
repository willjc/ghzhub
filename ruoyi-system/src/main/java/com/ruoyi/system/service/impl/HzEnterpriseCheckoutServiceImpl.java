package com.ruoyi.system.service.impl;

import com.alibaba.fastjson2.JSON;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.HzEnterpriseBatch;
import com.ruoyi.system.domain.HzEnterpriseBill;
import com.ruoyi.system.domain.HzEnterpriseCheckout;
import com.ruoyi.system.mapper.HzEnterpriseBatchHouseMapper;
import com.ruoyi.system.mapper.HzEnterpriseBatchMapper;
import com.ruoyi.system.mapper.HzEnterpriseBillMapper;
import com.ruoyi.system.mapper.HzEnterpriseCheckoutMapper;
import com.ruoyi.system.service.IHzEnterpriseCheckoutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 企业退租申请Service业务层处理
 *
 * @author ruoyi
 */
@Service
public class HzEnterpriseCheckoutServiceImpl implements IHzEnterpriseCheckoutService {

    @Autowired
    private HzEnterpriseCheckoutMapper checkoutMapper;

    @Autowired
    private HzEnterpriseBillMapper billMapper;

    @Autowired
    private HzEnterpriseBatchMapper batchMapper;

    @Autowired
    private HzEnterpriseBatchHouseMapper batchHouseMapper;

    /**
     * 查询企业退租申请列表
     *
     * @param checkout 企业退租申请
     * @return 企业退租申请
     */
    @Override
    public List<HzEnterpriseCheckout> selectCheckoutList(HzEnterpriseCheckout checkout) {
        return checkoutMapper.selectCheckoutList(checkout);
    }

    /**
     * 查询企业退租申请详情
     *
     * @param checkoutId 退租申请ID
     * @return 企业退租申请
     */
    @Override
    public HzEnterpriseCheckout selectCheckoutById(Long checkoutId) {
        return checkoutMapper.selectCheckoutById(checkoutId);
    }

    /**
     * 新增企业退租申请
     *
     * @param checkout 企业退租申请
     * @return 结果
     */
    @Override
    public int insertCheckout(HzEnterpriseCheckout checkout) {
        return checkoutMapper.insertCheckout(checkout);
    }

    /**
     * 修改企业退租申请
     *
     * @param checkout 企业退租申请
     * @return 结果
     */
    @Override
    public int updateCheckout(HzEnterpriseCheckout checkout) {
        return checkoutMapper.updateCheckout(checkout);
    }

    /**
     * 批量删除企业退租申请
     *
     * @param checkoutIds 需要删除的退租申请ID数组
     * @return 结果
     */
    @Override
    public int deleteCheckoutByIds(Long[] checkoutIds) {
        return checkoutMapper.deleteCheckoutByIds(checkoutIds);
    }

    /**
     * 删除企业退租申请信息
     *
     * @param checkoutId 退租申请ID
     * @return 结果
     */
    @Override
    public int deleteCheckoutById(Long checkoutId) {
        return checkoutMapper.deleteCheckoutById(checkoutId);
    }

    /**
     * 根据账单ID查询退租申请
     *
     * @param billId 账单ID
     * @return 企业退租申请
     */
    @Override
    public HzEnterpriseCheckout selectCheckoutByBillId(Long billId) {
        return checkoutMapper.selectCheckoutByBillId(billId);
    }

    /**
     * 根据联系电话查询退租申请列表
     *
     * @param contactPhone 联系电话
     * @return 企业退租申请集合
     */
    @Override
    public List<HzEnterpriseCheckout> selectCheckoutsByContactPhone(String contactPhone) {
        return checkoutMapper.selectCheckoutsByContactPhone(contactPhone);
    }

    /**
     * 生成退租申请编号
     *
     * @return 退租申请编号 (格式: QYTZ + yyyyMMdd + 6位序号)
     */
    @Override
    public String generateCheckoutNo() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String dateStr = sdf.format(new Date());
        // 简单实现：使用时间戳后6位
        String timeStr = String.valueOf(System.currentTimeMillis());
        String seqNo = timeStr.substring(timeStr.length() - 6);
        return "QYTZ" + dateStr + seqNo;
    }

    /**
     * 提交退租申请
     *
     * @param billId 账单ID
     * @return 结果
     */
    @Override
    @Transactional
    public int submitCheckout(Long billId) {
        // 检查是否已经申请过退租
        HzEnterpriseCheckout existing = checkoutMapper.selectCheckoutByBillId(billId);
        if (existing != null) {
            throw new ServiceException("该账单已申请过退租，请勿重复提交");
        }

        // 获取账单信息
        HzEnterpriseBill bill = billMapper.selectEnterpriseBillById(billId);
        if (bill == null) {
            throw new ServiceException("账单不存在");
        }

        // 检查账单状态：必须是已支付且已上传人员名单
        if (!"2".equals(bill.getBillStatus())) {
            throw new ServiceException("账单状态不正确，只有已支付状态可以申请退租");
        }
        if (StringUtils.isEmpty(bill.getPersonnelFile())) {
            throw new ServiceException("请先办理入住手续后再申请退租");
        }

        // 获取批次信息（获取项目名称）
        String projectName = "";
        if (bill.getBatchId() != null) {
            HzEnterpriseBatch batch = batchMapper.selectEnterpriseBatchById(bill.getBatchId());
            if (batch != null && StringUtils.isNotEmpty(batch.getProjectName())) {
                projectName = batch.getProjectName();
            }
        }

        // 获取房源信息
        List<Map<String, Object>> houseDetails = new ArrayList<>();
        List<String> houseNos = new ArrayList<>();
        if (bill.getBatchId() != null) {
            houseDetails = batchHouseMapper.selectHouseDetailsByBatchId(bill.getBatchId());
            for (Map<String, Object> detail : houseDetails) {
                String houseNo = (String) detail.get("house_no");
                if (StringUtils.isNotEmpty(houseNo)) {
                    houseNos.add(houseNo);
                }
            }
        }

        // 构建房源信息JSON
        Map<String, Object> houseInfoMap = new HashMap<>();
        houseInfoMap.put("houseNos", houseNos);
        houseInfoMap.put("houseDetails", houseDetails);
        String houseInfoJson = JSON.toJSONString(houseInfoMap);

        // 创建退租申请记录
        HzEnterpriseCheckout checkout = new HzEnterpriseCheckout();
        checkout.setCheckoutNo(generateCheckoutNo());
        checkout.setBillId(billId);
        checkout.setBatchId(bill.getBatchId());
        checkout.setBatchNo(bill.getBatchNo());
        checkout.setBatchName(bill.getBatchName());
        checkout.setEnterpriseName(bill.getEnterpriseName());
        checkout.setContactPerson(bill.getContactPerson());
        checkout.setContactPhone(bill.getContactPhone());
        checkout.setProjectName(projectName);
        checkout.setHouseInfo(houseInfoJson);
        checkout.setHouseCount(bill.getHouseCount());
        checkout.setTotalAmount(bill.getTotalAmount());
        checkout.setDiscountAmount(bill.getDiscountAmount());
        checkout.setFinalAmount(bill.getFinalAmount());
        checkout.setCheckInDate(bill.getCheckInDate());
        checkout.setCheckOutDate(bill.getCheckOutDate());
        checkout.setMonths(bill.getMonths());
        checkout.setCheckoutStatus("0"); // 已申请
        checkout.setApplyTime(new Date());
        checkout.setCreateTime(new Date());
        checkout.setDelFlag("0");

        return checkoutMapper.insertCheckout(checkout);
    }
}
