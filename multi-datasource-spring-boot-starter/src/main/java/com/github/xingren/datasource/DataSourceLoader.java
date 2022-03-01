package com.github.xingren.datasource;

import com.alibaba.druid.pool.DruidDataSource;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.xingren.datasource.bean.DynamicDataSource;
import com.github.xingren.datasource.config.MyBatisPlusConfig;
import com.github.xingren.datasource.constant.DataSourceConstant;
import com.github.xingren.datasource.entity.Tenant;
import com.github.xingren.datasource.mapper.TenantMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author HuZhenSha
 * @since 2021/10/28
 */

@AutoConfigureAfter(MyBatisPlusConfig.class)
public class DataSourceLoader {

    private final DynamicDataSource dynamicDataSource;
    private final DataSource defaultDataSource;
    private final TenantMapper tenantMapper;

    public DataSourceLoader(DynamicDataSource dynamicDataSource, DataSource defaultDataSource, TenantMapper tenantMapper) {
        this.dynamicDataSource = dynamicDataSource;
        this.defaultDataSource = defaultDataSource;
        this.tenantMapper = tenantMapper;
    }

    @Value("${multi-datasource.database.name}")
    private String databaseName;

    @Value("${multi-datasource.app.id}")
    private Long appId;


    @PostConstruct
    public void initDataSource() {
        Map<Object, Object> dataSourceMap = new HashMap<>();
        dataSourceMap.put("master", defaultDataSource);
        List<Tenant> tenants = tenantMapper.selectList(Wrappers.<Tenant>lambdaQuery()
                .eq(Tenant::getAppId, appId))
                .stream().filter(Tenant::getStatus).collect(Collectors.toList());
        tenants.forEach(tenant -> {
            if (tenant.getStatus()){
                DruidDataSource source = new DruidDataSource();
                source.setUrl(DataSourceConstant.PREFIX + tenant.getHost() + ":" + tenant.getPort() + "/" + databaseName + DataSourceConstant.SUFFIX);
                source.setDriverClassName(tenant.getDriver());
                source.setUsername(tenant.getUsername());
                source.setPassword(tenant.getPassword());
                dataSourceMap.put(tenant.getTenantId().toString(), source);
            }
        });
        //设置数据源
        dynamicDataSource.setTargetDataSources(dataSourceMap);
        dynamicDataSource.afterPropertiesSet();
    }

    public void addDataSource(){
        Object key = TenantContextHolder.getTenantKey();
        // 切换到主数据源
        TenantContextHolder.setTenantKey("master");
        List<Tenant> tenants = tenantMapper.selectList(Wrappers.<Tenant>lambdaQuery()
                .eq(Tenant::getAppId, appId))
                .stream().filter(Tenant::getStatus).collect(Collectors.toList());
        tenants.forEach(tenant -> {
            if (tenant.getStatus() && ! dynamicDataSource.getTargetDataSources().containsKey(tenant.getTenantId())){
                DruidDataSource source = new DruidDataSource();
                source.setUrl(DataSourceConstant.PREFIX + tenant.getHost() + ":" + tenant.getPort() + "/" + databaseName + DataSourceConstant.SUFFIX);
                source.setDriverClassName(tenant.getDriver());
                source.setUsername(tenant.getUsername());
                source.setPassword(tenant.getPassword());
                dynamicDataSource.addDataSource(tenant.getTenantId().toString(), source);
            }
        });
        TenantContextHolder.setTenantKey(key.toString());
    }

}
