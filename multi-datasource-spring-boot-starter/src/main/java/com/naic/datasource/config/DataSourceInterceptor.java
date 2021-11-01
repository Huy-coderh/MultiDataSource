package com.naic.datasource.config;

import com.naic.datasource.TenantContextHolder;
import com.naic.datasource.constant.DataSourceConstant;
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
    @Override
    public boolean preHandle(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response, @Nonnull Object handler) {
        String tenantId = request.getHeader(DataSourceConstant.TENANT);
        if (! StringUtils.isEmpty(tenantId)){
            TenantContextHolder.setTenantKey(tenantId);
        }
        return true;
    }
}
