package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.domain.HzCheckIn;

import java.util.List;
import java.util.Map;

/**
 * 入住申请Mapper接口
 *
 * @author ruoyi
 */
@Mapper
public interface HzCheckInMapper extends BaseMapper<HzCheckIn> {

    /**
     * 分页查询入住记录（带关联信息）
     *
     * @param page 分页对象
     * @param checkIn 查询条件
     * @return 分页结果
     */
    @Select("<script>" +
            "SELECT " +
            "  c.record_id, c.checkin_no, c.apply_id, c.contract_id, c.tenant_id, c.house_id, " +
            "  c.checkin_date, c.actual_checkin_date, c.roommate_info, " +
            "  c.emergency_contact_name, c.emergency_contact_relation, c.emergency_contact_phone, " +
            "  c.audit_by, c.audit_time, c.audit_remark, c.checkin_time, " +
            "  c.meter_reading_electric, c.meter_reading_water, c.meter_reading_gas, " +
            "  c.key_count, c.inventory_list_id, c.checkin_photos, " +
            "  c.tenant_signature, c.manager_signature, c.manager_id, c.manager_name, " +
            "  c.status, c.create_by, c.create_time, c.update_by, c.update_time, c.remark, c.del_flag, " +
            "  ct.contract_no AS contractNo, " +
            "  u.nickname AS tenantNickname, " +
            "  h.house_no AS houseName, " +
            "  u.real_name AS realName, u.id_card AS idCard, u.phone, " +
            "  p.project_name AS projectName, b.building_name AS buildingName, " +
            "  un.unit_name AS unitName, " +
            "  h.floor, h.orientation, h.area, " +
            "  ct.start_date AS startDate, ct.end_date AS endDate, " +
            "  DATEDIFF(ct.end_date, CURDATE()) AS remainingDays " +
            "FROM hz_checkin_record c " +
            "LEFT JOIN hz_contract ct ON c.contract_id = ct.contract_id " +
            "LEFT JOIN hz_user u ON c.tenant_id = u.user_id " +
            "LEFT JOIN hz_house h ON c.house_id = h.house_id " +
            "LEFT JOIN hz_building b ON h.building_id = b.building_id " +
            "LEFT JOIN hz_unit un ON h.unit_id = un.unit_id " +
            "LEFT JOIN hz_project p ON h.project_id = p.project_id " +
            "WHERE c.del_flag = '0' " +
            "<if test='checkIn.tenantId != null'> AND c.tenant_id = #{checkIn.tenantId} </if>" +
            "<if test='checkIn.contractId != null'> AND c.contract_id = #{checkIn.contractId} </if>" +
            "<if test='checkIn.houseId != null'> AND c.house_id = #{checkIn.houseId} </if>" +
            "<if test='checkIn.status != null and checkIn.status != \"\"'> AND c.status = #{checkIn.status} </if>" +
            "<if test='checkIn.checkinNo != null and checkIn.checkinNo != \"\"'> AND c.checkin_no LIKE CONCAT('%', #{checkIn.checkinNo}, '%') </if>" +
            "<if test='checkIn.realName != null and checkIn.realName != \"\"'> AND u.real_name LIKE CONCAT('%', #{checkIn.realName}, '%') </if>" +
            "<if test='checkIn.projectName != null and checkIn.projectName != \"\"'> AND p.project_name LIKE CONCAT('%', #{checkIn.projectName}, '%') </if>" +
            "<if test='checkIn.phone != null and checkIn.phone != \"\"'> AND u.phone LIKE CONCAT('%', #{checkIn.phone}, '%') </if>" +
            "ORDER BY c.create_time DESC" +
            "</script>")
    IPage<HzCheckIn> selectCheckInPageWithRelations(Page<HzCheckIn> page, @Param("checkIn") HzCheckIn checkIn);

    /**
     * 根据ID查询入住记录（带关联信息）
     *
     * @param recordId 入住记录ID
     * @return 入住记录
     */
    @Select("SELECT " +
            "  c.record_id, c.checkin_no, c.apply_id, c.contract_id, c.tenant_id, c.house_id, " +
            "  c.checkin_date, c.actual_checkin_date, c.roommate_info, " +
            "  c.emergency_contact_name, c.emergency_contact_relation, c.emergency_contact_phone, " +
            "  c.audit_by, c.audit_time, c.audit_remark, c.checkin_time, " +
            "  c.meter_reading_electric, c.meter_reading_water, c.meter_reading_gas, " +
            "  c.key_count, c.inventory_list_id, c.checkin_photos, " +
            "  c.tenant_signature, c.manager_signature, c.manager_id, c.manager_name, " +
            "  c.status, c.create_by, c.create_time, c.update_by, c.update_time, c.remark, c.del_flag, " +
            "  ct.contract_no AS contractNo, " +
            "  u.nickname AS tenantNickname, " +
            "  h.house_no AS houseName, " +
            "  u.real_name AS realName, u.id_card AS idCard, u.phone, " +
            "  p.project_name AS projectName, b.building_name AS buildingName, " +
            "  un.unit_name AS unitName, " +
            "  h.floor, h.orientation, h.area, " +
            "  ct.start_date AS startDate, ct.end_date AS endDate, " +
            "  DATEDIFF(ct.end_date, CURDATE()) AS remainingDays " +
            "FROM hz_checkin_record c " +
            "LEFT JOIN hz_contract ct ON c.contract_id = ct.contract_id " +
            "LEFT JOIN hz_user u ON c.tenant_id = u.user_id " +
            "LEFT JOIN hz_house h ON c.house_id = h.house_id " +
            "LEFT JOIN hz_building b ON h.building_id = b.building_id " +
            "LEFT JOIN hz_unit un ON h.unit_id = un.unit_id " +
            "LEFT JOIN hz_project p ON h.project_id = p.project_id " +
            "WHERE c.record_id = #{recordId} AND c.del_flag = '0'")
    HzCheckIn selectCheckInByIdWithRelations(@Param("recordId") Long recordId);

    /**
     * 查询入住记录列表（带关联信息）
     *
     * @param checkIn 查询条件
     * @return 入住记录列表
     */
    @Select("<script>" +
            "SELECT " +
            "  c.record_id, c.checkin_no, c.apply_id, c.contract_id, c.tenant_id, c.house_id, " +
            "  c.checkin_date, c.actual_checkin_date, c.roommate_info, " +
            "  c.emergency_contact_name, c.emergency_contact_relation, c.emergency_contact_phone, " +
            "  c.audit_by, c.audit_time, c.audit_remark, c.checkin_time, " +
            "  c.meter_reading_electric, c.meter_reading_water, c.meter_reading_gas, " +
            "  c.key_count, c.inventory_list_id, c.checkin_photos, " +
            "  c.tenant_signature, c.manager_signature, c.manager_id, c.manager_name, " +
            "  c.status, c.create_by, c.create_time, c.update_by, c.update_time, c.remark, c.del_flag, " +
            "  ct.contract_no AS contractNo, " +
            "  u.nickname AS tenantNickname, " +
            "  h.house_no AS houseName, " +
            "  u.real_name AS realName, u.id_card AS idCard, u.phone, " +
            "  p.project_name AS projectName, b.building_name AS buildingName, " +
            "  un.unit_name AS unitName, " +
            "  h.floor, h.orientation, h.area, " +
            "  ct.start_date AS startDate, ct.end_date AS endDate, " +
            "  DATEDIFF(ct.end_date, CURDATE()) AS remainingDays " +
            "FROM hz_checkin_record c " +
            "LEFT JOIN hz_contract ct ON c.contract_id = ct.contract_id " +
            "LEFT JOIN hz_user u ON c.tenant_id = u.user_id " +
            "LEFT JOIN hz_house h ON c.house_id = h.house_id " +
            "LEFT JOIN hz_building b ON h.building_id = b.building_id " +
            "LEFT JOIN hz_unit un ON h.unit_id = un.unit_id " +
            "LEFT JOIN hz_project p ON h.project_id = p.project_id " +
            "WHERE c.del_flag = '0' " +
            "<if test='tenantId != null'> AND c.tenant_id = #{tenantId} </if>" +
            "<if test='contractId != null'> AND c.contract_id = #{contractId} </if>" +
            "<if test='houseId != null'> AND c.house_id = #{houseId} </if>" +
            "<if test='status != null and status != \"\"'> AND c.status = #{status} </if>" +
            "<if test='checkinNo != null and checkinNo != \"\"'> AND c.checkin_no LIKE CONCAT('%', #{checkinNo}, '%') </if>" +
            "<if test='realName != null and realName != \"\"'> AND u.real_name LIKE CONCAT('%', #{realName}, '%') </if>" +
            "<if test='projectName != null and projectName != \"\"'> AND p.project_name LIKE CONCAT('%', #{projectName}, '%') </if>" +
            "<if test='phone != null and phone != \"\"'> AND u.phone LIKE CONCAT('%', #{phone}, '%') </if>" +
            "ORDER BY c.create_time DESC" +
            "</script>")
    List<HzCheckIn> selectCheckInListWithRelations(HzCheckIn checkIn);

    /**
     * 查询用户的所有房源列表（用于用户端我的房源页面）
     * 关联项目、房源、合同、退租记录信息
     *
     * @param tenantId 租户ID
     * @return 房源列表
     */
    @Select("SELECT " +
            "  ct.contract_id AS record_id, ct.tenant_id, h.house_id, " +
            "  ct.contract_status AS checkin_status, ct.start_date AS checkin_date, ct.create_time, " +
            "  p.project_name, p.cover_image, p.facilities, " +
            "  h.orientation, h.area, h.house_no, h.floor, h.house_code, " +
            "  ct.start_date, ct.end_date, " +
            "  CASE WHEN chr.record_id IS NOT NULL THEN 1 ELSE 0 END AS is_checked_out " +
            "FROM hz_contract ct " +
            "LEFT JOIN hz_house h ON ct.house_id = h.house_id " +
            "LEFT JOIN hz_project p ON h.project_id = p.project_id " +
            "LEFT JOIN hz_checkout_record chr ON ct.contract_id = chr.contract_id " +
            "WHERE ct.tenant_id = #{tenantId} " +
            "  AND ct.del_flag = '0' " +
            "  AND ct.contract_status IN ('2','3','4','5') " +
            "ORDER BY ct.create_time DESC")
    List<Map<String, Object>> selectMyListingsByTenantId(@Param("tenantId") Long tenantId);
}
