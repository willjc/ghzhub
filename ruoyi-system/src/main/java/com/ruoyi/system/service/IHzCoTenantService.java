package com.ruoyi.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.system.domain.HzCoTenant;

import java.util.List;

/**
 * 合租户申请Service接口
 *
 * @author ruoyi
 */
public interface IHzCoTenantService {

    /**
     * 查询合租户申请
     *
     * @param coTenantId 合租户主键
     * @return 合租户申请
     */
    public HzCoTenant selectCoTenantById(Long coTenantId);

    /**
     * 根据租户ID查询合租户申请列表
     *
     * @param tenantId 租户ID
     * @return 合租户申请列表
     */
    public List<HzCoTenant> selectCoTenantListByTenantId(Long tenantId);

    /**
     * 查询合租户申请列表
     *
     * @param coTenant 合租户申请
     * @return 合租户申请列表
     */
    public List<HzCoTenant> selectCoTenantList(HzCoTenant coTenant);

    /**
     * 分页查询合租户申请列表
     *
     * @param coTenant 合租户申请
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @return 合租户申请分页数据
     */
    public IPage<HzCoTenant> selectCoTenantPage(HzCoTenant coTenant, int pageNum, int pageSize);

    /**
     * 新增合租户申请
     *
     * @param coTenant 合租户申请
     * @return 结果
     */
    public int insertCoTenant(HzCoTenant coTenant);

    /**
     * 修改合租户申请
     *
     * @param coTenant 合租户申请
     * @return 结果
     */
    public int updateCoTenant(HzCoTenant coTenant);

    /**
     * 删除合租户申请
     *
     * @param coTenantId 合租户主键
     * @return 结果
     */
    public int deleteCoTenantById(Long coTenantId);

    /**
     * 审核合租户申请
     *
     * @param coTenantId 合租户ID
     * @param status 审核结果(1=通过,2=拒绝)
     * @param auditOpinion 审核意见
     * @return 结果
     */
    public int auditCoTenant(Long coTenantId, String status, String auditOpinion);
}
