package com.ruoyi.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ruoyi.system.domain.HzContract;

import java.util.List;

/**
 * 合同Service接口
 *
 * @author ruoyi
 */
public interface IHzContractService {
    /**
     * 查询合同
     *
     * @param contractId 合同ID
     * @return 合同
     */
    HzContract selectContractById(Long contractId);

    /**
     * 根据合同编号查询合同
     *
     * @param contractNo 合同编号
     * @return 合同
     */
    HzContract selectContractByContractNo(String contractNo);

    /**
     * 根据租户ID查询合同列表
     *
     * @param tenantId 租户ID
     * @return 合同列表
     */
    List<HzContract> selectContractListByTenantId(Long tenantId);

    /**
     * 获取租户的待退租合同列表（履行中且无进行中的退租申请）
     *
     * @param tenantId 租户ID
     * @return 待退租合同列表
     */
    List<HzContract> selectCheckoutableContractList(Long tenantId);


    /**
     * 根据房源ID查询当前有效合同
     *
     * @param houseId 房源ID
     * @return 合同
     */
    HzContract selectValidContractByHouseId(Long houseId);

    /**
     * 查询合同列表
     *
     * @param contract 合同
     * @return 合同列表
     */
    List<HzContract> selectContractList(HzContract contract);

    /**
     * 分页查询合同
     *
     * @param contract 合同
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 合同集合
     */
    IPage<HzContract> selectContractPage(HzContract contract, int pageNum, int pageSize);

    /**
     * 新增合同
     *
     * @param contract 合同
     * @return 结果
     */
    int insertContract(HzContract contract);

    /**
     * 修改合同
     *
     * @param contract 合同
     * @return 结果
     */
    int updateContract(HzContract contract);

    /**
     * 删除合同
     *
     * @param contractId 合同ID
     * @return 结果
     */
    int deleteContractById(Long contractId);

    /**
     * 生成合同编号
     *
     * @return 合同编号
     */
    String generateContractNo();
}
