package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.HzBuilding;
import com.ruoyi.system.mapper.HzBuildingMapper;
import com.ruoyi.system.service.IHzBuildingService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 楼栋Service业务层处理
 *
 * @author ruoyi
 */
@Service
public class HzBuildingServiceImpl extends ServiceImpl<HzBuildingMapper, HzBuilding> implements IHzBuildingService {

    @Override
    public HzBuilding selectBuildingById(Long buildingId) {
        return this.getById(buildingId);
    }

    @Override
    public List<HzBuilding> selectBuildingList(HzBuilding building) {
        // 使用自定义SQL查询,包含关联项目名称和统计总房源数
        return this.baseMapper.selectBuildingAllocationList(building);
    }

    @Override
    public IPage<HzBuilding> selectBuildingPage(HzBuilding building, int pageNum, int pageSize) {
        Page<HzBuilding> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<HzBuilding> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(building.getProjectId() != null, HzBuilding::getProjectId, building.getProjectId())
               .like(StringUtils.isNotEmpty(building.getBuildingName()), HzBuilding::getBuildingName, building.getBuildingName())
               .eq(StringUtils.isNotEmpty(building.getBuildingCode()), HzBuilding::getBuildingCode, building.getBuildingCode())
               .eq(StringUtils.isNotEmpty(building.getStatus()), HzBuilding::getStatus, building.getStatus())
               .eq(HzBuilding::getDelFlag, "0")
               .orderByAsc(HzBuilding::getSortOrder)
               .orderByDesc(HzBuilding::getCreateTime);
        return this.page(page, wrapper);
    }

    @Override
    public int insertBuilding(HzBuilding building) {
        building.setDelFlag("0");
        return this.save(building) ? 1 : 0;
    }

    @Override
    public int updateBuilding(HzBuilding building) {
        return this.updateById(building) ? 1 : 0;
    }

    @Override
    public int deleteBuildingById(Long buildingId) {
        LambdaUpdateWrapper<HzBuilding> wrapper = new LambdaUpdateWrapper<>();
        wrapper.set(HzBuilding::getDelFlag, "2")
               .eq(HzBuilding::getBuildingId, buildingId)
               .eq(HzBuilding::getDelFlag, "0");
        return this.update(wrapper) ? 1 : 0;
    }
}
