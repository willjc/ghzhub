package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.*;
import com.ruoyi.system.mapper.HzHouseExchangeMapper;
import com.ruoyi.system.service.*;
import com.ruoyi.system.service.IHzRoleProjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

/**
 * 调换房申请Service业务层处理
 *
 * @author ruoyi
 */
@Service
public class HzHouseExchangeServiceImpl extends ServiceImpl<HzHouseExchangeMapper, HzHouseExchange> implements IHzHouseExchangeService {

    private static final Logger logger = LoggerFactory.getLogger(HzHouseExchangeServiceImpl.class);

    @Autowired
    private IHzContractService contractService;

    @Autowired
    private IHzHouseService houseService;

    @Autowired
    private IHzProjectService projectService;

    @Autowired
    private IHzBuildingService buildingService;

    @Autowired
    private IHzUnitService unitService;

    @Autowired
    private IHzUserMessageService userMessageService;

    @Autowired
    private IHzRoleProjectService roleProjectService;

    @Override
    public HzHouseExchange selectExchangeById(Long exchangeId) {
        // 使用详情查询方法获取完整信息（含原房源和目标房源）
        HzHouseExchange exchange = baseMapper.selectExchangeDetailById(exchangeId);
        if (exchange == null) {
            // 如果详情查询查不到（如目标房源为空时），回退到简单查询
            exchange = baseMapper.selectExchangeByIdWithRelations(exchangeId);
        }
        return exchange;
    }

    @Override
    public List<HzHouseExchange> selectExchangeListByTenantId(Long tenantId) {
        HzHouseExchange exchange = new HzHouseExchange();
        exchange.setTenantId(tenantId);
        return baseMapper.selectExchangeListWithRelations(exchange);
    }

    @Override
    public List<HzHouseExchange> selectExchangeList(HzHouseExchange exchange) {
        injectProjectFilter(exchange);
        return baseMapper.selectExchangeListWithRelations(exchange);
    }

    @Override
    public IPage<HzHouseExchange> selectExchangePage(HzHouseExchange exchange, int pageNum, int pageSize) {
        injectProjectFilter(exchange);
        Page<HzHouseExchange> page = new Page<>(pageNum, pageSize);
        return baseMapper.selectExchangePageWithRelations(page, exchange);
    }

    /** 注入项目权限过滤到params */
    private void injectProjectFilter(HzHouseExchange exchange) {
        List<Long> projectIds = roleProjectService.getCurrentUserProjectIds();
        if (projectIds != null) {
            if (exchange.getParams() == null) {
                exchange.setParams(new java.util.HashMap<>());
            }
            exchange.getParams().put("projectIds", projectIds);
        }
    }

    @Override
    public int insertExchange(HzHouseExchange exchange) {
        exchange.setDelFlag("0");
        if (StringUtils.isEmpty(exchange.getStatus())) {
            exchange.setStatus("0"); // 默认待审核状态
        }
        if (StringUtils.isEmpty(exchange.getApproveResult())) {
            exchange.setApproveResult("0"); // 默认待审核
        }
        return this.save(exchange) ? 1 : 0;
    }

    @Override
    public int updateExchange(HzHouseExchange exchange) {
        return this.updateById(exchange) ? 1 : 0;
    }

    @Override
    public int deleteExchangeById(Long exchangeId) {
        LambdaUpdateWrapper<HzHouseExchange> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(HzHouseExchange::getExchangeId, exchangeId)
               .set(HzHouseExchange::getDelFlag, "2");
        return this.update(wrapper) ? 1 : 0;
    }

    @Override
    public int auditExchange(Long exchangeId, String approveResult, String approveOpinion) {
        HzHouseExchange exchange = new HzHouseExchange();
        exchange.setExchangeId(exchangeId);
        exchange.setApproveResult(approveResult);
        exchange.setApproveOpinion(approveOpinion);
        exchange.setApproveBy(SecurityUtils.getUsername());
        exchange.setApproveTime(DateUtils.getTime());

        // 同步更新status
        if ("1".equals(approveResult)) {
            exchange.setStatus("1"); // 已完成
        } else if ("2".equals(approveResult)) {
            exchange.setStatus("2"); // 已拒绝
        }

        return this.updateById(exchange) ? 1 : 0;
    }

    // ==================== 新增换房处理相关方法实现 ====================

    @Override
    public List<Map<String, Object>> getBuildingsByProjectId(Long projectId) {
        return baseMapper.selectBuildingsByProjectId(projectId);
    }

    @Override
    public List<Map<String, Object>> getUnitsByBuildingId(Long buildingId) {
        return baseMapper.selectUnitsByBuildingId(buildingId);
    }

    @Override
    public List<Map<String, Object>> getAvailableRooms(Long projectId, Long buildingId, Long unitId) {
        return baseMapper.selectAvailableRooms(projectId, buildingId, unitId);
    }

    @Override
    public int assignTargetHouse(Long exchangeId, Long newHouseId, String exchangeTime) {
        HzHouseExchange exchange = new HzHouseExchange();
        exchange.setExchangeId(exchangeId);
        exchange.setNewHouseId(newHouseId);

        // 查询目标房源信息，设置房源编号
        HzHouse newHouse = houseService.selectHouseById(newHouseId);
        if (newHouse != null) {
            exchange.setNewHouseCode(newHouse.getHouseCode());
        }

        // 如果提供了换房时间，也保存
        if (StringUtils.isNotEmpty(exchangeTime)) {
            exchange.setExchangeTime(exchangeTime);
        }

        return this.updateById(exchange) ? 1 : 0;
    }

    @Override
    @Transactional
    public int confirmExchange(Long exchangeId, String approveResult, String approveOpinion, String exchangeTime) {
        // 1. 查询换房申请记录（含完整关联信息）
        HzHouseExchange exchange = baseMapper.selectExchangeDetailById(exchangeId);
        if (exchange == null) {
            logger.error("换房申请不存在: exchangeId={}", exchangeId);
            return 0;
        }

        if ("1".equals(approveResult)) { // 审核通过 - 创建合同草稿，等待用户签署
            // 2. 检查目标房源是否已分配
            if (exchange.getNewHouseId() == null) {
                logger.error("审核通过但未分配目标房源: exchangeId={}", exchangeId);
                throw new RuntimeException("请先选择目标房源后再审核通过");
            }

            // 3. 检查换房时间是否已设置
            if (StringUtils.isEmpty(exchangeTime)) {
                logger.error("审核通过但未设置换房时间: exchangeId={}", exchangeId);
                throw new RuntimeException("请选择换房时间后再审核通过");
            }

            // 4. 查询原合同
            HzContract oldContract = contractService.selectContractById(exchange.getOldContractId());
            if (oldContract == null) {
                logger.error("原合同不存在: contractId={}", exchange.getOldContractId());
                throw new RuntimeException("原合同不存在");
            }

            // 5. 查询目标房源信息
            HzHouse newHouse = houseService.selectHouseById(exchange.getNewHouseId());
            if (newHouse == null) {
                logger.error("目标房源不存在: houseId={}", exchange.getNewHouseId());
                throw new RuntimeException("目标房源不存在");
            }

            // 6. 创建新合同草稿（status=0，等待用户签署后生效）
            HzContract newContract = new HzContract();
            newContract.setContractNo(contractService.generateContractNo());
            newContract.setContractType("3"); // 3=换房合同
            newContract.setTemplateId(oldContract.getTemplateId());
            newContract.setTenantId(oldContract.getTenantId());
            newContract.setTenantName(oldContract.getTenantName());
            newContract.setTenantIdCard(oldContract.getTenantIdCard());
            newContract.setTenantPhone(oldContract.getTenantPhone());
            newContract.setProjectId(newHouse.getProjectId());
            newContract.setHouseId(newHouse.getHouseId());
            newContract.setHouseCode(newHouse.getHouseCode());
            newContract.setHouseAddress(buildFullAddress(newHouse));
            newContract.setRentPrice(newHouse.getRentPrice() != null ? newHouse.getRentPrice() : BigDecimal.ZERO);
            newContract.setDeposit(newHouse.getDeposit() != null ? newHouse.getDeposit() : BigDecimal.ZERO);

            // 计算新合同的起止日期（签署回调时会更新startDate为实际签署日）
            LocalDate startDate = LocalDate.parse(exchangeTime);
            newContract.setStartDate(exchangeTime);
            newContract.setEndDate(oldContract.getEndDate());

            // 计算租期月数
            long months = ChronoUnit.MONTHS.between(startDate, LocalDate.parse(oldContract.getEndDate()));
            newContract.setRentMonths((int) months);

            newContract.setPaymentCycle(oldContract.getPaymentCycle());
            newContract.setPaymentDay(oldContract.getPaymentDay());
            newContract.setContractStatus("0"); // 草稿（等待用户e签宝签署）
            newContract.setIsRenewed("0");
            contractService.insertContract(newContract);

            // 7. 更新换房申请记录：状态改为“待签署”
            HzHouseExchange updateExchange = new HzHouseExchange();
            updateExchange.setExchangeId(exchangeId);
            updateExchange.setApproveResult(approveResult);
            updateExchange.setApproveOpinion(approveOpinion);
            updateExchange.setApproveBy(SecurityUtils.getUsername());
            updateExchange.setApproveTime(DateUtils.getTime());
            updateExchange.setExchangeTime(exchangeTime);
            updateExchange.setNewContractId(newContract.getContractId());
            updateExchange.setStatus("3"); // 3=待签署（等待用户签署新合同）
            this.updateById(updateExchange);

            // 8. 发送消息提示用户签署新合同
            sendExchangeSignMessage(exchange, newHouse);

            return 1;

        } else { // 审核拒绝
            HzHouseExchange updateExchange = new HzHouseExchange();
            updateExchange.setExchangeId(exchangeId);
            updateExchange.setApproveResult(approveResult);
            updateExchange.setApproveOpinion(approveOpinion);
            updateExchange.setApproveBy(SecurityUtils.getUsername());
            updateExchange.setApproveTime(DateUtils.getTime());
            updateExchange.setStatus("2"); // 已拒绝
            return this.updateById(updateExchange) ? 1 : 0;
        }
    }

    /**
     * 构建房源完整地址
     */
    private String buildFullAddress(HzHouse house) {
        StringBuilder address = new StringBuilder();

        if (house.getProjectId() != null) {
            HzProject project = projectService.selectProjectById(house.getProjectId());
            if (project != null) {
                address.append(project.getProjectName());
            }
        }

        if (house.getBuildingId() != null) {
            HzBuilding building = buildingService.selectBuildingById(house.getBuildingId());
            if (building != null) {
                address.append(building.getBuildingName());
            }
        }

        if (house.getUnitId() != null) {
            HzUnit unit = unitService.selectUnitById(house.getUnitId());
            if (unit != null) {
                address.append(unit.getUnitName());
            }
        }

        address.append(house.getHouseNo()).append("室");

        return address.toString();
    }

    /**
     * 发送换房签署提醒消息
     */
    private void sendExchangeSignMessage(HzHouseExchange exchange, HzHouse newHouse) {
        try {
            String newAddress = buildFullAddress(newHouse);

            HzUserMessage message = new HzUserMessage();
            message.setUserId(exchange.getTenantId());
            message.setMessageType("exchange");
            message.setMessageTitle("换房已批准");
            message.setMessageContent("您的换房申请已批准，目标房源为" + newAddress + "，请尽快签署新合同。");
            message.setIsRead("0");
            message.setDelFlag("0");
            userMessageService.insertMessage(message);

            logger.info("换房签署消息发送成功: userId={}", exchange.getTenantId());
        } catch (Exception e) {
            logger.error("发送换房签署消息失败: exchangeId={}", exchange.getExchangeId(), e);
        }
    }

    /**
     * 发送换房完成消息
     */
    private void sendExchangeCompleteMessage(HzHouseExchange exchange, HzHouse oldHouse, HzHouse newHouse) {
        try {
            // 构建原房源和目标房源的完整地址
            String oldAddress = buildFullAddress(oldHouse);
            String newAddress = buildFullAddress(newHouse);

            HzUserMessage message = new HzUserMessage();
            message.setUserId(exchange.getTenantId());
            message.setMessageType("exchange"); // 换房类型
            message.setMessageTitle("换房提醒");
            message.setMessageContent("您的房源已从" + oldAddress + "换到" + newAddress);
            message.setIsRead("0");
            message.setDelFlag("0");
            userMessageService.insertMessage(message);

            logger.info("换房消息发送成功: userId={}, content={}", exchange.getTenantId(), message.getMessageContent());
        } catch (Exception e) {
            logger.error("发送换房消息失败: exchangeId={}", exchange.getExchangeId(), e);
            // 消息发送失败不影响换房流程
        }
    }
}
