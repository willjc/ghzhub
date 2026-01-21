package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.HzOperationConfig;
import com.ruoyi.system.mapper.HzOperationConfigMapper;
import com.ruoyi.system.service.IHzOperationConfigService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * 运营配置Service业务层处理
 *
 * @author ruoyi
 * @date 2026-01-12
 */
@Service
public class HzOperationConfigServiceImpl extends ServiceImpl<HzOperationConfigMapper, HzOperationConfig> implements IHzOperationConfigService {

    /**
     * 查询运营配置列表
     *
     * @param config 运营配置
     * @return 运营配置
     */
    @Override
    public List<HzOperationConfig> selectConfigList(HzOperationConfig config) {
        LambdaQueryWrapper<HzOperationConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StringUtils.isNotEmpty(config.getConfigType()), HzOperationConfig::getConfigType, config.getConfigType())
                .like(StringUtils.isNotEmpty(config.getTitle()), HzOperationConfig::getTitle, config.getTitle())
                .eq(StringUtils.isNotEmpty(config.getStatus()), HzOperationConfig::getStatus, config.getStatus())
                .orderByAsc(HzOperationConfig::getSortOrder)
                .orderByDesc(HzOperationConfig::getCreateTime);
        return this.list(wrapper);
    }

    /**
     * 新增运营配置
     *
     * @param config 运营配置
     * @return 结果
     */
    @Override
    public int insertConfig(HzOperationConfig config) {
        return this.save(config) ? 1 : 0;
    }

    /**
     * 修改运营配置
     *
     * @param config 运营配置
     * @return 结果
     */
    @Override
    public int updateConfig(HzOperationConfig config) {
        return this.updateById(config) ? 1 : 0;
    }

    /**
     * 批量删除运营配置
     *
     * @param configIds 需要删除的运营配置主键
     * @return 结果
     */
    @Override
    public int deleteConfigByIds(Long[] configIds) {
        return this.removeByIds(Arrays.asList(configIds)) ? configIds.length : 0;
    }
}
