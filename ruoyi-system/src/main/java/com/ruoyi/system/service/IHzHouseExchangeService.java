package com.ruoyi.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ruoyi.system.domain.HzHouseExchange;

import java.util.List;

/**
 * 调换房申请Service接口
 *
 * @author ruoyi
 */
public interface IHzHouseExchangeService {

    /**
     * 查询调换房申请
     *
     * @param exchangeId 调换房主键
     * @return 调换房申请
     */
    public HzHouseExchange selectExchangeById(Long exchangeId);

    /**
     * 根据租户ID查询调换房申请列表
     *
     * @param tenantId 租户ID
     * @return 调换房申请列表
     */
    public List<HzHouseExchange> selectExchangeListByTenantId(Long tenantId);

    /**
     * 查询调换房申请列表
     *
     * @param exchange 调换房申请
     * @return 调换房申请列表
     */
    public List<HzHouseExchange> selectExchangeList(HzHouseExchange exchange);

    /**
     * 分页查询调换房申请列表
     *
     * @param exchange 调换房申请
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @return 调换房申请分���数据
     */
    public IPage<HzHouseExchange> selectExchangePage(HzHouseExchange exchange, int pageNum, int pageSize);

    /**
     * 新增调换房申请
     *
     * @param exchange 调换房申请
     * @return 结果
     */
    public int insertExchange(HzHouseExchange exchange);

    /**
     * 修改调换房申请
     *
     * @param exchange 调换房申请
     * @return 结果
     */
    public int updateExchange(HzHouseExchange exchange);

    /**
     * 删除调换房申请
     *
     * @param exchangeId 调换房主键
     * @return 结果
     */
    public int deleteExchangeById(Long exchangeId);

    /**
     * 审核调换房申请
     *
     * @param exchangeId 调换房ID
     * @param approveResult 审核结果(1=通过,2=拒绝)
     * @param approveOpinion 审核意见
     * @return 结果
     */
    public int auditExchange(Long exchangeId, String approveResult, String approveOpinion);
}
