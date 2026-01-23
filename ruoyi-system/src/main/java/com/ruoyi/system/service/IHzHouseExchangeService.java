package com.ruoyi.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ruoyi.system.domain.HzHouseExchange;

import java.util.List;
import java.util.Map;

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

    // ==================== 新增换房处理相关方法 ====================

    /**
     * 获取项目下的楼栋列表
     *
     * @param projectId 项目ID
     * @return 楼栋列表
     */
    public List<Map<String, Object>> getBuildingsByProjectId(Long projectId);

    /**
     * 获取楼栋下的单元列表
     *
     * @param buildingId 楼栋ID
     * @return 单元列表
     */
    public List<Map<String, Object>> getUnitsByBuildingId(Long buildingId);

    /**
     * 获取单元下的可用房间列表（空置状态）
     *
     * @param projectId 项目ID
     * @param buildingId 楼栋ID
     * @param unitId 单元ID
     * @return 可用房间列表
     */
    public List<Map<String, Object>> getAvailableRooms(Long projectId, Long buildingId, Long unitId);

    /**
     * 分配目标房源（保存到exchange记录）
     *
     * @param exchangeId 调换房ID
     * @param newHouseId 目标房源ID
     * @param exchangeTime 换房时间
     * @return 结果
     */
    public int assignTargetHouse(Long exchangeId, Long newHouseId, String exchangeTime);

    /**
     * 确认换房（审核通过时执行完整换房逻辑）
     *
     * @param exchangeId 调换房ID
     * @param approveResult 审核结果(1=通过,2=拒绝)
     * @param approveOpinion 审核意见
     * @param exchangeTime 换房时间
     * @return 结果
     */
    public int confirmExchange(Long exchangeId, String approveResult, String approveOpinion, String exchangeTime);
}
