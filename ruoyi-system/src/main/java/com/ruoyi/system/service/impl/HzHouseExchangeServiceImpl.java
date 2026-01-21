package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.HzHouseExchange;
import com.ruoyi.system.mapper.HzHouseExchangeMapper;
import com.ruoyi.system.service.IHzHouseExchangeService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 调换房申请Service业务层处理
 *
 * @author ruoyi
 */
@Service
public class HzHouseExchangeServiceImpl extends ServiceImpl<HzHouseExchangeMapper, HzHouseExchange> implements IHzHouseExchangeService {

    @Override
    public HzHouseExchange selectExchangeById(Long exchangeId) {
        return baseMapper.selectExchangeByIdWithRelations(exchangeId);
    }

    @Override
    public List<HzHouseExchange> selectExchangeListByTenantId(Long tenantId) {
        HzHouseExchange exchange = new HzHouseExchange();
        exchange.setTenantId(tenantId);
        return baseMapper.selectExchangeListWithRelations(exchange);
    }

    @Override
    public List<HzHouseExchange> selectExchangeList(HzHouseExchange exchange) {
        return baseMapper.selectExchangeListWithRelations(exchange);
    }

    @Override
    public IPage<HzHouseExchange> selectExchangePage(HzHouseExchange exchange, int pageNum, int pageSize) {
        Page<HzHouseExchange> page = new Page<>(pageNum, pageSize);
        return baseMapper.selectExchangePageWithRelations(page, exchange);
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
}
