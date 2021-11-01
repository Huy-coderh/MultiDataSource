package com.naic.datasource.bean;

import com.alibaba.druid.pool.DruidDataSource;
import com.naic.datasource.DataSourceLoader;
import com.naic.datasource.TenantContextHolder;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.util.Assert;

import javax.annotation.Nonnull;
import javax.sql.DataSource;
import java.util.Map;


/**
 * @author HuZhenSha
 * @since 2021/10/26
 */
public class DynamicDataSource extends AbstractRoutingDataSource {

    /**
     * 用于保存租户key和数据源的映射关系，目标数据源map的拷贝
     * 因为父类该map为peivate且没有提供get方法
     */
    private Map<Object, Object> targetDataSources;

    @Override
    public void setDefaultTargetDataSource(@Nonnull Object defaultTargetDataSource) {
        super.setDefaultTargetDataSource(defaultTargetDataSource);
    }

    @Override
    public void setTargetDataSources(@Nonnull Map<Object, Object> targetDataSources) {
        this.targetDataSources = targetDataSources;
        super.setTargetDataSources(targetDataSources);
    }

    public Map<Object, Object> getTargetDataSources() {
        return targetDataSources;
    }

    public void addDataSource(Object key, Object dataSource){
        this.targetDataSources.put(key, dataSource);
        super.setTargetDataSources(targetDataSources);
        super.afterPropertiesSet();
    }

    @Override
    @Nonnull
    protected DataSource determineTargetDataSource() {
        Assert.notNull(this.targetDataSources, "DataSource router not initialized");
        DruidDataSource dataSource;
        try {
            dataSource = (DruidDataSource) super.determineTargetDataSource();
        } catch (IllegalStateException exception){
            // 重新加载数据源
            DataSourceLoader loader = ApplicationContextProvider.getBean(DataSourceLoader.class);
            loader.addDataSource();
            dataSource = (DruidDataSource) super.determineTargetDataSource();
        }
        return dataSource;
    }

    @Override
    protected Object determineCurrentLookupKey() {
        return TenantContextHolder.getTenantKey();
    }

    @Override
    public void afterPropertiesSet() {
        super.afterPropertiesSet();
    }
}
