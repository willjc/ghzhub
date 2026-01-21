package com.ruoyi.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ruoyi.system.domain.HzDocument;

import java.util.List;

/**
 * 资料文档Service接口
 *
 * @author ruoyi
 */
public interface IHzDocumentService {

    /**
     * 查询资料文档
     *
     * @param documentId 资料文档主键
     * @return 资料文档
     */
    public HzDocument selectDocumentById(Long documentId);

    /**
     * 根据租户ID查询资料文档列表
     *
     * @param tenantId 租户ID
     * @return 资料文档列表
     */
    public List<HzDocument> selectDocumentListByTenantId(Long tenantId);

    /**
     * 根据租户ID和文档类型查询资料文档
     *
     * @param tenantId 租户ID
     * @param documentType 文档类型
     * @return 资料文档列表
     */
    public List<HzDocument> selectDocumentListByTenantIdAndType(Long tenantId, String documentType);

    /**
     * 分页查询资料文档列表
     *
     * @param document 资料文档
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @return 资料文档分页数据
     */
    public IPage<HzDocument> selectDocumentPage(HzDocument document, int pageNum, int pageSize);

    /**
     * 新增资料文档
     *
     * @param document 资料文档
     * @return 结果
     */
    public int insertDocument(HzDocument document);

    /**
     * 修改资料文档
     *
     * @param document 资料文档
     * @return 结果
     */
    public int updateDocument(HzDocument document);

    /**
     * 删除资料文档
     *
     * @param documentId 资料文档主键
     * @return 结果
     */
    public int deleteDocumentById(Long documentId);
}
