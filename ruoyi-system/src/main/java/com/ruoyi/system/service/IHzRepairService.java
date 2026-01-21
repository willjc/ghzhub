package com.ruoyi.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.system.domain.HzRepair;

import java.util.List;

/**
 * 物业报修Service接口
 *
 * @author ruoyi
 */
public interface IHzRepairService extends IService<HzRepair>
{
    /**
     * 分页查询物业报修
     *
     * @param repair 物业报修
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 物业报修分页数据
     */
    IPage<HzRepair> selectRepairPage(HzRepair repair, int pageNum, int pageSize);

    /**
     * 查询物业报修列表
     *
     * @param repair 物业报修
     * @return 物业报修集合
     */
    List<HzRepair> selectRepairList(HzRepair repair);

    /**
     * 查询用户的物业报修列表
     *
     * @param userId 用户ID
     * @return 物业报修集合
     */
    List<HzRepair> selectRepairListByUserId(Long userId);

    /**
     * 查询物业报修详情
     *
     * @param repairId 物业报修ID
     * @return 物业报修
     */
    HzRepair selectRepairById(Long repairId);

    /**
     * 新增物业报修
     *
     * @param repair 物业报修
     * @return 结果
     */
    int insertRepair(HzRepair repair);

    /**
     * 修改物业报修
     *
     * @param repair 物业报修
     * @return 结果
     */
    int updateRepair(HzRepair repair);

    /**
     * 处理物业报修
     *
     * @param repair 物业报修（包含处理结果）
     * @return 结果
     */
    int handleRepair(HzRepair repair);

    /**
     * 取消物业报修
     *
     * @param repairId 物业报修ID
     * @param userId 用户ID（用于验证权限）
     * @return 结果
     */
    int cancelRepair(Long repairId, Long userId);

    /**
     * 删除物业报修（逻辑删除）
     *
     * @param repairId 物业报修ID
     * @return 结果
     */
    int deleteRepairById(Long repairId);

    /**
     * 生成报修编号
     *
     * @return 报修编号 格式: BX + yyyyMMdd + 4位序号
     */
    String generateRepairNo();
}
