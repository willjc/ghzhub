package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.system.domain.HzUnit;
import com.ruoyi.system.mapper.HzUnitMapper;
import com.ruoyi.system.service.IHzUnitService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 单元Service业务层处理
 *
 * @author ruoyi
 */
@Service
public class HzUnitServiceImpl extends ServiceImpl<HzUnitMapper, HzUnit> implements IHzUnitService {

    @Override
    public HzUnit selectUnitById(Long unitId) {
        return this.getById(unitId);
    }

    @Override
    public List<HzUnit> selectUnitList(HzUnit unit) {
        // 使用自定义SQL查询,包含关联楼栋名称、项目名称和统计总房源数
        // 注意：此方法需要配合 PageHelper 才能正常分页
        // PageHelper 会拦截此查询并自动添加分页信息到返回的 List 中（Page对象）
        return this.baseMapper.selectUnitAllocationList(unit);
    }

    @Override
    public IPage<HzUnit> selectUnitPage(HzUnit unit, int pageNum, int pageSize) {
        Page<HzUnit> page = new Page<>(pageNum, pageSize);
        // 使用自定义SQL查询进行分页（包含关联楼栋名称、项目名称和统计总房源数）
        // MyBatis-Plus会自动处理分页
        return this.baseMapper.selectUnitAllocationPage(page, unit);
    }

    @Override
    public int insertUnit(HzUnit unit) {
        unit.setDelFlag("0");
        return this.save(unit) ? 1 : 0;
    }

    @Override
    public int updateUnit(HzUnit unit) {
        return this.updateById(unit) ? 1 : 0;
    }

    @Override
    public int deleteUnitById(Long unitId) {
        // 使用LambdaUpdateWrapper进行逻辑删除
        return this.lambdaUpdate()
                .set(HzUnit::getDelFlag, "2")
                .eq(HzUnit::getUnitId, unitId)
                .eq(HzUnit::getDelFlag, "0")
                .update() ? 1 : 0;
    }
}
