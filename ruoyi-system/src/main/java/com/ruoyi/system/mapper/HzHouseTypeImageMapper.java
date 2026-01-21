package com.ruoyi.system.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.domain.HzHouseTypeImage;
import java.util.List;

/**
 * 户型图片Mapper接口
 *
 * @author ruoyi
 * @date 2025-11-18
 */
@Mapper
public interface HzHouseTypeImageMapper extends BaseMapper<HzHouseTypeImage>
{
    /**
     * 查询户型图片列表
     *
     * @param houseTypeId 户型ID
     * @return 户型图片列表
     */
    List<HzHouseTypeImage> selectImageListByHouseTypeId(Long houseTypeId);
}
