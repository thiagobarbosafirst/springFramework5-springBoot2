package br.gov.go.sefaz.pat.procuracao.config;

import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.persistence.SharedCacheMode;
import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.support.PersistenceAnnotationBeanPostProcessor;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.jta.WebLogicJtaTransactionManager;

import br.gov.go.sefaz.javaee.corporativo.config.CorporativoDomainConfigurationParameters;
import br.gov.go.sefaz.javaee.repository.history.jpa.HistoryJpaAspect;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = {WebProcuracaoConfigurationParameters.PACKAGE_REPOSITORY_PROCUR_JPA}, enableDefaultTransactions = false, entityManagerFactoryRef="patProcuracaoEntityManagerFactory")
public class DomainProcuracaoJpaConfiguration {

    @Bean(destroyMethod = "")
    public DataSource patProcuracaoDataSource() {
        JndiDataSourceLookup jndiDataSourceLookup = new JndiDataSourceLookup();
        jndiDataSourceLookup.setResourceRef(true);
        return jndiDataSourceLookup.getDataSource("jdbc/pat");
    }

    @Bean
    public JpaVendorAdapter patProcuracaoJpaVendorAdapter() {
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setDatabase(Database.ORACLE);
        vendorAdapter.setShowSql(true);
        vendorAdapter.setGenerateDdl(false);
        return vendorAdapter;
    }

    @Bean
    public EntityManagerFactory patProcuracaoEntityManagerFactory() {
        LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
        factoryBean.setJtaDataSource(patProcuracaoDataSource());
        factoryBean.setJpaVendorAdapter(patProcuracaoJpaVendorAdapter());
        factoryBean.setJpaProperties(buildJpaProperties());
        factoryBean.setPackagesToScan("br.gov.go.sefaz.pat.model", "br.gov.go.sefaz.javaee.corporativo.model", "br.gov.go.sefaz.javaee.cce.model");
        factoryBean.setSharedCacheMode(SharedCacheMode.ENABLE_SELECTIVE);
        factoryBean.afterPropertiesSet();
        return factoryBean.getObject();
    }

    private Properties buildJpaProperties() {
        Properties properties = new Properties();
        properties.setProperty("hibernate.bytecode.use_reflection_optimizer", "true");
        properties.setProperty("hibernate.cache.provider_class", "net.sf.ehcache.hibernate.SingletonEhCacheProvider");
        properties.setProperty("hibernate.cache.region.factory_class", "org.hibernate.cache.ehcache.SingletonEhCacheRegionFactory");
        properties.setProperty("hibernate.cache.provider_configuration_file_resource_path", "/ehcache.xml");
        properties.setProperty("hibernate.cache.use_query_cache", "true");
        properties.setProperty("hibernate.cache.use_second_level_cache", "true");
        properties.setProperty("hibernate.show_sql", "true");
        properties.setProperty("hibernate.format_sql", "true");
        properties.setProperty("hibernate.hbm2ddl.auto", "none");
        properties.setProperty("hibernate.dialect", "org.hibernate.dialect.Oracle12cDialect");        
        properties.setProperty("hibernate.transaction.jta.platform", "org.hibernate.service.jta.platform.internal.WeblogicJtaPlatform");
        properties.setProperty("hibernate.transaction.manager_lookup_class", "org.hibernate.transaction.WeblogicTransactionManagerLookup");
        properties.setProperty("hibernate.temp.use_jdbc_metadata_defaults", "false");
        properties.setProperty("hibernate.hql.bulk_id_strategy", CorporativoDomainConfigurationParameters.HIBERNATE_BULK_ID_STRATEGY);
        return properties;
    }
    
    @Bean
    public PlatformTransactionManager transactionManager() {
        return new WebLogicJtaTransactionManager();
    }

    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
        return new PersistenceExceptionTranslationPostProcessor();
    }

    @Bean
    public PersistenceAnnotationBeanPostProcessor annotationBean() {
        return new PersistenceAnnotationBeanPostProcessor();
    }
    
    @Bean 
  	public HistoryJpaAspect historyJpaAspect() {
	  return new HistoryJpaAspect();
  	}
}