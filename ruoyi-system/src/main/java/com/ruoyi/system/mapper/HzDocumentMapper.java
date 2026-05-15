package com.ruoyi.system.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.system.domain.HzDocument;

import java.util.Map;

/**
 * 资料文档Mapper接口
 *
 * @author ruoyi
 */
@Mapper
public interface HzDocumentMapper extends BaseMapper<HzDocument> {

    /**
     * 管理端：分页查询资料文档（联表查询用户/合同/项目/房源）
     *
     * <p>筛选项：审核状态、资料类型、用户昵称（模糊）、合同编号（模糊）、上传时间区间</p>
     */
    @Select({
        "<script>",
        "SELECT d.document_id AS documentId,",
        "       d.tenant_id AS tenantId,",
        "       d.contract_id AS contractId,",
        "       d.document_type AS documentType,",
        "       d.document_name AS documentName,",
        "       d.file_path AS filePath,",
        "       d.file_size AS fileSize,",
        "       d.file_format AS fileFormat,",
        "       d.audit_status AS auditStatus,",
        "       d.auditor AS auditor,",
        "       d.audit_time AS auditTime,",
        "       d.audit_opinion AS auditOpinion,",
        "       d.create_time AS createTime,",
        "       t.real_name AS tenantName,",
        "       t.phone AS tenantPhone,",
        "       t.id_card AS tenantIdCard,",
        "       c.contract_no AS contractNo,",
        "       p.project_name AS projectName,",
        "       b.building_name AS buildingName,",
        "       u.unit_name AS unitName,",
        "       h.house_no AS houseNo",
        "  FROM hz_document d",
        "  LEFT JOIN hz_user t ON t.user_id = d.tenant_id",
        "  LEFT JOIN hz_contract c ON c.contract_id = d.contract_id AND c.del_flag = '0'",
        "  LEFT JOIN hz_project p ON p.project_id = c.project_id",
        "  LEFT JOIN hz_house h ON h.house_id = c.house_id",
        "  LEFT JOIN hz_building b ON b.building_id = h.building_id",
        "  LEFT JOIN hz_unit u ON u.unit_id = h.unit_id",
        " WHERE d.del_flag = '0'",
        "<if test='auditStatus != null and auditStatus != \"\"'> AND d.audit_status = #{auditStatus} </if>",
        "<if test='documentType != null and documentType != \"\"'> AND d.document_type = #{documentType} </if>",
        "<if test='tenantName != null and tenantName != \"\"'> AND t.real_name LIKE CONCAT('%', #{tenantName}, '%') </if>",
        "<if test='contractNo != null and contractNo != \"\"'> AND c.contract_no LIKE CONCAT('%', #{contractNo}, '%') </if>",
        "<if test='startTime != null and startTime != \"\"'> AND d.create_time &gt;= #{startTime} </if>",
        "<if test='endTime != null and endTime != \"\"'> AND d.create_time &lt;= #{endTime} </if>",
        " ORDER BY d.audit_status ASC, d.create_time DESC",
        "</script>"
    })
    IPage<Map<String, Object>> selectDocumentPageForAdmin(
            Page<Map<String, Object>> page,
            @Param("auditStatus") String auditStatus,
            @Param("documentType") String documentType,
            @Param("tenantName") String tenantName,
            @Param("contractNo") String contractNo,
            @Param("startTime") String startTime,
            @Param("endTime") String endTime);
}
