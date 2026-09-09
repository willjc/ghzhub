package com.ruoyi.system.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.domain.HzContract;

import java.util.List;
import java.util.Map;

/**
 * 合同Mapper接口
 *
 * @author ruoyi
 */
@Mapper
public interface HzContractMapper extends BaseMapper<HzContract> {

    @org.apache.ibatis.annotations.Select("SELECT c.contract_id FROM hz_contract c "
            + "JOIN hz_project p ON p.project_id = c.project_id "
            + "WHERE c.tenant_id = #{tenantId} AND p.project_type = #{projectType}")
    java.util.Set<Long> selectContractIdsByProjectType(@Param("tenantId") Long tenantId,
                                                     @Param("projectType") String projectType);

    /**
     * 根据用户ID查询合同列表（关联项目、楼栋、单元信息）
     *
     * @param userId 用户ID
     * @param projectType 项目类型（可选，1:人才公寓 2:保租房 3:市场租赁），为空则不过滤
     * @return 合同列表
     */
    List<Map<String, Object>> selectContractVOByUserId(@Param("userId") Long userId,
                                                        @Param("projectType") String projectType);

    /**
     * 根据合同ID查询合同详情（关联项目、楼栋、单元��房源信息）
     *
     * @param contractId 合同ID
     * @return 合同详情
     */
    Map<String, Object> selectContractDetailById(@Param("contractId") Long contractId);
}
