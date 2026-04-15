package com.ruoyi.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.system.domain.HzBatchAllocation;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 配租批次Service接口
 *
 * @author ruoyi
 */
public interface IHzBatchAllocationService extends IService<HzBatchAllocation> {

    /**
     * 查询配租批次列表
     *
     * @param batch 配租批次
     * @return 配租批次集合
     */
    List<HzBatchAllocation> selectBatchAllocationList(HzBatchAllocation batch);

    /**
     * 查询配租批次详情
     *
     * @param batchId 配租批次ID
     * @return 配租批次
     */
    HzBatchAllocation selectBatchAllocationById(Long batchId);

    /**
     * 新增配租批次
     *
     * @param batch 配租批次
     * @return 结果
     */
    int insertBatchAllocation(HzBatchAllocation batch);

    /**
     * 修改配租批次
     *
     * @param batch 配租批次
     * @return 结果
     */
    int updateBatchAllocation(HzBatchAllocation batch);

    /**
     * 删除配租批次
     *
     * @param batchId 配租批次ID
     * @return 结果
     */
    int deleteBatchAllocationById(Long batchId);

    /**
     * 批量删除配租批次
     *
     * @param batchIds 配租批次ID数组
     * @return 结果
     */
    int deleteBatchAllocationByIds(Long[] batchIds);

    /**
     * 审批配租批次
     *
     * @param batchId 批次ID
     * @param approveStatus 审批状态(1通过 2拒绝)
     * @param remark 审批意见
     * @return 结果
     */
    int approveBatchAllocation(Long batchId, String approveStatus, String remark);

    /**
     * 作废配租批次
     *
     * @param batchId 批次ID
     * @return 结果
     */
    int cancelBatchAllocation(Long batchId);

    /**
     * 下载人员导入模板
     *
     * @param response 响应对象
     */
    void downloadTenantTemplate(HttpServletResponse response);

    /**
     * 根据项目ID获取可用房源列表
     *
     * @param projectIds 项目ID列表(逗号分隔)
     * @return 房源列表
     */
    List<Map<String, Object>> getAvailableHousesByProjects(String projectIds);

    /**
     * 导入人员数据
     *
     * @param file Excel文件
     * @return 人员列表
     * @throws Exception 异常
     */
    List<Map<String, Object>> importTenants(MultipartFile file) throws Exception;

    /**
     * 保存批次分配（包含房源和人员匹配）
     *
     * @param params 参数(包含批次信息、房源列表、人员列表)
     * @return 结果
     */
    int saveBatchAllocation(Map<String, Object> params);

    /**
     * 获取批次房源列表
     *
     * @param batchId 批次ID
     * @return 房源列表
     */
    List<Map<String, Object>> getBatchHouses(Long batchId);

    /**
     * 获取批次人员列表
     *
     * @param batchId 批次ID
     * @return 人员列表
     */
    List<Map<String, Object>> getBatchTenants(Long batchId);

    /**
     * 分页查询配租批次列表
     */
    IPage<HzBatchAllocation> selectBatchAllocationPage(Page<HzBatchAllocation> page, HzBatchAllocation batch);
}
