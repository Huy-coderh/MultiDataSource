package com.naic.datasource.config;

import com.naic.datasource.TenantContextHolder;
import com.naic.datasource.constant.DataSourceConstant;
import feign.RequestInterceptor;
import feign.RequestTemplate;

/**
 * feign interceptor used in rpc communication
 * carry the tenant header
 * @author HuZhenSha
 * @since 2021/10/28
 */
public class FeignInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate template) {
        String tenant = TenantContextHolder.getTenantKey();
        template.header(DataSourceConstant.TENANT, tenant);
    }
}
