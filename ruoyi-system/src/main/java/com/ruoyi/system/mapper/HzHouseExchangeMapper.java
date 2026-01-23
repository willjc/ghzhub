package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.domain.HzHouseExchange;

import java.util.List;
import java.util.Map;

/**
 * 调换房申请Mapper接口
 *
 * @author ruoyi
 */
@Mapper
public interface HzHouseExchangeMapper extends BaseMapper<HzHouseExchange> {

    /**
     * 分页查询调换房申请（带关联信息）
     *
     * @param page 分页对象
     * @param exchange 查询条件
     * @return 分页结果
     */
    IPage<HzHouseExchange> selectExchangePageWithRelations(Page<HzHouseExchange> page, @Param("exchange") HzHouseExchange exchange);

    /**
     * 根据ID查询调换房申请（带关联信息）
     *
     * @param exchangeId 调换房ID
     * @return 调换房申请
     */
    HzHouseExchange selectExchangeByIdWithRelations(@Param("exchangeId") Long exchangeId);

    /**
     * 查询调换房申请列表（带关联信息）
     *
     * @param exchange 查询条件
     * @return 调换房申请列表
     */
    List<HzHouseExchange> selectExchangeListWithRelations(HzHouseExchange exchange);

    // ==================== 换房处理相关查询方法 ====================

    /**
     * 根据ID查询调换房申请（带完整原房源和目标房源信息）
     *
     * @param exchangeId 调换房ID
     * @return 调换房申请（含原房源和目标房源详细信息）
     */
    HzHouseExchange selectExchangeDetailById(@Param("exchangeId") Long exchangeId);

    /**
     * 获取项目下的楼栋列表
     *
     * @param projectId 项目ID
     * @return 楼栋列表
     */
    List<Map<String, Object>> selectBuildingsByProjectId(@Param("projectId") Long projectId);

    /**
     * 获取楼栋下的单元列表
     *
     * @param buildingId 楼栋ID
     * @return 单元列表
     */
    List<Map<String, Object>> selectUnitsByBuildingId(@Param("buildingId") Long buildingId);

    /**
     * 获取单元下的可用房间列表（空置状态）
     *
     * @param projectId 项目ID
     * @param buildingId 楼栋ID
     * @param unitId 单元ID
     * @return 可用房间列表
     */
    List<Map<String, Object>> selectAvailableRooms(@Param("projectId") Long projectId,
                                                   @Param("buildingId") Long buildingId,
                                                   @Param("unitId") Long unitId);
}
