package com.github.xingren.datasource.config;

import com.github.xingren.datasource.TenantContextHolder;
import com.github.xingren.datasource.constant.DataSourceConstant;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.beans.factory.annotation.Value;

/**
 * feign interceptor used in rpc communication
 * carry the tenant header
 * @author HuZhenSha
 * @since 2021/10/28
 */
public class FeignInterceptor implements RequestInterceptor {

    @Value("${multi-datasource.app.tenant-key}")
    private String tenantKey;

    @Override
    public void apply(RequestTemplate template) {
        String tenant = TenantContextHolder.getTenantKey();
        template.header(tenantKey, tenant);
    }
}
