package com.github.xingren.datasource.config;

import com.github.xingren.datasource.TenantContextHolder;
import com.github.xingren.datasource.constant.DataSourceConstant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.core.annotation.Order;
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
@Order(-999)
public class WebfluxInterceptorConfiguration implements WebFilter {

    @Value("${multi-datasource.app.tenant-key}")
    private String tenantKey;

    @Override
    @Nonnull
    public Mono<Void> filter(@Nonnull ServerWebExchange exchange, @Nonnull WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String tenantId = null;
        List<String> tenantHeader = request.getHeaders().get(tenantKey);
        if (tenantHeader != null){
            tenantId = tenantHeader.get(0);
        }
        if (! StringUtils.isEmpty(tenantId)){
            TenantContextHolder.setTenantKey(tenantId);
        }
        return chain.filter(exchange);
    }
}
