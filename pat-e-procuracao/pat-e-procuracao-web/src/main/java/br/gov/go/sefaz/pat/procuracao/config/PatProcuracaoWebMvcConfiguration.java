package br.gov.go.sefaz.pat.procuracao.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import br.gov.go.sefaz.pat.web.config.PatWebMvcConfiguration;

@Configuration
@EnableWebMvc
@EnableSpringDataWebSupport
@EnableGlobalMethodSecurity(prePostEnabled = true)
@ComponentScan(basePackages = {WebProcuracaoConfigurationParameters.PACKAGE_CONTROLLER, WebProcuracaoConfigurationParameters.PACKAGE_VALIDATION, 
		WebProcuracaoConfigurationParameters.PACKAGE_REST_CLIENT})
public class PatProcuracaoWebMvcConfiguration extends PatWebMvcConfiguration {
 
    @Bean(name = "messageSource")
    public ReloadableResourceBundleMessageSource getMessageSource() {
        ReloadableResourceBundleMessageSource resource = new ReloadableResourceBundleMessageSource();
        resource.setBasename("classpath:/br/gov/go/sefaz/pat/procuracao/messages");
        resource.setDefaultEncoding("UTF-8");
        return resource;
    }    
}