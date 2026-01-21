package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.domain.HzHouseExchange;

import java.util.List;

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
    @Select("<script>" +
            "SELECT " +
            "  e.exchange_id, e.tenant_id, e.old_contract_id, e.old_house_id, e.old_house_code, " +
            "  e.new_house_id, e.new_house_code, e.exchange_reason, e.apply_time, " +
            "  e.approve_result, e.approve_opinion, e.approve_by, e.approve_time, " +
            "  e.exchange_time, e.new_contract_id, e.status, " +
            "  e.create_by, e.create_time, e.update_by, e.update_time, e.remark, e.del_flag, " +
            "  ct.contract_no AS contractNo, " +
            "  ct.tenant_name AS tenantName, " +
            "  ct.tenant_id_card AS tenantIdCard, " +
            "  ct.tenant_phone AS tenantPhone, " +
            "  ct.house_address AS roomAddress, " +
            "  ct.rent_price AS oldRent " +
            "FROM hz_house_exchange e " +
            "LEFT JOIN hz_contract ct ON e.old_contract_id = ct.contract_id " +
            "WHERE e.del_flag = '0' " +
            "<if test='exchange.tenantId != null'> AND e.tenant_id = #{exchange.tenantId} </if>" +
            "<if test='exchange.status != null and exchange.status != \"\"'> AND e.status = #{exchange.status} </if>" +
            "<if test='exchange.approveResult != null and exchange.approveResult != \"\"'> AND e.approve_result = #{exchange.approveResult} </if>" +
            "ORDER BY e.create_time DESC" +
            "</script>")
    IPage<HzHouseExchange> selectExchangePageWithRelations(Page<HzHouseExchange> page, @Param("exchange") HzHouseExchange exchange);

    /**
     * 根据ID查询调换房申请（带关联信息）
     *
     * @param exchangeId 调换房ID
     * @return 调换房申请
     */
    @Select("SELECT " +
            "  e.exchange_id, e.tenant_id, e.old_contract_id, e.old_house_id, e.old_house_code, " +
            "  e.new_house_id, e.new_house_code, e.exchange_reason, e.apply_time, " +
            "  e.approve_result, e.approve_opinion, e.approve_by, e.approve_time, " +
            "  e.exchange_time, e.new_contract_id, e.status, " +
            "  e.create_by, e.create_time, e.update_by, e.update_time, e.remark, e.del_flag, " +
            "  ct.contract_no AS contractNo, " +
            "  ct.tenant_name AS tenantName, " +
            "  ct.tenant_id_card AS tenantIdCard, " +
            "  ct.tenant_phone AS tenantPhone, " +
            "  ct.house_address AS roomAddress, " +
            "  ct.rent_price AS oldRent " +
            "FROM hz_house_exchange e " +
            "LEFT JOIN hz_contract ct ON e.old_contract_id = ct.contract_id " +
            "WHERE e.exchange_id = #{exchangeId} AND e.del_flag = '0'")
    HzHouseExchange selectExchangeByIdWithRelations(@Param("exchangeId") Long exchangeId);

    /**
     * 查询调换房申请列表（带关联信息）
     *
     * @param exchange 查询条件
     * @return 调换房申请列表
     */
    @Select("<script>" +
            "SELECT " +
            "  e.exchange_id, e.tenant_id, e.old_contract_id, e.old_house_id, e.old_house_code, " +
            "  e.new_house_id, e.new_house_code, e.exchange_reason, e.apply_time, " +
            "  e.approve_result, e.approve_opinion, e.approve_by, e.approve_time, " +
            "  e.exchange_time, e.new_contract_id, e.status, " +
            "  e.create_by, e.create_time, e.update_by, e.update_time, e.remark, e.del_flag, " +
            "  ct.contract_no AS contractNo, " +
            "  ct.tenant_name AS tenantName, " +
            "  ct.tenant_id_card AS tenantIdCard, " +
            "  ct.tenant_phone AS tenantPhone, " +
            "  ct.house_address AS roomAddress, " +
            "  ct.rent_price AS oldRent " +
            "FROM hz_house_exchange e " +
            "LEFT JOIN hz_contract ct ON e.old_contract_id = ct.contract_id " +
            "WHERE e.del_flag = '0' " +
            "<if test='tenantId != null'> AND e.tenant_id = #{tenantId} </if>" +
            "<if test='status != null and status != \"\"'> AND e.status = #{status} </if>" +
            "<if test='approveResult != null and approveResult != \"\"'> AND e.approve_result = #{approveResult} </if>" +
            "ORDER BY e.create_time DESC" +
            "</script>")
    List<HzHouseExchange> selectExchangeListWithRelations(HzHouseExchange exchange);
}
