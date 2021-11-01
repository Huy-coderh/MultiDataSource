package com.naic.datasource.config;

import com.naic.datasource.TenantContextHolder;
import com.naic.datasource.constant.DataSourceConstant;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * springboot interceptors configuration class
 * @author HuZhenSha
 * @since 2021/11/1
 */
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
public class WebfluxInterceptorConfiguration implements WebFilter {
    @Override
    @Nonnull
    public Mono<Void> filter(@Nonnull ServerWebExchange exchange, @Nonnull WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String tenantId = null;
        List<String> tenantHeader = request.getHeaders().get(DataSourceConstant.TENANT);
        if (tenantHeader != null){
            tenantId = tenantHeader.get(0);
        }
        if (! StringUtils.isEmpty(tenantId)){
            TenantContextHolder.setTenantKey(tenantId);
        }
        return chain.filter(exchange);
    }
}
