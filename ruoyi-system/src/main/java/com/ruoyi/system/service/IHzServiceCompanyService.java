package com.ruoyi.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.system.domain.HzServiceCompany;

import java.util.List;

/**
 * 服务公司 Service 接口
 *
 * @author ruoyi
 */
public interface IHzServiceCompanyService extends IService<HzServiceCompany>
{
    /**
     * 分页查询
     */
    IPage<HzServiceCompany> selectCompanyPage(HzServiceCompany company, int pageNum, int pageSize);

    /**
     * 列表查询（不分页）
     */
    List<HzServiceCompany> selectCompanyList(HzServiceCompany company);

    /**
     * 按订单类型查询启用中的服务公司（下拉用）
     * orderType: 1=保洁 2=搬家
     * 返回：匹配类型 + 综合类型的启用公司
     */
    List<HzServiceCompany> selectActiveCompaniesByOrderType(String orderType);

    /**
     * 按 ID 查询
     */
    HzServiceCompany selectCompanyById(Long companyId);

    /**
     * 新增
     */
    int insertCompany(HzServiceCompany company);

    /**
     * 修改
     */
    int updateCompany(HzServiceCompany company);

    /**
     * 删除（软删除）
     */
    int deleteCompanyByIds(Long[] companyIds);
}
