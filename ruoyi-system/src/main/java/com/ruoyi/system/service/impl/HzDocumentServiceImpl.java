package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.HzDocument;
import com.ruoyi.system.mapper.HzDocumentMapper;
import com.ruoyi.system.service.IHzDocumentService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 资料文档Service业务层处理
 *
 * @author ruoyi
 */
@Service
public class HzDocumentServiceImpl extends ServiceImpl<HzDocumentMapper, HzDocument> implements IHzDocumentService {

    @Override
    public HzDocument selectDocumentById(Long documentId) {
        return this.getById(documentId);
    }

    @Override
    public List<HzDocument> selectDocumentListByTenantId(Long tenantId) {
        LambdaQueryWrapper<HzDocument> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HzDocument::getTenantId, tenantId)
               .eq(HzDocument::getDelFlag, "0")
               .orderByDesc(HzDocument::getCreateTime);
        return this.list(wrapper);
    }

    @Override
    public List<HzDocument> selectDocumentListByContractId(Long contractId) {
        LambdaQueryWrapper<HzDocument> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HzDocument::getContractId, contractId)
               .eq(HzDocument::getDelFlag, "0")
               .orderByDesc(HzDocument::getCreateTime);
        return this.list(wrapper);
    }

    @Override
    public List<HzDocument> selectDocumentListByTenantIdAndType(Long tenantId, String documentType) {
        LambdaQueryWrapper<HzDocument> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HzDocument::getTenantId, tenantId)
               .eq(HzDocument::getDocumentType, documentType)
               .eq(HzDocument::getDelFlag, "0")
               .orderByDesc(HzDocument::getCreateTime);
        return this.list(wrapper);
    }

    @Override
    public IPage<HzDocument> selectDocumentPage(HzDocument document, int pageNum, int pageSize) {
        Page<HzDocument> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<HzDocument> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(document.getTenantId() != null, HzDocument::getTenantId, document.getTenantId())
               .eq(StringUtils.isNotEmpty(document.getDocumentType()), HzDocument::getDocumentType, document.getDocumentType())
               .eq(StringUtils.isNotEmpty(document.getAuditStatus()), HzDocument::getAuditStatus, document.getAuditStatus())
               .eq(HzDocument::getDelFlag, "0")
               .orderByDesc(HzDocument::getCreateTime);
        return this.page(page, wrapper);
    }

    @Override
    public int insertDocument(HzDocument document) {
        document.setDelFlag("0");
        document.setAuditStatus("0"); // 默认待审核状态
        return this.save(document) ? 1 : 0;
    }

    @Override
    public int updateDocument(HzDocument document) {
        return this.updateById(document) ? 1 : 0;
    }

    @Override
    public int deleteDocumentById(Long documentId) {
        HzDocument document = new HzDocument();
        document.setDocumentId(documentId);
        document.setDelFlag("2");
        return this.updateById(document) ? 1 : 0;
    }
}
