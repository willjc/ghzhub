package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.domain.HzEnterpriseBatch;
import com.ruoyi.system.domain.HzEnterpriseBatchHouse;
import com.ruoyi.system.mapper.HzEnterpriseBatchHouseMapper;
import com.ruoyi.system.mapper.HzEnterpriseBatchMapper;
import com.ruoyi.system.service.IHzEnterpriseBatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 企业批次Service业务层处理
 *
 * @author ruoyi
 */
@Service
public class HzEnterpriseBatchServiceImpl extends ServiceImpl<HzEnterpriseBatchMapper, HzEnterpriseBatch>
        implements IHzEnterpriseBatchService {

    @Autowired
    private HzEnterpriseBatchMapper enterpriseBatchMapper;

    @Autowired
    private HzEnterpriseBatchHouseMapper enterpriseBatchHouseMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 查询企业批次列表
     *
     * @param enterpriseBatch 企业批次
     * @return 企业批次
     */
    @Override
    public List<HzEnterpriseBatch> selectEnterpriseBatchList(HzEnterpriseBatch enterpriseBatch) {
        return enterpriseBatchMapper.selectEnterpriseBatchList(enterpriseBatch);
    }

    /**
     * 查询企业批次详情
     *
     * @param batchId 企业批次主键
     * @return 企业批次
     */
    @Override
    public HzEnterpriseBatch selectEnterpriseBatchById(Long batchId) {
        return enterpriseBatchMapper.selectEnterpriseBatchById(batchId);
    }

    /**
     * 新增企业批次
     *
     * @param enterpriseBatch 企业批次
     * @return 结果
     */
    @Override
    @Transactional
    public int insertEnterpriseBatch(HzEnterpriseBatch enterpriseBatch) {
        enterpriseBatch.setCreateTime(DateUtils.getNowDate());
        return enterpriseBatchMapper.insert(enterpriseBatch);
    }

    /**
     * 修改企业批次
     *
     * @param enterpriseBatch 企业批次
     * @return 结果
     */
    @Override
    @Transactional
    public int updateEnterpriseBatch(HzEnterpriseBatch enterpriseBatch) {
        enterpriseBatch.setUpdateTime(DateUtils.getNowDate());
        return enterpriseBatchMapper.updateById(enterpriseBatch);
    }

    /**
     * 删除企业批次信息
     *
     * @param batchId 企业批次主键
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteEnterpriseBatchById(Long batchId) {
        // 逻辑删除批次
        LambdaUpdateWrapper<HzEnterpriseBatch> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(HzEnterpriseBatch::getBatchId, batchId)
                .set(HzEnterpriseBatch::getDelFlag, "2");

        if (!this.update(updateWrapper)) {
            return 0;
        }

        // 同时逻辑删除关联的房源分配记录
        LambdaUpdateWrapper<HzEnterpriseBatchHouse> houseUpdateWrapper = new LambdaUpdateWrapper<>();
        houseUpdateWrapper.eq(HzEnterpriseBatchHouse::getBatchId, batchId)
                .eq(HzEnterpriseBatchHouse::getDelFlag, "0")
                .set(HzEnterpriseBatchHouse::getDelFlag, "2");
        enterpriseBatchHouseMapper.update(null, houseUpdateWrapper);

        return 1;
    }

    /**
     * 批量删除企业批次
     *
     * @param batchIds 需要删除的企业批次主键
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteEnterpriseBatchByIds(Long[] batchIds) {
        int count = 0;
        for (Long batchId : batchIds) {
            count += deleteEnterpriseBatchById(batchId);
        }
        return count;
    }

    /**
     * 保存企业批次及房源关联
     *
     * @param enterpriseBatch 企业批次
     * @param houseIds 房源ID列表
     * @return 结果
     */
    @Override
    @Transactional
    public int saveEnterpriseBatchWithHouses(HzEnterpriseBatch enterpriseBatch, List<Long> houseIds) {
        // 生成批次编号
        if (enterpriseBatch.getBatchNo() == null || enterpriseBatch.getBatchNo().isEmpty()) {
            enterpriseBatch.setBatchNo(generateBatchNo());
        }

        int result;
        if (enterpriseBatch.getBatchId() != null) {
            // 修改批次
            enterpriseBatch.setUpdateTime(DateUtils.getNowDate());
            result = enterpriseBatchMapper.updateById(enterpriseBatch);

            // 删除旧的房源关联
            LambdaUpdateWrapper<HzEnterpriseBatchHouse> deleteWrapper = new LambdaUpdateWrapper<>();
            deleteWrapper.eq(HzEnterpriseBatchHouse::getBatchId, enterpriseBatch.getBatchId())
                    .set(HzEnterpriseBatchHouse::getDelFlag, "2");
            enterpriseBatchHouseMapper.update(null, deleteWrapper);
        } else {
            // 新增批次
            enterpriseBatch.setCreateTime(DateUtils.getNowDate());
            enterpriseBatch.setApproveStatus("0");
            enterpriseBatch.setBatchStatus("0");
            enterpriseBatch.setDelFlag("0");
            result = enterpriseBatchMapper.insert(enterpriseBatch);
        }

        // 保存房源关联
        if (result > 0 && houseIds != null && !houseIds.isEmpty()) {
            for (Long houseId : houseIds) {
                HzEnterpriseBatchHouse batchHouse = new HzEnterpriseBatchHouse();
                batchHouse.setBatchId(enterpriseBatch.getBatchId());
                batchHouse.setHouseId(houseId);
                batchHouse.setHouseCode("HOUSE-" + houseId);
                batchHouse.setAllocationStatus("1");
                batchHouse.setAllocationTime(DateUtils.getNowDate());
                batchHouse.setDelFlag("0");
                batchHouse.setCreateBy(SecurityUtils.getUsername());
                batchHouse.setCreateTime(DateUtils.getNowDate());
                enterpriseBatchHouseMapper.insert(batchHouse);
            }
        }

        return result;
    }

    /**
     * 查询企业批次关联的房源列表
     *
     * @param batchId 批次ID
     * @return 房源列表
     */
    @Override
    public List<Map<String, Object>> getBatchHouses(Long batchId) {
        // 直接使用hz_house表的house_type_name字段，因为house_type_id可能为null导致JOIN失败
        String sql = "SELECT h.house_id AS houseId, h.house_code AS houseCode, h.house_no AS houseNo, h.floor, " +
                "h.house_type_name AS houseTypeName, h.area, h.rent_price AS rentPrice " +
                "FROM hz_enterprise_batch_house bh " +
                "LEFT JOIN hz_house h ON bh.house_id = h.house_id " +
                "WHERE bh.batch_id = ? AND bh.del_flag = '0' AND h.del_flag = '0'";
        return jdbcTemplate.queryForList(sql, batchId);
    }

    /**
     * 生成批次编号（格式：EP + 日期 + 时间 + 随机数）
     *
     * @return 批次编号
     */
    private String generateBatchNo() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String dateTime = sdf.format(new Date());
        // 生成3位随机数确保唯一性
        int randomNum = (int) (Math.random() * 1000);
        return String.format("EP%s%03d", dateTime, randomNum);
    }

    @Override
    public IPage<HzEnterpriseBatch> selectEnterpriseBatchPage(Page<HzEnterpriseBatch> page, HzEnterpriseBatch enterpriseBatch) {
        return enterpriseBatchMapper.selectEnterpriseBatchPage(page, enterpriseBatch);
    }
}
