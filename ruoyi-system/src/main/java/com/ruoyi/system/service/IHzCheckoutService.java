package com.ruoyi.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.system.domain.HzCheckoutApply;
import com.ruoyi.system.domain.HzCheckoutRecord;

import java.util.List;
import java.util.Map;


/**
 * 退租Service接口
 *
 * @author ruoyi
 */
public interface IHzCheckoutService {

    /**
     * 查询退租申请
     *
     * @param applyId 退租申请主键
     * @return 退租申请
     */
    public HzCheckoutApply selectCheckoutApplyByApplyId(Long applyId);

    /**
     * 查询退租申请列表
     *
     * @param hzCheckoutApply 退租申请
     * @return 退租申请集合
     */
    public List<HzCheckoutApply> selectCheckoutApplyList(HzCheckoutApply hzCheckoutApply);

    /**
     * 查询退租申请列表（包含合同和房源完整信息）
     *
     * @param hzCheckoutApply 退租申请查询条件
     * @return 退租申请VO集合（包含合同和房源信息）
     */
    public TableDataInfo selectCheckoutApplyListWithDetails(Page<HzCheckoutApply> page, HzCheckoutApply hzCheckoutApply);

    /**
     * 根据租户ID查询退租申请列表
     *
     * @param tenantId 租户ID
     * @return 退租申请列表
     */
    public List<HzCheckoutApply> selectCheckoutApplyListByTenantId(Long tenantId);

    /**
     * 根据租户ID查询退租申请列表（包含房源详细信息）
     *
     * @param tenantId 租户ID
     * @return 退租申请列表（包含房源信息）
     */
    public List<Map<String, Object>> selectCheckoutApplyListWithHouseInfo(Long tenantId);

    /**
     * 新增退租申请
     *
     * @param hzCheckoutApply 退租申请
     * @return 结果
     */
    public int insertCheckoutApply(HzCheckoutApply hzCheckoutApply);

    /**
     * 修改退租申请
     *
     * @param hzCheckoutApply 退租申请
     * @return 结果
     */
    public int updateCheckoutApply(HzCheckoutApply hzCheckoutApply);

    /**
     * 批量删除退租申请
     *
     * @param applyIds 需要删除的退租申请主键集合
     * @return 结果
     */
    public int deleteCheckoutApplyByApplyIds(Long[] applyIds);

    /**
     * 删除退租申请信息
     *
     * @param applyId 退租申请主键
     * @return 结果
     */
    public int deleteCheckoutApplyByApplyId(Long applyId);

    /**
     * 审批退租申请
     *
     * @param applyId 退租申请ID
     * @param applyStatus 审批状态 (1=通过, 2=拒绝)
     * @param approveOpinion 审批意见
     * @param approveBy 审批人
     * @return 结果
     */
    public int approveCheckoutApply(Long applyId, String applyStatus, String approveOpinion, String approveBy);

    /**
     * 取消退租申请
     *
     * @param applyId 退租申请ID
     * @return 结果
     */
    public int cancelCheckoutApply(Long applyId);

    /**
     * 保存退租费用计算结果
     *
     * @param hzCheckoutApply 退租申请(包含费用信息)
     * @return 结果
     */
    public int saveCheckoutCalculate(HzCheckoutApply hzCheckoutApply);

    /**
     * 查询退租记录
     *
     * @param recordId 退租记录主键
     * @return 退租记录
     */
    public HzCheckoutRecord selectCheckoutRecordByRecordId(Long recordId);

    /**
     * 查询退租记录列表
     *
     * @param hzCheckoutRecord 退租记录
     * @return 退租记录
     */
    public List<HzCheckoutRecord> selectCheckoutRecordList(HzCheckoutRecord hzCheckoutRecord);

    /**
     * 根据申请ID查询退租记录
     *
     * @param applyId 退租申请ID
     * @return 退租记录
     */
    public HzCheckoutRecord selectCheckoutRecordByApplyId(Long applyId);

    /**
     * 新增退租记录
     *
     * @param hzCheckoutRecord 退租记录
     * @return 结果
     */
    public int insertCheckoutRecord(HzCheckoutRecord hzCheckoutRecord);

    /**
     * 提交退租确认
     *
     * @param applyId 退租申请ID
     * @param tenantSignature 租户签名
     * @return 结果
     */
    public int submitCheckoutConfirm(Long applyId, String tenantSignature);

    /**
     * 查询已取消的退租申请（用于再次申请时更新原记录）
     *
     * @param contractId 合同ID
     * @param tenantId 租户ID
     * @return 已取消的退租申请
     */
    public HzCheckoutApply selectCancelledApply(Long contractId, Long tenantId);

    /**
     * 获取退租确认信息（供用户确认页面使用）
     *
     * @param applyId 退租申请ID
     * @return 确认信息（包含申请信息、配套设备、费用明细等）
     */
    public Map<String, Object> getCheckoutConfirmInfo(Long applyId);

    /**
     * 查询合同的账单列表（用于退租审批时查看缴费记录）
     *
     * @param contractId 合同ID
     * @return 账单列表
     */
    public List<Map<String, Object>> selectContractBillList(Long contractId);

    /**
     * 按日精算退租租金（覆盖不满一月/满一月/未缴当期/未入住 4 种场景）
     * <p>
     * 返回体字段：
     * - rentRefund：应退租金（情况一/二/四 场景下 > 0）
     * - currentPeriodOwed：当期未缴按日折算金额（情况三 场景下 > 0，从应退总额中扣）
     * - detail：计算明细（人类可读）
     *
     * @param contractId 合同ID
     * @param planCheckoutDate 计划退租日期（yyyy-MM-dd）
     * @return 计算结果
     */
    public Map<String, Object> calculateRentRefund(Long contractId, String planCheckoutDate);

    /**
     * 管理方确认退租（非阻塞留痕操作）
     *
     * @param applyId 退租申请ID
     * @param opinion 确认意见
     * @return 结果
     */
    public int managerConfirmCheckout(Long applyId, String opinion);
}
