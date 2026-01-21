package com.ruoyi.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ruoyi.system.domain.HzComplaint;

import java.util.List;

/**
 * 投诉建议Service接口
 *
 * @author ruoyi
 */
public interface IHzComplaintService
{
    /**
     * 分页查询投诉建议
     *
     * @param complaint 投诉建议
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 投诉建议分页数据
     */
    IPage<HzComplaint> selectComplaintPage(HzComplaint complaint, int pageNum, int pageSize);

    /**
     * 查询投诉建议列表
     *
     * @param complaint 投诉建议
     * @return 投诉建议集合
     */
    List<HzComplaint> selectComplaintList(HzComplaint complaint);

    /**
     * 查询用户的投诉建议列表
     *
     * @param userId 用户ID
     * @return 投诉建议集合
     */
    List<HzComplaint> selectComplaintListByUserId(Long userId);

    /**
     * 查询投诉建议详情
     *
     * @param complaintId 投诉建议ID
     * @return 投诉建议
     */
    HzComplaint selectComplaintById(Long complaintId);

    /**
     * 新增投诉建议
     *
     * @param complaint 投诉建议
     * @return 结果
     */
    int insertComplaint(HzComplaint complaint);

    /**
     * 修改投诉建议
     *
     * @param complaint 投诉建议
     * @return 结果
     */
    int updateComplaint(HzComplaint complaint);

    /**
     * 催办投诉建议
     *
     * @param complaintId 投诉建议ID
     * @return 结果
     */
    int urgeComplaint(Long complaintId);

    /**
     * 处理投诉建议
     *
     * @param complaint 投诉建议（包含处理结果）
     * @return 结果
     */
    int handleComplaint(HzComplaint complaint);

    /**
     * 取消投诉建议
     *
     * @param complaintId 投诉建议ID
     * @param userId 用户ID（用于验证权限）
     * @return 结果
     */
    int cancelComplaint(Long complaintId, Long userId);

    /**
     * 删除投诉建议（逻辑删除）
     *
     * @param complaintId 投诉建议ID
     * @return 结果
     */
    int deleteComplaintById(Long complaintId);
}
