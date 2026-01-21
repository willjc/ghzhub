package com.ruoyi.common.utils;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.page.PageDomain;
import com.ruoyi.common.core.page.TableSupport;

/**
 * 分页工具类 (MyBatis-Plus 版本)
 *
 * @author ruoyi
 */
public class PageUtils
{
    /**
     * 获取分页参数，用于 MyBatis-Plus 分页
     *
     * @return Page 对象
     */
    public static <T> Page<T> getPage()
    {
        PageDomain pageDomain = TableSupport.buildPageRequest();
        Integer pageNum = pageDomain.getPageNum();
        Integer pageSize = pageDomain.getPageSize();

        // 使用默认值：第1页，每页10条
        if (pageNum == null || pageNum <= 0) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize <= 0) {
            pageSize = 10;
        }

        return new Page<>(pageNum, pageSize);
    }

    /**
     * 设置请求分页数据
     * 保留此方法以兼容现有代码
     */
    public static void startPage()
    {
        // MyBatis-Plus 分页通过 Page 对象传递，不需要 ThreadLocal
        // 保留此方法以兼容现有代码
    }

    /**
     * 清理分页的线程变量
     * 保留此方法以兼容现有代码
     */
    public static void clearPage()
    {
        // MyBatis-Plus 不需要清理 ThreadLocal
    }
}
