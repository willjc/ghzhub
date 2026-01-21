package com.ruoyi.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.system.domain.HzOperationConfig;

import java.util.List;

/**
 * 运营配置Service接口
 *
 * @author ruoyi
 * @date 2026-01-12
 */
public interface IHzOperationConfigService extends IService<HzOperationConfig> {
    /**
     * 查询运营配置列表
     *
     * @param config 运营配置
     * @return 运营配置集合
     */
    List<HzOperationConfig> selectConfigList(HzOperationConfig config);

    /**
     * 新增运营配置
     *
     * @param config 运营配置
     * @return 结果
     */
    int insertConfig(HzOperationConfig config);

    /**
     * 修改运营配置
     *
     * @param config 运营配置
     * @return 结果
     */
    int updateConfig(HzOperationConfig config);

    /**
     * 批量删除运营配置
     *
     * @param configIds 需要删除的运营配置主键集合
     * @return 结果
     */
    int deleteConfigByIds(Long[] configIds);
}
