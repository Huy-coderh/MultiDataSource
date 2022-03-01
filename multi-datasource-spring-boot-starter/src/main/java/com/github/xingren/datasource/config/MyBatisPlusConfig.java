package com.github.xingren.datasource.config;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.core.incrementer.IKeyGenerator;
import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.baomidou.mybatisplus.core.injector.ISqlInjector;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.extension.plugins.pagination.optimize.JsqlParserCountOptimize;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.github.xingren.datasource.bean.DynamicDataSource;
import org.apache.ibatis.plugin.Interceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author HuZhenSha
 * @date 2021/4/29 10:48
 */
@MapperScan("com.naic.com.github.xingren.datasource.mapper")
@ComponentScan("com.naic.com.github.xingren.datasource")
public class MyBatisPlusConfig {

    private final ApplicationContext applicationContext;

    public MyBatisPlusConfig(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Bean("master")
    @Primary
    @ConfigurationProperties(prefix = "spring.com.github.xingren.datasource")
    public DataSource master() {
        return DruidDataSourceBuilder.create().build();
    }

    @Bean("dynamicDataSource")
    public DynamicDataSource dynamicDataSource(@Qualifier("master") DataSource dataSource) {
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
        //sessionFactory.setTypeAliasesPackage("com.naic.com.github.xingren.datasource.mapper.*");
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        // 扫描映射文件
        sessionFactory.setMapperLocations(resolver.getResources("classpath*:mapper/**/*Mapper.xml"));

        // 配置其他配置
        GlobalConfig globalConfig = new GlobalConfig();
        // 注入填充器
        this.getBeanThen(MetaObjectHandler.class, globalConfig::setMetaObjectHandler);
        // 注入主键生成器
        this.getBeanThen(IKeyGenerator.class, i -> globalConfig.getDbConfig().setKeyGenerator(i));
        // 注入sql注入器
        this.getBeanThen(ISqlInjector.class, globalConfig::setSqlInjector);
        // 注入ID生成器
        this.getBeanThen(IdentifierGenerator.class, globalConfig::setIdentifierGenerator);
        sessionFactory.setGlobalConfig(globalConfig);

        return sessionFactory;
    }

    @Bean
    public PlatformTransactionManager transactionManager(@Qualifier("dynamicDataSource") DataSource dataSource) {
        // 配置事务管理, 使用事务时在方法头部添加@Transactional注解即可
        return new DataSourceTransactionManager(dataSource);
    }

    /**
     * 分页插件
     *
     * @return bean
     */
    @Bean
    public PaginationInterceptor paginationInterceptor() {
        PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
        // 开启 count 的join优化，只针对left join
        paginationInterceptor.setCountSqlParser(new JsqlParserCountOptimize(true));
        return paginationInterceptor;
    }

    /**
     * 检查spring容器里是否有对应的bean,有则进行消费
     *
     * @param clazz    class
     * @param consumer 消费
     * @param <T>      泛型
     */
    private <T> void getBeanThen(Class<T> clazz, Consumer<T> consumer) {
        if (this.applicationContext.getBeanNamesForType(clazz, false, false).length > 0) {
            consumer.accept(this.applicationContext.getBean(clazz));
        }
    }

}
