package com.ruoyi.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ruoyi.system.domain.HzCheckIn;

import java.util.List;

/**
 * 入住申请Service接口
 *
 * @author ruoyi
 */
public interface IHzCheckInService {

    /**
     * 查询入住申请
     *
     * @param recordId 入住记录主键
     * @return 入住申请
     */
    public HzCheckIn selectCheckInById(Long recordId);

    /**
     * 根据租户ID查询入住申请列表
     *
     * @param tenantId 租户ID
     * @return 入住申请列表
     */
    public List<HzCheckIn> selectCheckInListByTenantId(Long tenantId);

    /**
     * 根据租户ID查询已入住确认的入住申请列表 (用于续租页面)
     *
     * @param tenantId 租户ID
     * @return 已入住确认的入住申请列表 (status='4')
     */
    public List<HzCheckIn> selectConfirmedCheckInListByTenantId(Long tenantId);

    /**
     * 根据合同ID查询入住申请
     *
     * @param contractId 合同ID
     * @return 入住申请
     */
    public HzCheckIn selectCheckInByContractId(Long contractId);

    /**
     * 查询入住申请列表
     *
     * @param checkIn 入住申请
     * @return 入住申请列表
     */
    public List<HzCheckIn> selectCheckInList(HzCheckIn checkIn);

    /**
     * 分页查询入住申请列表
     *
     * @param checkIn 入住申请
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @return 入住申请分页数据
     */
    public IPage<HzCheckIn> selectCheckInPage(HzCheckIn checkIn, int pageNum, int pageSize);

    /**
     * 新增入住申请
     *
     * @param checkIn 入住申请
     * @return 结果
     */
    public int insertCheckIn(HzCheckIn checkIn);

    /**
     * 修改入住申请
     *
     * @param checkIn 入住申请
     * @return 结果
     */
    public int updateCheckIn(HzCheckIn checkIn);

    /**
     * 删除入住申请
     *
     * @param recordId 入住记录主键
     * @return 结果
     */
    public int deleteCheckInById(Long recordId);

    /**
     * 更新入住申请状态
     *
     * @param recordId 入住记录ID
     * @param status 状态
     * @return 结果
     */
    public int updateCheckInStatus(Long recordId, String status);

    /**
     * 生成入住单号
     *
     * @return 入住单号
     */
    public String generateCheckinNo();
}
