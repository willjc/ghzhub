package com.ruoyi.framework.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;

/**
 * 全局审计账号只读保护。
 */
@Component
public class AuditReadOnlyInterceptor implements HandlerInterceptor
{
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
    {
        try
        {
            if (!SecurityUtils.isAuditRole())
            {
                return true;
            }
        }
        catch (Exception e)
        {
            return true;
        }

        String method = request.getMethod();
        if ("GET".equals(method) || "HEAD".equals(method) || "OPTIONS".equals(method))
        {
            return true;
        }

        String uri = request.getRequestURI().toLowerCase();
        if (uri.endsWith("/logout") || uri.contains("/export") || uri.contains("/download")
                || uri.contains("/print"))
        {
            return true;
        }

        throw new ServiceException("审计账号仅支持查询、导出和下载", 403);
    }
}
