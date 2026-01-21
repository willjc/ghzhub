package com.ruoyi.system.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.domain.HzBuilding;
import java.util.List;

/**
 * 楼栋Mapper接口
 *
 * @author ruoyi
 */
@Mapper
public interface HzBuildingMapper extends BaseMapper<HzBuilding> {

    /**
     * 查询楼栋列表(带关联项目名称和总房源数统计)
     *
     * @param building 楼栋
     * @return 楼栋集合
     */
    List<HzBuilding> selectBuildingAllocationList(HzBuilding building);

}
