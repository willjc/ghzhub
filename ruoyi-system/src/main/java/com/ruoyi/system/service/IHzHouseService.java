package com.ruoyi.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.system.domain.HzHouse;
import com.ruoyi.system.domain.HzHouseImage;
import com.ruoyi.system.domain.HzHouseVr;

import java.util.List;
import java.util.Map;

/**
 * 房源Service接口
 *
 * @author ruoyi
 * @date 2025-11-17
 */
public interface IHzHouseService extends IService<HzHouse>
{
    /**
     * 查询房源列表（支持分页）
     *
     * @param house 房源查询条件
     * @return 房源分页列表
     */
    IPage<HzHouse> selectHouseList(HzHouse house);

    /**
     * 分页查询房源列表
     *
     * @param house 房源查询条件
     * @param pageNum 当前页码
     * @param pageSize 每页数量
     * @return 房源分页列表
     */
    IPage<HzHouse> selectHousePage(HzHouse house, int pageNum, int pageSize);

    /**
     * 查询房源详情
     *
     * @param houseId 房源ID
     * @return 房源
     */
    HzHouse selectHouseById(Long houseId);

    /**
     * 查询房源详细信息（包含关联数据）
     *
     * @param houseId 房源ID
     * @return 房源详细信息
     */
    java.util.Map<String, Object> selectHouseDetailById(Long houseId);

    /**
     * 新增房源
     *
     * @param house 房源
     * @return 结果
     */
    int insertHouse(HzHouse house);

    /**
     * 修改房源
     *
     * @param house 房源
     * @return 结果
     */
    int updateHouse(HzHouse house);

    /**
     * 删除房源
     *
     * @param houseId 房源ID
     * @return 结果
     */
    int deleteHouseById(Long houseId);

    /**
     * 增加浏览次数
     *
     * @param houseId 房源ID
     * @return 结果
     */
    int increaseViewCount(Long houseId);

    /**
     * 导入房源数据
     *
     * @param houseList 房源列表
     * @param updateSupport 是否更新已存在数据
     * @return 结果消息
     */
    String importHouse(List<HzHouse> houseList, boolean updateSupport);

    /**
     * 获取房源图片列表
     *
     * @param houseId 房源ID
     * @return 图片列表
     */
    List<HzHouseImage> getHouseImages(Long houseId);

    /**
     * 保存房源图片
     *
     * @param houseId 房源ID
     * @param images 图片列表
     */
    void saveHouseImages(Long houseId, List<Map<String, Object>> images);

    /**
     * 获取房源VR列表
     *
     * @param houseId 房源ID
     * @return VR列表
     */
    List<HzHouseVr> getHouseVrs(Long houseId);

    /**
     * 保存房源VR
     *
     * @param houseId 房源ID
     * @param vrs VR列表
     */
    void saveHouseVrs(Long houseId, List<Map<String, Object>> vrs);

    /**
     * 按项目批量修改房源状态（受控过渡白名单）。
     * 仅对源状态为 0(空置) / 3(维修中) / 4(下架) 的房源进行更新，
     * 跳过 1(已预订) / 2(已出租)，避免与订单/合同冲突。
     * 目标状态同样限定在 0 / 3 / 4 范围。
     *
     * @param projectId    项目ID
     * @param targetStatus 目标状态（0/3/4）
     * @return 汇总：total / affected / skippedBooked(1) / skippedRented(2)
     */
    Map<String, Integer> batchUpdateHouseStatusByProject(Long projectId, String targetStatus);

    /**
     * 按房源ID列表批量修改房源状态（受控过渡白名单）。
     * 仅对源状态为 0(空置) / 3(维修中) / 4(下架) 的房源进行更新，
     * 跳过 1(已预订) / 2(已出租)，避免与订单/合同冲突。
     * 目标状态同样限定在 0 / 3 / 4 范围。
     *
     * @param houseIds     房源ID列表
     * @param targetStatus 目标状态（0/3/4）
     * @return 汇总：total / affected / skippedBooked(1) / skippedRented(2)
     */
    Map<String, Integer> batchUpdateHouseStatusByIds(List<Long> houseIds, String targetStatus);

    /**
     * 按当前查询条件统计各状态房源数量（用于列表页统计看板）。
     *
     * @param house 查询条件（复用 selectHouseList 的条件）
     * @return 各状态计数：total / vacant(0) / booked(1) / rented(2) / maintain(3) / offline(4)
     */
    Map<String, Integer> selectHouseStatusStats(HzHouse house);
}
