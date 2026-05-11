package com.ruoyi.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.system.domain.HzHouseType;

import java.util.List;
import java.util.Map;

/**
 * 户型Service接口
 *
 * @author ruoyi
 * @date 2025-11-18
 */
public interface IHzHouseTypeService extends IService<HzHouseType>
{
    /**
     * 查询户型列表
     *
     * @param houseType 户型查询条件
     * @return 户型列表
     */
    List<HzHouseType> selectHouseTypeList(HzHouseType houseType);

    /**
     * 分页查询户型列表
     *
     * @param houseType 户型查询条件
     * @param pageNum 当前页码
     * @param pageSize 每页数量
     * @return 户型分页列表
     */
    IPage<HzHouseType> selectHouseTypePage(HzHouseType houseType, int pageNum, int pageSize);

    /**
     * 查询户型详情
     *
     * @param houseTypeId 户型ID
     * @return 户型
     */
    HzHouseType selectHouseTypeById(Long houseTypeId);

    /**
     * 新增户型
     *
     * @param houseType 户型
     * @return 结果
     */
    int insertHouseType(HzHouseType houseType);

    /**
     * 修改户型
     *
     * @param houseType 户型
     * @return 结果
     */
    int updateHouseType(HzHouseType houseType);

    /**
     * 删除户型
     *
     * @param houseTypeId 户型ID
     * @return 结果
     */
    int deleteHouseTypeById(Long houseTypeId);

    /**
     * 批量删除户型
     *
     * @param houseTypeIds 户型ID数组
     * @return 结果
     */
    int deleteHouseTypeByIds(Long[] houseTypeIds);

    /**
     * 将户型的 6 类图片与 VR 下发到该户型所有房源。
     * 仅填空：房源对应类别无图片则补齐；房源无 VR 才补 VR。
     * 不覆盖房源已有数据。
     *
     * @param houseTypeId 户型ID
     * @return 统计结果: total 该户型下房源总数, imageFilled 补了图片的房源数, vrFilled 补了VR的房源数
     */
    Map<String, Integer> pushImagesAndVrToHouses(Long houseTypeId);
}
