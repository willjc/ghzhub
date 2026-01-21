package com.ruoyi.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ruoyi.system.domain.HzUnit;

import java.util.List;

/**
 * 单元Service接口
 *
 * @author ruoyi
 */
public interface IHzUnitService {
    /**
     * 查询单元
     *
     * @param unitId 单元主键
     * @return 单元
     */
    HzUnit selectUnitById(Long unitId);

    /**
     * 查询单元列表
     *
     * @param unit 单元
     * @return 单元集合
     */
    List<HzUnit> selectUnitList(HzUnit unit);

    /**
     * 分页查询单元列表
     *
     * @param unit 单元
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 单元集合
     */
    IPage<HzUnit> selectUnitPage(HzUnit unit, int pageNum, int pageSize);

    /**
     * 新增单元
     *
     * @param unit 单元
     * @return 结果
     */
    int insertUnit(HzUnit unit);

    /**
     * 修改单元
     *
     * @param unit 单元
     * @return 结果
     */
    int updateUnit(HzUnit unit);

    /**
     * 删除单元信息
     *
     * @param unitId 单元主键
     * @return 结果
     */
    int deleteUnitById(Long unitId);
}
