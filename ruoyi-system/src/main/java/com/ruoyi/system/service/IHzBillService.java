package com.ruoyi.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ruoyi.system.domain.HzBill;
import com.ruoyi.system.domain.HzBillVO;

import java.util.List;

/**
 * 账单Service接口
 *
 * @author ruoyi
 */
public interface IHzBillService {

    /**
     * 查询账单
     *
     * @param billId 账单主键
     * @return 账单
     */
    public HzBill selectBillById(Long billId);

    /**
     * 查询账单VO（包含关联信息）
     *
     * @param billId 账单主键
     * @return 账单VO
     */
    public HzBillVO selectBillVOById(Long billId);

    /**
     * 根据账单编号查询账单
     *
     * @param billNo 账单编号
     * @return 账单
     */
    public HzBill selectBillByBillNo(String billNo);

    /**
     * 根据租户ID查询账单列表
     *
     * @param tenantId 租户ID
     * @return 账单列表
     */
    public List<HzBill> selectBillListByTenantId(Long tenantId);

    /**
     * 根据合同ID查询账单列表
     *
     * @param contractId 合同ID
     * @return 账单列表
     */
    public List<HzBill> selectBillListByContractId(Long contractId);

    /**
     * 查询待支付账单列表
     *
     * @param tenantId 租户ID
     * @return 账单列表
     */
    public List<HzBill> selectUnpaidBillListByTenantId(Long tenantId);

    /**
     * 查询逾期账单列表
     *
     * @param tenantId 租户ID
     * @return 账单列表
     */
    public List<HzBill> selectOverdueBillListByTenantId(Long tenantId);

    /**
     * 查询账单列表
     *
     * @param bill 账单
     * @return 账单列表
     */
    public List<HzBill> selectBillList(HzBill bill);

    /**
     * 查询账单VO列表（包含关联信息）
     *
     * @param bill 账单
     * @return 账单VO列表
     */
    public List<HzBillVO> selectBillVOList(HzBill bill);

    /**
     * 分页查询账单VO列表（包含关联信息）
     *
     * @param bill 账单
     * @return 账单VO分页数据
     */
    public IPage<HzBillVO> selectBillVOPage(HzBill bill);

    /**
     * 分页查询账单列表
     *
     * @param bill 账单
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @return 账单分页数据
     */
    public IPage<HzBill> selectBillPage(HzBill bill, int pageNum, int pageSize);

    /**
     * 新增账单
     *
     * @param bill 账单
     * @return 结果
     */
    public int insertBill(HzBill bill);

    /**
     * 修改账单
     *
     * @param bill 账单
     * @return 结果
     */
    public int updateBill(HzBill bill);

    /**
     * 删除账单
     *
     * @param billId 账单主键
     * @return 结果
     */
    public int deleteBillById(Long billId);

    /**
     * 生成账单编号
     *
     * @return 账单编号
     */
    public String generateBillNo();

    /**
     * 更新账单状态
     *
     * @param billId 账单ID
     * @param billStatus 账单状态
     * @return 结果
     */
    public int updateBillStatus(Long billId, String billStatus);

    /**
     * 检查并更新逾期账单
     */
    public void checkAndUpdateOverdueBills();
}
