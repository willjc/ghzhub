package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.domain.HzCoTenant;

import java.util.List;

/**
 * 合租户申请Mapper接口
 *
 * @author ruoyi
 */
@Mapper
public interface HzCoTenantMapper extends BaseMapper<HzCoTenant> {

    /**
     * 分页查询合租户申请（带关联信息）
     *
     * @param page 分页对象
     * @param coTenant 查询条件
     * @return 分页结果
     */
    @Select("<script>" +
            "SELECT " +
            "  t.co_tenant_id, t.contract_id, t.tenant_name, t.id_card, t.phone, " +
            "  t.relationship, t.status, " +
            "  t.create_by, t.create_time AS applyTime, t.update_by, t.update_time, t.remark, t.del_flag, " +
            "  c.contract_no AS contractNo, " +
            "  c.house_address AS roomAddress, " +
            "  c.tenant_name AS mainTenantName, " +
            "  c.tenant_phone AS mainTenantPhone " +
            "FROM hz_co_tenant t " +
            "LEFT JOIN hz_contract c ON t.contract_id = c.contract_id " +
            "WHERE t.del_flag = '0' " +
            "<if test='coTenant.tenantName != null and coTenant.tenantName != \"\"'> AND t.tenant_name LIKE CONCAT('%', #{coTenant.tenantName}, '%') </if>" +
            "<if test='coTenant.idCard != null and coTenant.idCard != \"\"'> AND t.id_card LIKE CONCAT('%', #{coTenant.idCard}, '%') </if>" +
            "<if test='coTenant.status != null and coTenant.status != \"\"'> AND t.status = #{coTenant.status} </if>" +
            "ORDER BY t.create_time DESC" +
            "</script>")
    IPage<HzCoTenant> selectCoTenantPageWithRelations(Page<HzCoTenant> page, @Param("coTenant") HzCoTenant coTenant);

    /**
     * 根据ID查询合租户申请（带关联信息）
     *
     * @param coTenantId 合租户ID
     * @return 合租户申请
     */
    @Select("SELECT " +
            "  t.co_tenant_id, t.contract_id, t.tenant_name, t.id_card, t.phone, " +
            "  t.relationship, t.status, " +
            "  t.create_by, t.create_time AS applyTime, t.update_by, t.update_time, t.remark, t.del_flag, " +
            "  c.contract_no AS contractNo, " +
            "  c.house_address AS roomAddress, " +
            "  c.tenant_name AS mainTenantName, " +
            "  c.tenant_phone AS mainTenantPhone " +
            "FROM hz_co_tenant t " +
            "LEFT JOIN hz_contract c ON t.contract_id = c.contract_id " +
            "WHERE t.co_tenant_id = #{coTenantId} AND t.del_flag = '0'")
    HzCoTenant selectCoTenantByIdWithRelations(@Param("coTenantId") Long coTenantId);

    /**
     * 查询合租户申请列表（带关联信息）
     *
     * @param coTenant 查询条件
     * @return 合租户申请列表
     */
    @Select("<script>" +
            "SELECT " +
            "  t.co_tenant_id, t.contract_id, t.tenant_name, t.id_card, t.phone, " +
            "  t.relationship, t.status, " +
            "  t.create_by, t.create_time AS applyTime, t.update_by, t.update_time, t.remark, t.del_flag, " +
            "  c.contract_no AS contractNo, " +
            "  c.house_address AS roomAddress, " +
            "  c.tenant_name AS mainTenantName, " +
            "  c.tenant_phone AS mainTenantPhone " +
            "FROM hz_co_tenant t " +
            "LEFT JOIN hz_contract c ON t.contract_id = c.contract_id " +
            "WHERE t.del_flag = '0' " +
            "<if test='tenantName != null and tenantName != \"\"'> AND t.tenant_name LIKE CONCAT('%', #{tenantName}, '%') </if>" +
            "<if test='idCard != null and idCard != \"\"'> AND t.id_card LIKE CONCAT('%', #{idCard}, '%') </if>" +
            "<if test='status != null and status != \"\"'> AND t.status = #{status} </if>" +
            "ORDER BY t.create_time DESC" +
            "</script>")
    List<HzCoTenant> selectCoTenantListWithRelations(HzCoTenant coTenant);
}
