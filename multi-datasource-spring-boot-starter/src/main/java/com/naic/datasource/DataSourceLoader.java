package com.naic.datasource;

import com.alibaba.druid.pool.DruidDataSource;
import com.naic.datasource.bean.DynamicDataSource;
import com.naic.datasource.config.MyBatisPlusConfig;
import com.naic.datasource.constant.DataSourceConstant;
import com.naic.datasource.entity.Tenant;
import com.naic.datasource.mapper.TenantMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Value("${database.name}")
    private String databaseName;


    @PostConstruct
    public void initDataSource() {
        Map<Object, Object> dataSourceMap = new HashMap<>();
        dataSourceMap.put("master", defaultDataSource);
        List<Tenant> tenants = tenantMapper.selectList(null);
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
        List<Tenant> tenants = tenantMapper.selectList(null);
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
