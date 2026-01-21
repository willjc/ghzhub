package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.system.domain.HzHouseTypeImage;
import com.ruoyi.system.mapper.HzHouseTypeImageMapper;
import com.ruoyi.system.service.IHzHouseTypeImageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 户型图片Service业务层处理
 *
 * @author ruoyi
 * @date 2025-11-18
 */
@Service
public class HzHouseTypeImageServiceImpl extends ServiceImpl<HzHouseTypeImageMapper, HzHouseTypeImage> implements IHzHouseTypeImageService
{
    /**
     * 查询户型图片列表
     *
     * @param houseTypeId 户型ID
     * @return 户型图片列表
     */
    @Override
    public List<HzHouseTypeImage> selectImageListByHouseTypeId(Long houseTypeId)
    {
        return this.baseMapper.selectImageListByHouseTypeId(houseTypeId);
    }

    /**
     * 批量保存户型图片
     *
     * @param houseTypeId 户型ID
     * @param imageList 图片列表
     * @return 结果
     */
    @Override
    @Transactional
    public int batchSaveImages(Long houseTypeId, List<HzHouseTypeImage> imageList)
    {
        // 先删除原有图片(逻辑删除)
        deleteImagesByHouseTypeId(houseTypeId);

        // 批量插入新图片
        if (imageList != null && !imageList.isEmpty())
        {
            for (HzHouseTypeImage image : imageList)
            {
                image.setHouseTypeId(houseTypeId);
                image.setDelFlag("0");
                this.save(image);
            }
            return imageList.size();
        }
        return 0;
    }

    /**
     * 删除户型图片
     *
     * @param imageId 图片ID
     * @return 结果
     */
    @Override
    public int deleteImageById(Long imageId) {
        LambdaUpdateWrapper<HzHouseTypeImage> wrapper = new LambdaUpdateWrapper<>();
        wrapper.set(HzHouseTypeImage::getDelFlag, "2")
               .eq(HzHouseTypeImage::getImageId, imageId)
               .eq(HzHouseTypeImage::getDelFlag, "0");
        return this.update(wrapper) ? 1 : 0;
    }

    /**
     * 删除户型的所有图片
     *
     * @param houseTypeId 户型ID
     * @return 结果
     */
    @Override
    public int deleteImagesByHouseTypeId(Long houseTypeId)
    {
        LambdaUpdateWrapper<HzHouseTypeImage> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(HzHouseTypeImage::getHouseTypeId, houseTypeId)
               .set(HzHouseTypeImage::getDelFlag, "2");
        return this.update(wrapper) ? 1 : 0;
    }
}
