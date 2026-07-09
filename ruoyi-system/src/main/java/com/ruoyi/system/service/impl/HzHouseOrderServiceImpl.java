package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.HzBatchHouse;
import com.ruoyi.system.domain.HzBatchTenant;
import com.ruoyi.system.domain.vo.BatchPreferenceVo;
import com.ruoyi.system.domain.HzBill;
import com.ruoyi.system.domain.HzContract;
import com.ruoyi.system.domain.HzDocument;
import com.ruoyi.system.domain.HzHouse;
import com.ruoyi.system.domain.HzHouseOrder;
import com.ruoyi.system.domain.HzUser;
import com.ruoyi.system.mapper.HzBatchHouseMapper;
import com.ruoyi.system.mapper.HzBatchTenantMapper;
import com.ruoyi.system.mapper.HzBillMapper;
import com.ruoyi.system.mapper.HzContractMapper;
import com.ruoyi.system.mapper.HzDocumentMapper;
import com.ruoyi.system.mapper.HzHouseMapper;
import com.ruoyi.system.mapper.HzHouseOrderMapper;
import com.ruoyi.system.mapper.HzUserMapper;
import com.ruoyi.system.service.IHzHouseOrderService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 选房预订单Service业务层处理
 *
 * @author ruoyi
 */
@Service
public class HzHouseOrderServiceImpl
        extends ServiceImpl<HzHouseOrderMapper, HzHouseOrder>
        implements IHzHouseOrderService {

    @Autowired
    private HzHouseOrderMapper orderMapper;

    @Autowired
    private HzHouseMapper houseMapper;

    @Autowired
    private HzDocumentMapper documentMapper;

    @Autowired
    private HzContractMapper contractMapper;

    @Autowired
    private HzBillMapper billMapper;

    @Autowired
    private HzUserMapper userMapper;

    @Autowired
    private HzBatchTenantMapper batchTenantMapper;

    @Autowired
    private HzBatchHouseMapper batchHouseMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult createOrder(Long tenantId, Long houseId) {
        // 1. 检查是否有进行中的活跃订单
        List<HzHouseOrder> activeOrders = list(new LambdaQueryWrapper<HzHouseOrder>()
                .eq(HzHouseOrder::getTenantId, tenantId)
                .eq(HzHouseOrder::getHouseId, houseId)
                .in(HzHouseOrder::getOrderStatus, "0", "1", "2")
                .eq(HzHouseOrder::getDelFlag, "0"));
        if (!activeOrders.isEmpty()) {
            // 1.1 尝试清理已过期的卡死订单（e签宝超时/失败导致预订单停留且锁已过期）
            Date now = new Date();
            boolean hasUnexpired = false;
            for (HzHouseOrder existOrder : activeOrders) {
                boolean expired = existOrder.getLockExpireTime() != null
                        && existOrder.getLockExpireTime().before(now);
                if (!expired) {
                    hasUnexpired = true;
                    break;
                }
                // 检查关联合同是否已完成签署（status >= 2），已签署的不能作废
                if (existOrder.getContractId() != null) {
                    HzContract contract = contractMapper.selectById(existOrder.getContractId());
                    if (contract != null) {
                        String cs = contract.getContractStatus();
                        if (cs != null && Integer.parseInt(cs) >= 2 && "0".equals(contract.getDelFlag())) {
                            hasUnexpired = true;
                            break;
                        }
                    }
                }
            }
            if (hasUnexpired) {
                return AjaxResult.error("您已预订过该房源，请勿重复操作");
            }
            // 清理所有过期订单及其草稿合同
            for (HzHouseOrder existOrder : activeOrders) {
                // 作废草稿合同
                if (existOrder.getContractId() != null) {
                    HzContract contract = contractMapper.selectById(existOrder.getContractId());
                    if (contract != null && "0".equals(contract.getDelFlag())) {
                        contract.setDelFlag("2");
                        contract.setUpdateTime(now);
                        contractMapper.updateById(contract);
                    }
                }
                // 取消预订单
                existOrder.setOrderStatus("5");
                existOrder.setUpdateTime(now);
                updateById(existOrder);
                // 普通订单（非批次配租）：释放房源为空置
                if (!"1".equals(existOrder.getIsBatchAlloc())) {
                    houseMapper.update(null, new LambdaUpdateWrapper<HzHouse>()
                            .eq(HzHouse::getHouseId, houseId)
                            .eq(HzHouse::getHouseStatus, "1")
                            .set(HzHouse::getHouseStatus, "0"));
                }
            }
        }

        // 2. 提前判断是否为批次配租用户
        boolean isBatch = checkIsBatchTenant(tenantId);

        // 3. 房源锁定逻辑（批次配租 vs 普通用户）
        if (isBatch) {
            // 批次配租用户：校验该房源是否确实分配给了该用户
            if (!verifyBatchHouseAssignment(tenantId, houseId)) {
                return AjaxResult.error("该房源未分配给您，无法选定");
            }
            // 房源已在批次审批通过时设为'1'（已预订），无需再次lockHouse
        } else {
            // 普通用户：原子锁定房源（house_status: 空置→已预订）
            int locked = orderMapper.lockHouse(houseId);
            if (locked == 0) {
                return AjaxResult.error("该房源已被他人选中，请重新选择");
            }
        }

        // 4. 查询房源信息
        HzHouse house = houseMapper.selectById(houseId);

        // 5. 创建预订单
        HzHouseOrder order = new HzHouseOrder();
        order.setOrderNo(generateOrderNo());
        order.setTenantId(tenantId);
        order.setHouseId(houseId);
        order.setProjectId(house.getProjectId());
        order.setDepositAmount(house.getDeposit() != null ? house.getDeposit() : BigDecimal.ZERO);
        order.setRentPrice(house.getRentPrice() != null ? house.getRentPrice() : BigDecimal.ZERO);
        order.setOrderStatus("0"); // 待签约
        order.setIsBatchAlloc(isBatch ? "1" : "0");

        Calendar cal = Calendar.getInstance();
        if (!isBatch) {
            // 普通用户：锁定10分钟
            cal.add(Calendar.MINUTE, 10);
        } else {
            // 批次配租用户：截止日期为配租批次的入住结束日期(entry_end_date) 23:59:59
            try {
                BatchPreferenceVo batchPref = batchHouseMapper.selectBatchPreferenceByHouseId(houseId);
                if (batchPref != null && batchPref.getEntryEndDate() != null) {
                    LocalDate endDate = LocalDate.parse(batchPref.getEntryEndDate(), DateTimeFormatter.ISO_LOCAL_DATE);
                    LocalDateTime endDateTime = endDate.atTime(23, 59, 59);
                    cal.setTime(Date.from(endDateTime.atZone(ZoneId.systemDefault()).toInstant()));
                } else {
                    // 查询不到批次信息时回退为远期时间
                    cal.set(2099, Calendar.DECEMBER, 31, 23, 59, 59);
                }
            } catch (Exception e) {
                cal.set(2099, Calendar.DECEMBER, 31, 23, 59, 59);
            }
        }
        order.setLockExpireTime(cal.getTime());
        order.setDelFlag("0");
        order.setCreateTime(new Date());

        save(order);

        Map<String, Object> data = new HashMap<>();
        data.put("orderNo", order.getOrderNo());
        data.put("lockExpireTime", order.getLockExpireTime() != null ? order.getLockExpireTime().getTime() : null);
        data.put("depositAmount", order.getDepositAmount());
        return AjaxResult.success(data);
    }

    @Override
    public Map<String, Object> getOrderStatus(String orderNo) {
        HzHouseOrder order = getOne(new LambdaQueryWrapper<HzHouseOrder>()
                .eq(HzHouseOrder::getOrderNo, orderNo)
                .eq(HzHouseOrder::getDelFlag, "0"));
        if (order == null) {
            return null;
        }

        Map<String, Object> result = new HashMap<>();
        result.put("orderNo", order.getOrderNo());
        result.put("orderStatus", order.getOrderStatus());

        long now = System.currentTimeMillis();
        if (order.getLockExpireTime() != null) {
            result.put("remainSeconds", Math.max(0L,
                    (order.getLockExpireTime().getTime() - now) / 1000));
        }
        if (order.getDocUploadExpireTime() != null) {
            result.put("docRemainSeconds", Math.max(0L,
                    (order.getDocUploadExpireTime().getTime() - now) / 1000));
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelOrder(String orderNo, Long tenantId) {
        HzHouseOrder order = getOne(new LambdaQueryWrapper<HzHouseOrder>()
                .eq(HzHouseOrder::getOrderNo, orderNo)
                .eq(HzHouseOrder::getTenantId, tenantId)
                .eq(HzHouseOrder::getDelFlag, "0"));
        if (order == null) {
            return;
        }
        order.setOrderStatus("5"); // 已取消
        order.setUpdateTime(new Date());
        updateById(order);
        // 批次配租订单：房源回退到'3'(修缮中)；普通订单：房源释放为'0'(空置)
        if ("1".equals(order.getIsBatchAlloc())) {
            houseMapper.update(null, new com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<HzHouse>()
                    .eq(HzHouse::getHouseId, order.getHouseId())
                    .eq(HzHouse::getHouseStatus, "1")
                    .set(HzHouse::getHouseStatus, "3"));
        } else {
            orderMapper.releaseHouse(order.getHouseId());
        }
    }

    @Override
    public List<HzHouseOrder> getPendingUploadOrders(Long tenantId) {
        List<HzHouseOrder> orders = list(new LambdaQueryWrapper<HzHouseOrder>()
                .eq(HzHouseOrder::getTenantId, tenantId)
                .eq(HzHouseOrder::getOrderStatus, "2")
                .eq(HzHouseOrder::getIsBatchAlloc, "0")
                .eq(HzHouseOrder::getDelFlag, "0"));
        long now = System.currentTimeMillis();
        orders.forEach(o -> {
            if (o.getDocUploadExpireTime() != null) {
                o.setDocRemainSeconds(Math.max(0L,
                        (o.getDocUploadExpireTime().getTime() - now) / 1000));
            }
        });
        return orders;
    }

    @Override
    public Map<String, Object> checkinCheck(Long tenantId) {
        Map<String, Object> result = new HashMap<>();

        // 1. 查找该用户最新的已签署合同
        HzContract contract = contractMapper.selectOne(
            new LambdaQueryWrapper<HzContract>()
                .eq(HzContract::getTenantId, tenantId)
                .in(HzContract::getContractStatus, "2", "3")
                .eq(HzContract::getDelFlag, "0")
                .orderByDesc(HzContract::getCreateTime)
                .last("LIMIT 1"));

        if (contract == null) {
            result.put("canCheckin", false);
            result.put("depositPaid", false);
            result.put("materialApproved", false);
            result.put("firstRentPaid", false);
            result.put("blockMsg", "未找到有效合同");
            return result;
        }

        // 2. 检查押金是否已缴
        HzBill depositBill = billMapper.selectOne(
            new LambdaQueryWrapper<HzBill>()
                .eq(HzBill::getContractId, contract.getContractId())
                .eq(HzBill::getBillType, "1")
                .eq(HzBill::getDelFlag, "0")
                .last("LIMIT 1"));
        boolean depositPaid = depositBill != null && "1".equals(depositBill.getBillStatus());

        // 3. 检查资料是否已审核通过（至少一条通过）
        List<HzDocument> docs = documentMapper.selectList(
            new LambdaQueryWrapper<HzDocument>()
                .eq(HzDocument::getTenantId, tenantId)
                .eq(HzDocument::getDelFlag, "0"));
        boolean materialApproved = docs != null && docs.stream()
                .anyMatch(d -> "1".equals(d.getAuditStatus()));

        // 4. 检查第一期房租是否已缴
        HzBill firstRentBill = billMapper.selectOne(
            new LambdaQueryWrapper<HzBill>()
                .eq(HzBill::getContractId, contract.getContractId())
                .eq(HzBill::getBillType, "2")
                .eq(HzBill::getDelFlag, "0")
                .orderByAsc(HzBill::getBillDate)
                .last("LIMIT 1"));
        boolean firstRentPaid = firstRentBill != null && "1".equals(firstRentBill.getBillStatus());

        // 入住条件：押金已缴 + 首期房租已缴（资料审核不阻断入住）
        boolean canCheckin = depositPaid && firstRentPaid;

        String blockMsg = "";
        if (!depositPaid)        blockMsg = "请先缴纳押金";
        else if (!firstRentPaid) blockMsg = "请先缴纳首期房租";

        result.put("canCheckin",       canCheckin);
        result.put("depositPaid",      depositPaid);
        result.put("materialApproved", materialApproved);
        result.put("firstRentPaid",    firstRentPaid);
        result.put("blockMsg",         blockMsg);
        result.put("contractId",       contract.getContractId());
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void onDepositPaid(String orderNo) {
        HzHouseOrder order = getOne(new LambdaQueryWrapper<HzHouseOrder>()
                .eq(HzHouseOrder::getOrderNo, orderNo)
                .eq(HzHouseOrder::getDelFlag, "0"));
        if (order == null || !"1".equals(order.getOrderStatus())) {
            return;
        }

        if ("1".equals(order.getIsBatchAlloc())) {
            // 配租用户：押金缴清后直接完成
            order.setOrderStatus("3");
        } else {
            // 自选用户：押金缴清后进入待上传资料阶段，72小时倒计时
            order.setOrderStatus("2");
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.HOUR, 72);
            order.setDocUploadExpireTime(cal.getTime());
        }
        order.setUpdateTime(new Date());
        updateById(order);

        // 押金缴清后，房源状态从「已预订(1)」更新为「已出租(2)」
        if (order.getHouseId() != null) {
            houseMapper.update(null, new com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<HzHouse>()
                    .eq(HzHouse::getHouseId, order.getHouseId())
                    .eq(HzHouse::getHouseStatus, "1")  // 只在「已预订」状态下才更新，防止误改
                    .set(HzHouse::getHouseStatus, "2")); // 2=已出租
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void onDocumentsApproved(Long tenantId) {
        // 检查工作证明(type=3)和学历证明(type=2)是否均已审核通过
        long approvedCount = documentMapper.selectCount(
                new LambdaQueryWrapper<HzDocument>()
                        .eq(HzDocument::getTenantId, tenantId)
                        .in(HzDocument::getDocumentType, "2", "3")
                        .eq(HzDocument::getAuditStatus, "1")
                        .eq(HzDocument::getDelFlag, "0"));
        if (approvedCount >= 2) {
            // 两类资料均已通过，完成订单
            update(null, new LambdaUpdateWrapper<HzHouseOrder>()
                    .eq(HzHouseOrder::getTenantId, tenantId)
                    .eq(HzHouseOrder::getOrderStatus, "2")
                    .eq(HzHouseOrder::getDelFlag, "0")
                    .set(HzHouseOrder::getOrderStatus, "3")
                    .set(HzHouseOrder::getUpdateTime, new Date()));
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void processExpiredOrders() {
        List<HzHouseOrder> expired = orderMapper.selectExpiredOrders();
        for (HzHouseOrder order : expired) {
            order.setOrderStatus("4"); // 已过期
            order.setUpdateTime(new Date());
            updateById(order);
            orderMapper.releaseHouse(order.getHouseId());

            // 同步推关联合同到「6 超时失效」（仅从 0草稿/1待签署 推进，避免误改已签署/履行中等状态）
            if (order.getContractId() != null && order.getContractId() > 0) {
                contractMapper.update(null, new LambdaUpdateWrapper<HzContract>()
                        .eq(HzContract::getContractId, order.getContractId())
                        .in(HzContract::getContractStatus, "0", "1")
                        .set(HzContract::getContractStatus, "6")
                        .set(HzContract::getUpdateTime, new Date()));
            }
        }
    }

    @Override
    public void tryAdvanceContractToFulfilling(Long contractId) {
        if (contractId == null) return;
        HzContract contract = contractMapper.selectById(contractId);
        if (contract == null) return;

        // 仅允许从「1 待签署」或「2 已签署」推进，避免回退或对已履行/已到期/已解约/已超时合同二次推进
        String cs = contract.getContractStatus();
        if (!"1".equals(cs) && !"2".equals(cs)) return;

        // 条件1：押金账单是否已付清（续租合同 contract_type='2' 没有押金账单，跳过此条件）
        boolean depositOk;
        if ("2".equals(contract.getContractType())) {
            depositOk = true;
        } else {
            Long paidDeposit = billMapper.selectCount(new LambdaQueryWrapper<HzBill>()
                    .eq(HzBill::getContractId, contractId)
                    .eq(HzBill::getBillType, "1")
                    .eq(HzBill::getBillStatus, "1")
                    .eq(HzBill::getDelFlag, "0"));
            depositOk = paidDeposit != null && paidDeposit > 0;
        }
        if (!depositOk) return;

        // 条件2：首期租金账单是否已付清（按 bill_date 升序取第一条 bill_amount>0 的）
        HzBill firstRent = billMapper.selectOne(new LambdaQueryWrapper<HzBill>()
                .eq(HzBill::getContractId, contractId)
                .eq(HzBill::getBillType, "2")
                .eq(HzBill::getDelFlag, "0")
                .gt(HzBill::getBillAmount, BigDecimal.ZERO)
                .orderByAsc(HzBill::getBillDate)
                .last("LIMIT 1"));
        if (firstRent == null || !"1".equals(firstRent.getBillStatus())) return;

        // 双条件齐全 → 推进合同到「3 履行中」
        int updated = contractMapper.update(null, new LambdaUpdateWrapper<HzContract>()
                .eq(HzContract::getContractId, contractId)
                .in(HzContract::getContractStatus, "1", "2")
                .set(HzContract::getContractStatus, "3")
                .set(HzContract::getUpdateTime, new Date()));

        // 合同成功推进到履行中后，同步将房源状态更新为「2 已出租」
        if (updated > 0 && contract.getHouseId() != null) {
            houseMapper.update(null, new com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<HzHouse>()
                    .eq(HzHouse::getHouseId, contract.getHouseId())
                    .in(HzHouse::getHouseStatus, "0", "1") // 空置或已预订 → 已出租
                    .set(HzHouse::getHouseStatus, "2"));
        }
    }

    /**
     * 生成预订单号：HO + 时间戳 + 4位随机数
     */
    private String generateOrderNo() {
        String ts = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        int rand = (int) (Math.random() * 9000) + 1000;
        return "HO" + ts + rand;
    }

    /**
     * 判断指定租户是否为批次配租用户
     * 依据：hz_user.id_card 命中 hz_batch_tenant（未删除）任一记录
     */
    private boolean checkIsBatchTenant(Long tenantId) {
        if (tenantId == null) return false;
        HzUser user = userMapper.selectById(tenantId);
        if (user == null || !StringUtils.hasText(user.getIdCard())) return false;
        QueryWrapper<HzBatchTenant> wrapper = new QueryWrapper<>();
        wrapper.eq("id_card", user.getIdCard())
                .eq("del_flag", "0")
                .last("LIMIT 1");
        Long count = batchTenantMapper.selectCount(wrapper);
        return count != null && count > 0;
    }

    /**
     * 校验批次配租房源归属：该房源是否确实分配给了该用户
     * 通过 hz_user.id_card → hz_batch_tenant.id → hz_batch_house.tenant_id + house_id 三表匹配
     */
    private boolean verifyBatchHouseAssignment(Long tenantId, Long houseId) {
        if (tenantId == null || houseId == null) return false;
        HzUser user = userMapper.selectById(tenantId);
        if (user == null || !StringUtils.hasText(user.getIdCard())) return false;

        // 查出该用户在 hz_batch_tenant 中的所有记录ID
        QueryWrapper<HzBatchTenant> tenantWrapper = new QueryWrapper<>();
        tenantWrapper.eq("id_card", user.getIdCard())
                .eq("del_flag", "0");
        List<HzBatchTenant> batchTenants = batchTenantMapper.selectList(tenantWrapper);
        if (batchTenants == null || batchTenants.isEmpty()) return false;

        // 检查 hz_batch_house 中是否存在 tenant_id 匹配且 house_id 匹配的记录
        List<Long> tenantIds = new java.util.ArrayList<>();
        for (HzBatchTenant bt : batchTenants) {
            tenantIds.add(bt.getId());
        }
        QueryWrapper<HzBatchHouse> houseWrapper = new QueryWrapper<>();
        houseWrapper.in("tenant_id", tenantIds)
                .eq("house_id", houseId)
                .eq("del_flag", "0")
                .last("LIMIT 1");
        Long matchCount = batchHouseMapper.selectCount(houseWrapper);
        return matchCount != null && matchCount > 0;
    }
}
