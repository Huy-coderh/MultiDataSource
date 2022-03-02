package com.github.xingren.datasource.config;


import com.github.xingren.datasource.TenantContextHolder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * spring interceptor which is used to read the tenant id
 * @author HuZhenSha
 * @since 2021/10/28
 */
public class DataSourceInterceptor implements HandlerInterceptor {

    @Value("${multi-datasource.app.tenant-key}")
    private String tenantKey;

    @Override
    public boolean preHandle(HttpServletRequest request, @Nonnull HttpServletResponse response, @Nonnull Object handler) {
        String tenantId = request.getHeader(tenantKey);
        if (! StringUtils.isEmpty(tenantId)){
            TenantContextHolder.setTenantKey(tenantId);
        }
        return true;
    }
}
