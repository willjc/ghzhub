package com.ruoyi.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ruoyi.system.domain.HzBuilding;

import java.util.List;

/**
 * 楼栋Service接口
 *
 * @author ruoyi
 */
public interface IHzBuildingService {
    /**
     * 查询楼栋
     *
     * @param buildingId 楼栋主键
     * @return 楼栋
     */
    HzBuilding selectBuildingById(Long buildingId);

    /**
     * 查询楼栋列表
     *
     * @param building 楼栋
     * @return 楼栋集合
     */
    List<HzBuilding> selectBuildingList(HzBuilding building);

    /**
     * 分页查询楼栋列表
     *
     * @param building 楼栋
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 楼栋集合
     */
    IPage<HzBuilding> selectBuildingPage(HzBuilding building, int pageNum, int pageSize);

    /**
     * 新增楼栋
     *
     * @param building 楼栋
     * @return 结果
     */
    int insertBuilding(HzBuilding building);

    /**
     * 修改楼栋
     *
     * @param building 楼栋
     * @return 结果
     */
    int updateBuilding(HzBuilding building);

    /**
     * 删除楼栋信息
     *
     * @param buildingId 楼栋主键
     * @return 结果
     */
    int deleteBuildingById(Long buildingId);
}
