package com.ruoyi.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.system.domain.HzHouseTypeImage;

import java.util.List;

/**
 * 户型图片Service接口
 *
 * @author ruoyi
 * @date 2025-11-18
 */
public interface IHzHouseTypeImageService extends IService<HzHouseTypeImage>
{
    /**
     * 查询户型图片列表
     *
     * @param houseTypeId 户型ID
     * @return 户型图片列表
     */
    List<HzHouseTypeImage> selectImageListByHouseTypeId(Long houseTypeId);

    /**
     * 批量保存户型图片
     *
     * @param houseTypeId 户型ID
     * @param imageList 图片列表
     * @return 结果
     */
    int batchSaveImages(Long houseTypeId, List<HzHouseTypeImage> imageList);

    /**
     * 删除户型图片
     *
     * @param imageId 图片ID
     * @return 结果
     */
    int deleteImageById(Long imageId);

    /**
     * 删除户型的所有图片
     *
     * @param houseTypeId 户型ID
     * @return 结果
     */
    int deleteImagesByHouseTypeId(Long houseTypeId);
}
