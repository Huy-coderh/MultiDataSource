package com.naic.datasource.config;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.extension.plugins.pagination.optimize.JsqlParserCountOptimize;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.naic.datasource.bean.DynamicDataSource;
import org.apache.ibatis.plugin.Interceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author HuZhenSha
 * @date 2021/4/29 10:48
 */
@MapperScan("com.naic.datasource.mapper")
public class MyBatisPlusConfig {


    @Bean("master")
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource master() {
        return DruidDataSourceBuilder.create().build();
    }

    @Bean("dynamicDataSource")
    public DynamicDataSource dynamicDataSource(@Qualifier("master")DataSource dataSource) {
        DynamicDataSource dynamicDataSource = new DynamicDataSource();
        Map<Object, Object> dataSourceMap = new HashMap<>();
        dataSourceMap.put("master", dataSource);
        // 将 master 数据源作为默认指定的数据源
        //dynamicDataSource.setDefaultTargetDataSource(dataSource);
        // 将 master 和 slave 数据源作为指定的数据源
        dynamicDataSource.setTargetDataSources(dataSourceMap);
        return dynamicDataSource;
    }
    @Bean
    public MybatisSqlSessionFactoryBean sqlSessionFactoryBean(@Qualifier("dynamicDataSource") DynamicDataSource dataSource,
                                                              @Qualifier("paginationInterceptor") PaginationInterceptor paginationInterceptor) throws Exception {

        MybatisSqlSessionFactoryBean sessionFactory = new MybatisSqlSessionFactoryBean();
        // 重点，使分页插件生效
        Interceptor[] plugins = new Interceptor[1];
        plugins[0] = paginationInterceptor;
        sessionFactory.setPlugins(plugins);
        //配置数据源，此处配置为关键配置，如果没有将 dynamicDataSource作为数据源则不能实现切换
        sessionFactory.setDataSource(dataSource);
        // 扫描Model
        //sessionFactory.setTypeAliasesPackage("com.naic.datasource.mapper.*");
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        // 扫描映射文件
        sessionFactory.setMapperLocations(resolver.getResources("classpath*:mapper/**/*Mapper.xml"));
        return sessionFactory;
    }

    @Bean
    public PlatformTransactionManager transactionManager(@Qualifier("dynamicDataSource") DataSource dataSource) {
        // 配置事务管理, 使用事务时在方法头部添加@Transactional注解即可
        return new DataSourceTransactionManager(dataSource);
    }

    /**
     * 分页插件
     * @return bean
     */
    @Bean
    public PaginationInterceptor paginationInterceptor(){
        PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
        // 开启 count 的join优化，只针对left join
        paginationInterceptor.setCountSqlParser(new JsqlParserCountOptimize(true));
        return paginationInterceptor;
    }

}
