package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.domain.HzHouseOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 选房预订单Mapper接口
 *
 * @author ruoyi
 */
@Mapper
public interface HzHouseOrderMapper extends BaseMapper<HzHouseOrder> {

    /**
     * 原子更新房源状态（并发安全锁定）
     * 只有 house_status='0'（空置）时才能更新，返回影响行数
     */
    @Update("UPDATE hz_house SET house_status='1' WHERE house_id=#{houseId} AND house_status='0'")
    int lockHouse(@Param("houseId") Long houseId);

    /**
     * 原子释放房源（预定中→空置）
     */
    @Update("UPDATE hz_house SET house_status='0' WHERE house_id=#{houseId} AND house_status='1'")
    int releaseHouse(@Param("houseId") Long houseId);

    /**
     * 查询待过期的订单（lock_expire_time 已过且状态为 0 或 1）
     */
    List<HzHouseOrder> selectExpiredOrders();
}
