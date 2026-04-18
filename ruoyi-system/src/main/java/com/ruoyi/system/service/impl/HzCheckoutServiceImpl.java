package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.HzCheckoutApply;
import com.ruoyi.system.domain.HzCheckoutApplyVO;
import com.ruoyi.system.domain.HzCheckoutRecord;
import com.ruoyi.system.domain.HzContract;
import com.ruoyi.system.domain.HzHouse;
import com.ruoyi.system.domain.HzProject;
import com.ruoyi.system.domain.HzBuilding;
import com.ruoyi.system.domain.HzUnit;
import com.ruoyi.system.domain.HzCheckIn;
import com.ruoyi.system.domain.HzUser;
import com.ruoyi.system.mapper.HzCheckoutApplyMapper;
import com.ruoyi.system.mapper.HzCheckoutRecordMapper;
import com.ruoyi.system.mapper.HzContractMapper;
import com.ruoyi.system.mapper.HzHouseMapper;
import com.ruoyi.system.mapper.HzProjectMapper;
import com.ruoyi.system.mapper.HzCheckInMapper;
import com.ruoyi.system.mapper.HzBillMapper;
import com.ruoyi.system.mapper.HzUserMapper;
import com.ruoyi.system.mapper.HzRefundApplyMapper;
import com.ruoyi.system.mapper.HzBuildingMapper;
import com.ruoyi.system.mapper.HzUnitMapper;
import com.ruoyi.system.domain.HzRefundApply;
import com.ruoyi.system.service.IHzCheckoutService;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

/**
 * 退租Service业务层处理
 *
 * @author ruoyi
 */
@Service
public class HzCheckoutServiceImpl extends ServiceImpl<HzCheckoutApplyMapper, HzCheckoutApply> implements IHzCheckoutService {
    private static final Logger logger = LoggerFactory.getLogger(HzCheckoutServiceImpl.class);

    @Autowired
    private HzCheckoutApplyMapper checkoutApplyMapper;

    @Autowired
    private HzCheckoutRecordMapper checkoutRecordMapper;

    @Autowired
    private HzContractMapper contractMapper;

    @Autowired
    private HzHouseMapper houseMapper;

    @Autowired
    private HzProjectMapper projectMapper;

    @Autowired
    private HzCheckInMapper checkInMapper;

    @Autowired
    private HzBillMapper billMapper;

    @Autowired
    private HzUserMapper userMapper;

    @Autowired
    private HzRefundApplyMapper refundApplyMapper;

    @Autowired
    private HzBuildingMapper buildingMapper;

    @Autowired
    private HzUnitMapper unitMapper;

    @Override
    public HzCheckoutApply selectCheckoutApplyByApplyId(Long applyId) {
        return checkoutApplyMapper.selectById(applyId);
    }

    @Override
    public List<HzCheckoutApply> selectCheckoutApplyList(HzCheckoutApply hzCheckoutApply) {
        LambdaQueryWrapper<HzCheckoutApply> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HzCheckoutApply::getDelFlag, "0")
               .eq(hzCheckoutApply.getApplyStatus() != null, HzCheckoutApply::getApplyStatus, hzCheckoutApply.getApplyStatus())
               .orderByDesc(HzCheckoutApply::getApplyTime); // 改用申请时间排序
        return this.list(wrapper);
    }

    @Override
    public TableDataInfo selectCheckoutApplyListWithDetails(Page<HzCheckoutApply> page, HzCheckoutApply hzCheckoutApply) {
        // 1. 用 MyBatis-Plus 分页查询退租申请列表
        LambdaQueryWrapper<HzCheckoutApply> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HzCheckoutApply::getDelFlag, "0")
               .eq(hzCheckoutApply.getApplyStatus() != null, HzCheckoutApply::getApplyStatus, hzCheckoutApply.getApplyStatus())
               .orderByDesc(HzCheckoutApply::getApplyTime);
        IPage<HzCheckoutApply> pageResult = this.page(page, wrapper);
        List<HzCheckoutApply> applyList = pageResult.getRecords();

        List<HzCheckoutApplyVO> voList = new ArrayList<>();

        // 2. 遍历申请列表，填充合同和房源信息
        for (HzCheckoutApply apply : applyList) {
            HzCheckoutApplyVO vo = new HzCheckoutApplyVO();

            // 复制退租申请基本信息
            vo.setApplyId(apply.getApplyId());
            vo.setContractId(apply.getContractId());
            vo.setTenantId(apply.getTenantId());
            vo.setHouseId(apply.getHouseId());
            vo.setApplyTime(apply.getApplyTime());
            vo.setPlanCheckoutDate(apply.getPlanCheckoutDate());
            vo.setCheckoutReason(apply.getCheckoutReason());
            vo.setApplyStatus(apply.getApplyStatus());
            vo.setApproveBy(apply.getApproveBy());
            vo.setApproveTime(apply.getApproveTime());
            vo.setApproveOpinion(apply.getApproveOpinion());

            // 复制费用信息
            vo.setPenaltyAmount(apply.getPenaltyAmount());
            vo.setUnpaidBills(apply.getUnpaidBills());
            vo.setDepositRefund(apply.getDepositRefund());
            vo.setWaterFee(apply.getWaterFee());
            vo.setElectricFee(apply.getElectricFee());
            vo.setGasFee(apply.getGasFee());
            vo.setHeatingFee(apply.getHeatingFee());
            vo.setPropertyFee(apply.getPropertyFee());
            vo.setDamageDeduction(apply.getDamageDeduction());
            vo.setDamageDescription(apply.getDamageDescription());
            vo.setRefundAmount(apply.getRefundAmount());

            // 复制表读数
            vo.setMeterReadingWater(apply.getMeterReadingWater());
            vo.setMeterReadingElectric(apply.getMeterReadingElectric());
            vo.setMeterReadingGas(apply.getMeterReadingGas());
            vo.setKeyReturned(apply.getKeyReturned());

            // 复制签名信息
            vo.setTenantSignature(apply.getTenantSignature());

            // 3. 查询合同信息
            if (apply.getContractId() != null) {
                HzContract contract = contractMapper.selectById(apply.getContractId());
                if (contract != null) {
                    vo.setContractNo(contract.getContractNo());
                    vo.setStartDate(contract.getStartDate());
                    vo.setEndDate(contract.getEndDate());
                    vo.setRentMonths(contract.getRentMonths());
                    vo.setRentPrice(contract.getRentPrice());
                    vo.setDeposit(contract.getDeposit());
                    vo.setPaymentCycle(contract.getPaymentCycle());

                    // 设置缴费周期文本
                    String paymentCycleText = getPaymentCycleText(contract.getPaymentCycle());
                    vo.setPaymentCycleText(paymentCycleText);
                }
            }

            // 4. 查询房源信息
            if (apply.getHouseId() != null) {
                HzHouse house = houseMapper.selectById(apply.getHouseId());
                if (house != null) {
                    vo.setHouseCode(house.getHouseCode());
                    vo.setHouseNo(house.getHouseNo());
                    vo.setFloor(house.getFloor());
                    vo.setHouseTypeName(house.getHouseTypeName());
                    vo.setArea(house.getArea());
                    vo.setOrientation(house.getOrientation());
                    vo.setDecoration(house.getDecoration());
                    vo.setFacilities(house.getFacilities());

                    // 获取项目信息
                    if (house.getProjectId() != null) {
                        HzProject project = projectMapper.selectById(house.getProjectId());
                        if (project != null) {
                            vo.setProjectName(project.getProjectName());
                        }
                    }
                    // 获取楼栋信息
                    if (house.getBuildingId() != null) {
                        HzBuilding building = buildingMapper.selectById(house.getBuildingId());
                        if (building != null) {
                            vo.setBuildingName(building.getBuildingName());
                        }
                    }
                    // 获取单元信息
                    if (house.getUnitId() != null) {
                        HzUnit unit = unitMapper.selectById(house.getUnitId());
                        if (unit != null) {
                            vo.setUnitName(unit.getUnitName());
                        }
                    }
                }
            }

            voList.add(vo);
        }

        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(200);
        rspData.setMsg("查询成功");
        rspData.setRows(voList);
        rspData.setTotal(pageResult.getTotal());
        return rspData;
    }

    /**
     * 获取缴费周期文本
     */
    private String getPaymentCycleText(String paymentCycle) {
        if (paymentCycle == null) {
            return "未知";
        }
        switch (paymentCycle) {
            case "1": return "押一付一";
            case "2": return "押一付二";
            case "3": return "押一付三";
            case "6": return "半年付";
            case "12": return "年付";
            default: return "其他";
        }
    }

    @Override
    public List<HzCheckoutApply> selectCheckoutApplyListByTenantId(Long tenantId) {
        return checkoutApplyMapper.selectByTenantId(tenantId);
    }

    @Override
    public List<Map<String, Object>> selectCheckoutApplyListWithHouseInfo(Long tenantId) {
        // 1. 获取退租申请列表
        List<HzCheckoutApply> applyList = checkoutApplyMapper.selectByTenantId(tenantId);
        List<Map<String, Object>> resultList = new ArrayList<>();

        for (HzCheckoutApply apply : applyList) {
            // 2. 查找对应的入住单，获取房源信息
            LambdaQueryWrapper<HzCheckIn> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(HzCheckIn::getContractId, apply.getContractId())
                   .eq(HzCheckIn::getDelFlag, "0")
                   .orderByDesc(HzCheckIn::getCreateTime)
                   .last("LIMIT 1");
            HzCheckIn checkIn = checkInMapper.selectOne(wrapper);

            // 3. 如果没有找到对应的入住单，跳过这条退租申请（不返回给前端）
            if (checkIn == null) {
                continue;
            }

            // 4. 有入住单，组装返回数据
            Map<String, Object> item = new HashMap<>();

            // 基本信息
            item.put("applyId", apply.getApplyId());
            item.put("recordId", checkIn.getRecordId()); // 添加入住记录ID，用于再次退租
            item.put("contractId", apply.getContractId());
            item.put("tenantId", apply.getTenantId());
            item.put("houseId", apply.getHouseId());
            item.put("applyStatus", apply.getApplyStatus());
            item.put("applyTime", apply.getApplyTime());
            item.put("planCheckoutDate", apply.getPlanCheckoutDate());
            item.put("checkoutReason", apply.getCheckoutReason());

            // 查询合同信息，获取合同编号和合同类型
            HzContract contract = contractMapper.selectById(apply.getContractId());
            if (contract != null) {
                item.put("contractNo", contract.getContractNo());
                item.put("contractType", contract.getContractType());
                item.put("isRenewed", contract.getIsRenewed());
            } else {
                item.put("contractNo", "");
                item.put("contractType", "");
                item.put("isRenewed", "0");
            }

            // 状态映射
            String statusText = getStatusText(apply.getApplyStatus());
            item.put("statusText", statusText);

            // 5. 从入住单的 remark 字段解析房源信息
            if (StringUtils.isNotEmpty(checkIn.getRemark())) {
                String remark = checkIn.getRemark();
                item.put("community", extractInfo(remark, "项目："));
                item.put("room", extractInfo(remark, "房间："));
                item.put("rentPeriod", extractInfo(remark, "租期："));
                item.put("rent", extractInfo(remark, "月租金："));
                item.put("deposit", extractInfo(remark, "押金："));
            } else {
                // 如果入住单没有备注信息，设置默认值
                item.put("community", "");
                item.put("room", "");
                item.put("rentPeriod", "");
                item.put("rent", "");
                item.put("deposit", "");
            }

            resultList.add(item);
        }

        return resultList;
    }

    /**
     * 从备注中提取信息
     */
    private String extractInfo(String remark, String key) {
        if (StringUtils.isEmpty(remark) || StringUtils.isEmpty(key)) {
            return "";
        }
        int index = remark.indexOf(key);
        if (index != -1) {
            int start = index + key.length();
            int end = remark.indexOf("|", start);
            if (end == -1) {
                end = remark.length();
            }
            return remark.substring(start, end).trim();
        }
        return "";
    }

    /**
     * 获取状态文本
     */
    private String getStatusText(String status) {
        if (StringUtils.isEmpty(status)) {
            return "未知";
        }
        switch (status) {
            case "0":
                return "审批中";
            case "1":
                return "审批通过";
            case "2":
                return "审批驳回";
            case "3":
                return "已取消";
            case "4":
                return "待确认";
            case "5":
                return "已确认";
            default:
                return "未知";
        }
    }

    @Override
    public int insertCheckoutApply(HzCheckoutApply hzCheckoutApply) {
        hzCheckoutApply.setDelFlag("0");
        if (StringUtils.isEmpty(hzCheckoutApply.getApplyStatus())) {
            hzCheckoutApply.setApplyStatus("0"); // 默认审批中
        }
        if (hzCheckoutApply.getApplyTime() == null) {
            hzCheckoutApply.setApplyTime(new Date());
        }
        return this.save(hzCheckoutApply) ? 1 : 0;
    }

    @Override
    public int updateCheckoutApply(HzCheckoutApply hzCheckoutApply) {
        return this.updateById(hzCheckoutApply) ? 1 : 0;
    }

    @Override
    public int deleteCheckoutApplyByApplyIds(Long[] applyIds) {
        int count = 0;
        for (Long applyId : applyIds) {
            count += deleteCheckoutApplyByApplyId(applyId);
        }
        return count;
    }

    @Override
    public int deleteCheckoutApplyByApplyId(Long applyId) {
        LambdaUpdateWrapper<HzCheckoutApply> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(HzCheckoutApply::getApplyId, applyId)
               .set(HzCheckoutApply::getDelFlag, "2");
        return this.update(wrapper) ? 1 : 0;
    }

    @Override
    @Transactional
    public int approveCheckoutApply(Long applyId, String applyStatus, String approveOpinion, String approveBy) {
        LambdaUpdateWrapper<HzCheckoutApply> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(HzCheckoutApply::getApplyId, applyId)
               .set(HzCheckoutApply::getApplyStatus, applyStatus)
               .set(HzCheckoutApply::getApproveBy, approveBy)
               .set(HzCheckoutApply::getApproveTime, new Date())
               .set(HzCheckoutApply::getApproveOpinion, approveOpinion);
        return this.update(wrapper) ? 1 : 0;
    }

    @Override
    public int cancelCheckoutApply(Long applyId) {
        LambdaUpdateWrapper<HzCheckoutApply> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(HzCheckoutApply::getApplyId, applyId)
               .set(HzCheckoutApply::getApplyStatus, "3"); // 3=已取消
        return this.update(wrapper) ? 1 : 0;
    }

    @Override
    public int saveCheckoutCalculate(HzCheckoutApply hzCheckoutApply) {
        // 获取当前登录用户作为审批人
        String approveBy = hzCheckoutApply.getApproveBy();
        if (StringUtils.isEmpty(approveBy)) {
            approveBy = SecurityUtils.getUsername();
        }

        // 保存管理员计算的费用信息和审批信息
        LambdaUpdateWrapper<HzCheckoutApply> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(HzCheckoutApply::getApplyId, hzCheckoutApply.getApplyId())
               .set(HzCheckoutApply::getWaterFee, hzCheckoutApply.getWaterFee())
               .set(HzCheckoutApply::getElectricFee, hzCheckoutApply.getElectricFee())
               .set(HzCheckoutApply::getGasFee, hzCheckoutApply.getGasFee())
               .set(HzCheckoutApply::getHeatingFee, hzCheckoutApply.getHeatingFee())
               .set(HzCheckoutApply::getPropertyFee, hzCheckoutApply.getPropertyFee())
               .set(HzCheckoutApply::getDamageDeduction, hzCheckoutApply.getDamageDeduction())
               .set(HzCheckoutApply::getDamageDescription, hzCheckoutApply.getDamageDescription())
               .set(HzCheckoutApply::getMeterReadingWater, hzCheckoutApply.getMeterReadingWater())
               .set(HzCheckoutApply::getMeterReadingElectric, hzCheckoutApply.getMeterReadingElectric())
               .set(HzCheckoutApply::getMeterReadingGas, hzCheckoutApply.getMeterReadingGas())
               .set(HzCheckoutApply::getKeyReturned, hzCheckoutApply.getKeyReturned())
               .set(HzCheckoutApply::getRefundAmount, hzCheckoutApply.getRefundAmount())
               .set(HzCheckoutApply::getApproveOpinion, hzCheckoutApply.getApproveOpinion())
               .set(HzCheckoutApply::getApproveBy, approveBy)
               .set(HzCheckoutApply::getApproveTime, new java.util.Date())
               .set(HzCheckoutApply::getApplyStatus, "4"); // 4=待确认
        return this.update(wrapper) ? 1 : 0;
    }

    @Override
    public HzCheckoutRecord selectCheckoutRecordByRecordId(Long recordId) {
        return checkoutRecordMapper.selectById(recordId);
    }

    @Override
    public List<HzCheckoutRecord> selectCheckoutRecordList(HzCheckoutRecord hzCheckoutRecord) {
        LambdaQueryWrapper<HzCheckoutRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HzCheckoutRecord::getDelFlag, "0")
               .orderByDesc(HzCheckoutRecord::getCreateTime);
        return checkoutRecordMapper.selectList(wrapper);
    }

    @Override
    public HzCheckoutRecord selectCheckoutRecordByApplyId(Long applyId) {
        return checkoutRecordMapper.selectByApplyId(applyId);
    }

    @Override
    public int insertCheckoutRecord(HzCheckoutRecord hzCheckoutRecord) {
        hzCheckoutRecord.setDelFlag("0");
        if (hzCheckoutRecord.getCheckoutTime() == null) {
            hzCheckoutRecord.setCheckoutTime(new Date());
        }
        return checkoutRecordMapper.insert(hzCheckoutRecord);
    }

    @Override
    @Transactional
    public int submitCheckoutConfirm(Long applyId, String tenantSignature) {
        // 1. 获取退租申请信息
        HzCheckoutApply apply = checkoutApplyMapper.selectById(applyId);
        if (apply == null) {
            throw new RuntimeException("退租申请不存在");
        }

        // 2. 创建退租记录
        HzCheckoutRecord record = new HzCheckoutRecord();
        record.setApplyId(applyId);
        record.setContractId(apply.getContractId());
        record.setTenantId(apply.getTenantId());
        record.setHouseId(apply.getHouseId());
        record.setCheckoutDate(new Date());
        record.setCheckoutTime(new Date());

        // 费用信息
        record.setMeterReadingElectric(apply.getMeterReadingElectric());
        record.setMeterReadingWater(apply.getMeterReadingWater());
        record.setMeterReadingGas(apply.getMeterReadingGas());
        record.setKeyReturned(apply.getKeyReturned());

        // 计算总费用
        BigDecimal utilityBill = BigDecimal.ZERO;
        if (apply.getWaterFee() != null) utilityBill = utilityBill.add(apply.getWaterFee());
        if (apply.getElectricFee() != null) utilityBill = utilityBill.add(apply.getElectricFee());
        if (apply.getGasFee() != null) utilityBill = utilityBill.add(apply.getGasFee());
        if (apply.getHeatingFee() != null) utilityBill = utilityBill.add(apply.getHeatingFee());
        if (apply.getPropertyFee() != null) utilityBill = utilityBill.add(apply.getPropertyFee());
        record.setUtilityBill(utilityBill);

        record.setUnpaidRent(apply.getUnpaidBills());
        record.setPenaltyAmount(apply.getPenaltyAmount());
        record.setDamageDeduction(apply.getDamageDeduction());
        record.setDamageDescription(apply.getDamageDescription());

        // 计算应退押金
        BigDecimal depositRefund = apply.getDepositRefund() != null ? apply.getDepositRefund() : BigDecimal.ZERO;
        depositRefund = depositRefund.subtract(utilityBill)
                                      .subtract(apply.getUnpaidBills() != null ? apply.getUnpaidBills() : BigDecimal.ZERO)
                                      .subtract(apply.getPenaltyAmount() != null ? apply.getPenaltyAmount() : BigDecimal.ZERO)
                                      .subtract(apply.getDamageDeduction() != null ? apply.getDamageDeduction() : BigDecimal.ZERO);
        record.setDepositRefund(depositRefund);

        record.setTenantSignature(tenantSignature);
        record.setRefundStatus("0"); // 0=待退还

        int result = checkoutRecordMapper.insert(record);

        // 3. 创建退款申请（供管理员在退款管理页面审核）
        if (result > 0) {
            try {
                // 查询租户姓名
                HzUser user = userMapper.selectById(apply.getTenantId());
                String tenantName = "未知用户";
                if (user != null) {
                    tenantName = StringUtils.isNotEmpty(user.getRealName()) ? user.getRealName() : user.getNickname();
                }

                // 生成退款编号
                String refundNo = generateRefundNo();

                // 创建退款申请
                HzRefundApply refundApply = new HzRefundApply();
                refundApply.setApplyNo(refundNo);
                refundApply.setPaymentId(applyId); // 存储退租申请ID，用于关联查询
                refundApply.setTenantId(apply.getTenantId());
                refundApply.setTenantName(tenantName);
                refundApply.setRefundAmount(apply.getRefundAmount()); // 使用退租申请中的退款金额
                refundApply.setRefundReason("退租退款");
                refundApply.setApplyTime(new Date());
                refundApply.setApproveStatus("0"); // 0=待审核
                refundApply.setCreateBy("用户端自动创建");
                refundApply.setDelFlag("0");

                refundApplyMapper.insert(refundApply);
            } catch (Exception e) {
                // 创建退款申请失败不影响退租确认流程
                logger.error("创建退款申请失败:", e);
            }
        }

        // 4. 更新退租申请状态为已完成
        LambdaUpdateWrapper<HzCheckoutApply> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(HzCheckoutApply::getApplyId, applyId)
               .set(HzCheckoutApply::getApplyStatus, "5"); // 5=已完成
        this.update(wrapper);

        // 5. 更新合同状态为已解约
        LambdaUpdateWrapper<HzContract> contractWrapper = new LambdaUpdateWrapper<>();
        contractWrapper.eq(HzContract::getContractId, apply.getContractId())
                       .set(HzContract::getContractStatus, "5"); // 5=已解约
        contractMapper.update(null, contractWrapper);

        return result;
    }

    /**
     * 生成退款编号
     * 格式：RF + yyyyMMdd + 3位序号（如：RF20250118001）
     */
    private String generateRefundNo() {
        // 查询今天已有的退款申请数量
        String today = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String prefix = "RF" + today;

        // 查询今天已创建的退款申请数量
        Long count = refundApplyMapper.countByApplyNo(prefix);

        // 生成序号：001, 002, 003...
        String sequence = String.format("%03d", count + 1);
        return prefix + sequence;
    }

    @Override
    public HzCheckoutApply selectCancelledApply(Long contractId, Long tenantId) {
        // 查询已取消的退租申请（相同合同和租户）
        LambdaQueryWrapper<HzCheckoutApply> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HzCheckoutApply::getContractId, contractId)
               .eq(HzCheckoutApply::getTenantId, tenantId)
               .eq(HzCheckoutApply::getApplyStatus, "3")  // 3=已取消
               .eq(HzCheckoutApply::getDelFlag, "0")
               .orderByDesc(HzCheckoutApply::getApplyTime)
               .last("LIMIT 1");

        return checkoutApplyMapper.selectOne(wrapper);
    }

    @Override
    public Map<String, Object> getCheckoutConfirmInfo(Long applyId) {
        Map<String, Object> result = new HashMap<>();

        // 1. 获取退租申请信息
        HzCheckoutApply apply = checkoutApplyMapper.selectById(applyId);
        if (apply == null) {
            throw new RuntimeException("退租申请不存在");
        }

        // 2. 获取房源信息（配套设备、小区、房间）
        HzHouse house = houseMapper.selectById(apply.getHouseId());
        String communityName = "";  // 小区名称
        String roomAddress = "";     // 房间地址

        if (house != null) {
            // 获取项目名称作为小区名称
            if (house.getProjectId() != null) {
                HzProject project = projectMapper.selectById(house.getProjectId());
                if (project != null) {
                    communityName = project.getProjectName();
                }
            }

            // 组装房间地址
            roomAddress = house.getHouseCode() + "栋" + house.getHouseNo() + "室";
        }

        // 3. 获取合同信息（押金、租金、合同期限）
        HzContract contract = contractMapper.selectById(apply.getContractId());
        BigDecimal deposit = contract != null ? contract.getDeposit() : BigDecimal.ZERO;
        BigDecimal rentPrice = contract != null ? contract.getRentPrice() : BigDecimal.ZERO;
        String startDate = contract != null ? contract.getStartDate() : "";
        String endDate = contract != null ? contract.getEndDate() : "";

        // 4. 获取申请人信息（昵称）
        String applicantName = "申请人";
        if (apply.getTenantId() != null) {
            HzUser user = userMapper.selectById(apply.getTenantId());
            if (user != null) {
                // 优先使用真实姓名，如果没有则使用昵称
                if (StringUtils.isNotEmpty(user.getRealName())) {
                    applicantName = user.getRealName();
                } else if (StringUtils.isNotEmpty(user.getNickname())) {
                    applicantName = user.getNickname();
                }
            }
        }

        // 5. 组装返回结果（平铺结构，方便前端使用）
        // 退租基本信息
        result.put("applicant", applicantName);
        result.put("planCheckoutDate", apply.getPlanCheckoutDate());
        result.put("checkoutReason", apply.getCheckoutReason());
        result.put("community", communityName);
        result.put("room", roomAddress);

        // 合同信息
        result.put("deposit", deposit);
        result.put("rentPrice", rentPrice);
        result.put("startDate", startDate);
        result.put("endDate", endDate);

        // 费用信息
        result.put("depositRefund", apply.getRefundAmount() != null ? apply.getRefundAmount() : BigDecimal.ZERO);
        result.put("waterFee", apply.getWaterFee() != null ? apply.getWaterFee() : BigDecimal.ZERO);
        result.put("electricFee", apply.getElectricFee() != null ? apply.getElectricFee() : BigDecimal.ZERO);
        result.put("gasFee", apply.getGasFee() != null ? apply.getGasFee() : BigDecimal.ZERO);
        result.put("heatingFee", apply.getHeatingFee() != null ? apply.getHeatingFee() : BigDecimal.ZERO);
        result.put("propertyFee", apply.getPropertyFee() != null ? apply.getPropertyFee() : BigDecimal.ZERO);
        result.put("damageDeduction", apply.getDamageDeduction() != null ? apply.getDamageDeduction() : BigDecimal.ZERO);

        // 从 damageDescription 中提取损坏说明（排除设施状态的JSON部分）
        String damageDescription = "";
        if (StringUtils.isNotEmpty(apply.getDamageDescription())) {
            try {
                // 尝试解析JSON格式
                com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                Map<String, Object> damageJson = mapper.readValue(apply.getDamageDescription(), Map.class);
                if (damageJson.containsKey("description")) {
                    damageDescription = (String) damageJson.get("description");
                }
            } catch (Exception e) {
                // 如果不是JSON格式，直接使用原值
                damageDescription = apply.getDamageDescription();
            }
        }
        result.put("damageDescription", damageDescription);

        // 房源ID（供前端调用设施API）
        result.put("houseId", apply.getHouseId());

        // 表读数
        result.put("meterReadingWater", apply.getMeterReadingWater());
        result.put("meterReadingElectric", apply.getMeterReadingElectric());
        result.put("meterReadingGas", apply.getMeterReadingGas());
        result.put("keyReturned", apply.getKeyReturned() != null ? apply.getKeyReturned() : 0);

        // 配套设备列表（从 damageDescription 的 facilities 字段解析）
        List<Map<String, String>> facilitiesStatusList = new ArrayList<>();
        if (StringUtils.isNotEmpty(apply.getDamageDescription())) {
            try {
                com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                Map<String, Object> damageJson = mapper.readValue(apply.getDamageDescription(), Map.class);
                if (damageJson.containsKey("facilities")) {
                    List<Map<String, String>> facilities = (List<Map<String, String>>) damageJson.get("facilities");
                    if (facilities != null) {
                        facilitiesStatusList = facilities;
                    }
                }
            } catch (Exception e) {
                // 解析失败，忽略
            }
        }
        result.put("equipmentList", facilitiesStatusList);

        return result;
    }

    @Override
    public List<Map<String, Object>> selectContractBillList(Long contractId) {
        // 查询该合同的所有账单
        LambdaQueryWrapper<com.ruoyi.system.domain.HzBill> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(com.ruoyi.system.domain.HzBill::getContractId, contractId)
               .eq(com.ruoyi.system.domain.HzBill::getDelFlag, "0")
               .orderByAsc(com.ruoyi.system.domain.HzBill::getBillPeriod);

        List<com.ruoyi.system.domain.HzBill> bills = billMapper.selectList(wrapper);
        List<Map<String, Object>> resultList = new ArrayList<>();

        for (com.ruoyi.system.domain.HzBill bill : bills) {
            Map<String, Object> item = new HashMap<>();
            item.put("billId", bill.getBillId());
            item.put("billNo", bill.getBillNo());
            item.put("billType", bill.getBillType());
            item.put("billTypeText", getBillTypeText(bill.getBillType()));
            item.put("billPeriod", bill.getBillPeriod());
            item.put("billAmount", bill.getBillAmount());
            item.put("paidAmount", bill.getPaidAmount());
            item.put("unpaidAmount", bill.getUnpaidAmount());
            item.put("billStatus", bill.getBillStatus());
            item.put("billStatusText", getBillStatusText(bill.getBillStatus()));
            item.put("payTime", bill.getPayTime());
            resultList.add(item);
        }

        return resultList;
    }

    /**
     * 获取账单类型文本
     */
    private String getBillTypeText(String billType) {
        if (billType == null) {
            return "未知";
        }
        switch (billType) {
            case "1": return "押金";
            case "2": return "租金";
            case "3": return "物业费";
            case "4": return "水电费";
            default: return "其他";
        }
    }

    /**
     * 获取账单状态文本
     */
    private String getBillStatusText(String billStatus) {
        if (billStatus == null) {
            return "未知";
        }
        switch (billStatus) {
            case "0": return "未支付";
            case "1": return "已支付";
            case "2": return "已取消";
            default: return "未知";
        }
    }
}
